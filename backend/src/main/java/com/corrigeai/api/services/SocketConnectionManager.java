package com.corrigeai.api.services;

import com.corrigeai.servidor.comunicacao.Comunicado;
import com.corrigeai.servidor.comunicacao.PedidoDeConexao;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import com.corrigeai.servidor.comunicacao.PedidoDeMensagem;
import com.corrigeai.servidor.comunicacao.MensagemChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

@Service
public class SocketConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(SocketConnectionManager.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3001;
    
    // Pool de conexões ativas por socketId
    private final Map<String, PersistentConnection> connections = new ConcurrentHashMap<>();
    
    // Listeners para mensagens de chat
    private final List<Consumer<MensagemChat>> chatMessageListeners = new ArrayList<>();
    
    /**
     * Registra um listener para mensagens de chat
     */
    public void addChatMessageListener(Consumer<MensagemChat> listener) {
        chatMessageListeners.add(listener);
    }
    
    /**
     * Notifica todos os listeners sobre uma nova mensagem
     */
    private void notifyChatMessageListeners(MensagemChat mensagem) {
        for (Consumer<MensagemChat> listener : chatMessageListeners) {
            try {
                listener.accept(mensagem);
            } catch (Exception e) {
                logger.error("Erro ao notificar listener", e);
            }
        }
    }
    
    /**
     * Estabelece uma conexão persistente com o servidor
     */
    public RespostaDeConexao connect(String userId, String userType, String authToken) throws Exception {
        try {
            logger.info("Estabelecendo conexão persistente para userId: {}", userId);
            
            // Cria socket
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            
            // Cria streams
            ObjectOutputStream transmissor = new ObjectOutputStream(socket.getOutputStream());
            transmissor.flush();
            ObjectInputStream receptor = new ObjectInputStream(socket.getInputStream());
            
            // Envia pedido de conexão
            Map<String, Object> dados = Map.of(
                "userId", userId,
                "userType", userType,
                "authToken", authToken
            );
            
            PedidoDeConexao pedido = new PedidoDeConexao("CONNECT", dados);
            transmissor.writeObject(pedido);
            transmissor.flush();
            
            logger.info("Pedido de conexão enviado, aguardando resposta...");
            
            // Recebe resposta
            Object resposta = receptor.readObject();
            
            if (!(resposta instanceof RespostaDeConexao)) {
                throw new Exception("Resposta inesperada do servidor");
            }
            
            RespostaDeConexao respostaConexao = (RespostaDeConexao) resposta;
            String socketId = respostaConexao.getSocketId();
            
            logger.info("Conexão estabelecida! SocketId: {}", socketId);
            
            // Cria e armazena conexão persistente
            PersistentConnection connection = new PersistentConnection(
                socketId, userId, socket, transmissor, receptor
            );
            
            connections.put(socketId, connection);
            
            // Inicia thread listener
            connection.startListening();
            
            return respostaConexao;
            
        } catch (Exception e) {
            logger.error("Erro ao estabelecer conexão persistente", e);
            throw e;
        }
    }
    
    /**
     * Envia uma mensagem através de uma conexão existente
     */
    public void sendMessage(String socketId, Comunicado mensagem) throws Exception {
        PersistentConnection connection = connections.get(socketId);
        
        if (connection == null) {
            throw new Exception("Conexão não encontrada: " + socketId);
        }
        
        connection.send(mensagem);
    }
    
    /**
     * Envia uma mensagem de chat
     */
    public void sendChatMessage(String socketId, String mensagem) throws Exception {
        PersistentConnection connection = connections.get(socketId);
        
        if (connection == null) {
            throw new Exception("Conexão não encontrada: " + socketId);
        }
        
        Map<String, Object> dados = Map.of("mensagem", mensagem);
        PedidoDeMensagem pedido = new PedidoDeMensagem("SEND_MESSAGE", dados);
        
        connection.send(pedido);
        logger.info("Mensagem de chat enviada do socketId: {}", socketId);
    }
    
    /**
     * Desconecta uma conexão
     */
    public void disconnect(String socketId) {
        PersistentConnection connection = connections.remove(socketId);
        
        if (connection != null) {
            connection.close();
            logger.info("Conexão fechada: {}", socketId);
        }
    }
    
    /**
     * Obtém uma conexão por socketId
     */
    public PersistentConnection getConnection(String socketId) {
        return connections.get(socketId);
    }
    
    /**
     * Classe interna que representa uma conexão persistente
     */
    public class PersistentConnection {
        private final String socketId;
        private final String userId;
        private final Socket socket;
        private final ObjectOutputStream transmissor;
        private final ObjectInputStream receptor;
        private Thread listenerThread;
        private volatile boolean running = true;
        
        public PersistentConnection(String socketId, String userId, Socket socket, 
                                   ObjectOutputStream transmissor, ObjectInputStream receptor) {
            this.socketId = socketId;
            this.userId = userId;
            this.socket = socket;
            this.transmissor = transmissor;
            this.receptor = receptor;
        }
        
        /**
         * Inicia thread que escuta mensagens do servidor
         */
        public void startListening() {
            listenerThread = new Thread(() -> {
                logger.info("[LISTENER-{}] Thread iniciada para socketId: {}", userId, socketId);
                
                while (running && !socket.isClosed()) {
                    try {
                        Object mensagem = receptor.readObject();
                        logger.info("[LISTENER-{}] Mensagem recebida: {}", userId, mensagem);
                        
                        // Aqui você pode processar a mensagem recebida
                        // Por exemplo, enviar para um WebSocket do frontend via SSE ou WebSocket
                        handleIncomingMessage(mensagem);
                        
                    } catch (Exception e) {
                        if (running) {
                            logger.error("[LISTENER-{}] Erro ao receber mensagem", userId, e);
                        }
                        break;
                    }
                }
                
                logger.info("[LISTENER-{}] Thread encerrada", userId);
            });
            
            listenerThread.setDaemon(true);
            listenerThread.start();
        }
        
        /**
         * Envia uma mensagem para o servidor
         */
        public synchronized void send(Comunicado mensagem) throws Exception {
            try {
                transmissor.writeObject(mensagem);
                transmissor.flush();
                logger.info("[SENDER-{}] Mensagem enviada: {}", userId, mensagem.getClass().getSimpleName());
            } catch (Exception e) {
                logger.error("[SENDER-{}] Erro ao enviar mensagem", userId, e);
                throw e;
            }
        }
        
        /**
         * Processa mensagens recebidas do servidor
         */
        private void handleIncomingMessage(Object mensagem) {
            if (mensagem instanceof MensagemChat) {
                MensagemChat chatMsg = (MensagemChat) mensagem;
                logger.info("[CHAT-{}] Mensagem recebida de {}: {}", 
                    userId, chatMsg.getUserId(), chatMsg.getMensagem());
                
                // Notifica os listeners
                notifyChatMessageListeners(chatMsg);
            } else {
                logger.info("Processando mensagem: {}", mensagem);
            }
        }
        
        /**
         * Fecha a conexão
         */
        public void close() {
            running = false;
            
            try {
                if (transmissor != null) transmissor.close();
                if (receptor != null) receptor.close();
                if (socket != null) socket.close();
                
                if (listenerThread != null) {
                    listenerThread.interrupt();
                }
                
                logger.info("Conexão fechada com sucesso: {}", socketId);
            } catch (Exception e) {
                logger.error("Erro ao fechar conexão", e);
            }
        }
        
        // Getters
        public String getSocketId() { return socketId; }
        public String getUserId() { return userId; }
        public boolean isConnected() { return running && !socket.isClosed(); }
    }
}
