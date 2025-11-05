package com.corrigeai.api.services;

import com.corrigeai.api.models.ConnectRequest;
import com.corrigeai.api.models.ConnectResponse;
import com.corrigeai.api.models.SocketResponse;
import com.corrigeai.api.repositories.SocketResponseRepository;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ServerCommunicationService {
    private static final Logger logger = LoggerFactory.getLogger(ServerCommunicationService.class);
    
    @Autowired
    private SocketConnectionManager connectionManager;
    
    @Autowired
    private SocketResponseRepository socketResponseRepository;
    
    @NonNull
    public ConnectResponse handleConnection(@NonNull ConnectRequest request) {
        try {
            logger.info("Estabelecendo conexão persistente");
            
            // Estabelece conexão persistente
            RespostaDeConexao respostaConexao = connectionManager.connect(
                request.getDados().getUserId(),
                request.getDados().getUserType(),
                request.getDados().getAuthToken()
            );
            
            String socketId = respostaConexao.getSocketId();
            Long timestamp = respostaConexao.getTimestamp();
            Integer totalUsuarios = respostaConexao.getTotalUsuarios();
            
            logger.info("Conexão persistente estabelecida: {}", socketId);
            logger.info("Total de usuários conectados: {}", totalUsuarios);
            
            // Salva a resposta no MongoDB
            SocketResponse socketResponse = new SocketResponse(
                socketId,
                timestamp,
                "CONNECT_SUCCESS",
                totalUsuarios
            );
            socketResponseRepository.save(socketResponse);
            logger.info("Resposta salva no banco de dados: {}", socketResponse);
            
            ConnectResponse connectResponse = new ConnectResponse();
            ConnectResponse.ConnectResponseData data = new ConnectResponse.ConnectResponseData();
            data.setSocketId(socketId);
            data.setTimestamp(timestamp);
            data.setTotalUsuarios(totalUsuarios);
            connectResponse.setDados(data);
            
            logger.info("Resposta processada com sucesso: {}", connectResponse);
            return connectResponse;
        } catch (Exception e) {
            logger.error("Erro na comunicação com o servidor", e);
            throw new RuntimeException("Erro na comunicação com o servidor", e);
        }
    }
}