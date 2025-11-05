package com.corrigeai.api.services;

import com.corrigeai.servidor.comunicacao.PedidoDeConexao;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocketClientService {
    private static final Logger logger = LoggerFactory.getLogger(SocketClientService.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3001;
    
    public Map<String, Object> sendRequest(String tipo, Map<String, Object> dados) throws Exception {
        Socket conexao = null;
        ObjectOutputStream transmissor = null;
        ObjectInputStream receptor = null;
        
        try {
            logger.info("Conectando ao servidor em {}:{}", SERVER_HOST, SERVER_PORT);
            conexao = new Socket(SERVER_HOST, SERVER_PORT);
            
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor = new ObjectInputStream(conexao.getInputStream());
            
            logger.info("Conexão estabelecida, enviando pedido de tipo: {}", tipo);
            
            // Envia o pedido
            PedidoDeConexao pedido = new PedidoDeConexao(tipo, dados);
            transmissor.writeObject(pedido);
            transmissor.flush();
            
            logger.info("Pedido enviado, aguardando resposta...");
            
            // Recebe a resposta
            Object resposta = receptor.readObject();
            
            if (resposta instanceof RespostaDeConexao) {
                RespostaDeConexao respostaConexao = (RespostaDeConexao) resposta;
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("tipo", respostaConexao.getTipo());
                resultado.put("dados", respostaConexao.getDados());
                
                logger.info("Resposta recebida: {}", resultado);
                return resultado;
            } else {
                throw new Exception("Resposta inesperada do servidor");
            }
            
        } catch (Exception e) {
            logger.error("Erro ao comunicar com o servidor de sockets", e);
            throw e;
        } finally {
            try {
                if (transmissor != null) transmissor.close();
                if (receptor != null) receptor.close();
                if (conexao != null) conexao.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar conexão", e);
            }
        }
    }
}
