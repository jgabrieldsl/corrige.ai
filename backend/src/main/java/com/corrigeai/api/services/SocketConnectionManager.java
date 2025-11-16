package com.corrigeai.api.services;

import com.corrigeai.servidor.comunicacao.Comunicado;
import com.corrigeai.servidor.comunicacao.PedidoDeConexao;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import com.corrigeai.servidor.comunicacao.PedidoDeMensagem;
import com.corrigeai.servidor.comunicacao.MensagemChat;
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
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3001;
    
    // Pool de conexões ativas por socketId
    private final Map<String, PersistentConnection> connections = new ConcurrentHashMap<>();
    
    // Listeners para mensagens de chat
    private final List<Consumer<MensagemChat>> chatMessageListeners = new ArrayList<>();
    
    /** Registra um listener para mensagens de chat */
    public void addChatMessageListener(Consumer<MensagemChat> listener) {
        chatMessageListeners.add(listener);
    }
    
    /* Notifica todos os listeners sobre uma nova mensagem */
    private void notifyChatMessageListeners(MensagemChat mensagem) throws Exception {
        for (Consumer<MensagemChat> listener : chatMessageListeners) {
            try {
                listener.accept(mensagem);
            } catch (Exception e) {
                throw new Exception("Erro ao notificar listener de mensagem de chat", e);
            }
        }
    }
    
    /* Estabelece uma conexão persistente com o servidor */
    public RespostaDeConexao connect(String userId, String userType, String authToken) throws Exception {
        try {            
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
                        
            // Recebe resposta
            Object resposta = receptor.readObject();
            
            if (!(resposta instanceof RespostaDeConexao)) {
                throw new Exception("Resposta inesperada do servidor");
            }
            
            RespostaDeConexao respostaConexao = (RespostaDeConexao) resposta;
            String socketId = respostaConexao.getSocketId();
                        
            // Cria e armazena conexão persistente
            PersistentConnection connection = new PersistentConnection(
                socketId, userId, socket, transmissor, receptor
            );
            
            connections.put(socketId, connection);
            
            // Inicia thread listener
            connection.startListening();
            
            return respostaConexao;
            
        } catch (Exception e) {
            throw new Exception("Erro ao conectar ao servidor de sockets: " + e.getMessage(), e);
        }
    }
    
    /* Envia uma mensagem através de uma conexão existente */
    public void sendMessage(String socketId, Comunicado mensagem) throws Exception {
        PersistentConnection connection = connections.get(socketId);

        if (connection == null) {
            throw new Exception("Conexão não encontrada: " + socketId);
        }
        
        connection.send(mensagem);
    }
    
    /* Envia uma mensagem de chat */
    public void sendChatMessage(String socketId, String mensagem) throws Exception {
        PersistentConnection connection = connections.get(socketId);
        
        if (connection == null) {
            throw new Exception("Conexão não encontrada: " + socketId);
        }
        
        Map<String, Object> dados = Map.of("mensagem", mensagem);
        PedidoDeMensagem pedido = new PedidoDeMensagem("SEND_MESSAGE", dados);
        
        connection.send(pedido);
    }
    
    /*  Desconecta uma conexão */
    public void disconnect(String socketId) throws Exception {
        PersistentConnection connection = connections.remove(socketId);
        
        if (connection != null) {
            connection.close();
        }
    }
    
    /* Obtém uma conexão por socketId */
    public PersistentConnection getConnection(String socketId) {
        return connections.get(socketId);
    }
    
    /* Classe interna que representa uma conexão persistente */
    public class PersistentConnection {
        private final String socketId;
        private final String userId;
        private final Socket socket;

        private final ObjectOutputStream transmissor;
        private final ObjectInputStream receptor;

        private Thread listenerThread;
        private volatile boolean running = true;
        
        public PersistentConnection(
            String socketId,
            String userId,
            Socket socket,
            ObjectOutputStream transmissor,
            ObjectInputStream receptor
        ) {
            this.socketId = socketId;
            this.userId = userId;
            this.socket = socket;
            this.transmissor = transmissor;
            this.receptor = receptor;
        }
        
        /* Inicia thread que escuta mensagens do servidor */
        public void startListening() {
            listenerThread = new Thread(() -> {
                
                while (running && !socket.isClosed()) {
                    try {
                        Object mensagem = receptor.readObject();
                        
                        handleIncomingMessage(mensagem);
                        
                    } catch (Exception e) { break; }
                }
            });
            
            listenerThread.setDaemon(true);
            listenerThread.start();
        }
        
        /* Envia uma mensagem para o servidor */
        public synchronized void send(Comunicado mensagem) throws Exception {
            try {
                transmissor.writeObject(mensagem);
                transmissor.flush();
            } catch (Exception e) {
                throw e;
            }
        }
        
        /* Processa mensagens recebidas do servidor */
        private void handleIncomingMessage(Object mensagem) throws Exception {
            if (mensagem instanceof MensagemChat) {
                MensagemChat chatMsg = (MensagemChat) mensagem;       
                         
                // Notifica os listeners
                notifyChatMessageListeners(chatMsg);
            }
        }
        
        /* Fecha a conexão */
        public void close() throws Exception {
            running = false;
            
            try {
                if (transmissor != null) transmissor.close();
                if (receptor != null) receptor.close();
                if (socket != null) socket.close();
                
                if (listenerThread != null) {
                    listenerThread.interrupt();
                }
                
            } catch (Exception e) {
                throw new Exception("Erro ao fechar conexão", e);
            }
        }
        
        // Getters
        public String getSocketId() { return socketId; }
        public String getUserId() { return userId; }
        public boolean isConnected() { return running && !socket.isClosed(); }
    }
}
