package com.corrigeai.api.services;

import com.corrigeai.api.models.ConnectRequest;
import com.corrigeai.api.models.ConnectResponse;
import com.corrigeai.api.models.SocketResponse;
import com.corrigeai.api.repositories.SocketResponseRepository;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ServerCommunicationService {    
    @Autowired
    private SocketConnectionManager connectionManager;
    
    @Autowired
    private SocketResponseRepository socketResponseRepository;
    
    @NonNull
    public ConnectResponse handleConnection(@NonNull ConnectRequest request) {
        try {            
            // Estabelece conexão persistente
            RespostaDeConexao respostaConexao = connectionManager.connect(
                request.getDados().getUserId(),
                request.getDados().getUserType(),
                request.getDados().getAuthToken()
            );
            
            String socketId = respostaConexao.getSocketId();
            Long timestamp = respostaConexao.getTimestamp();
            Integer totalUsuarios = respostaConexao.getTotalUsuarios();
                        
            // Salva a resposta no MongoDB
            SocketResponse socketResponse = new SocketResponse(
                socketId,
                timestamp,
                ConnectResponse.CONNECT_SUCCESS,
                totalUsuarios
            );
            socketResponseRepository.save(socketResponse);
            
            // Cria resposta de sucesso
            ConnectResponse.ConnectResponseData data = new ConnectResponse.ConnectResponseData();
            data.setSocketId(socketId);
            data.setTimestamp(timestamp);
            data.setTotalUsuarios(totalUsuarios);
            
            ConnectResponse connectResponse = new ConnectResponse(data);
            return connectResponse;
            
        } catch (Exception e) {
            
            // Cria resposta de erro
            ConnectResponse errorResponse = new ConnectResponse("Erro na comunicação com o servidor: " + e.getMessage());
            return errorResponse;
        }
    }
}