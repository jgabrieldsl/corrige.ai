package com.corrigeai.api.controllers;

import com.corrigeai.api.models.ConnectRequest;
import com.corrigeai.api.models.ConnectResponse;
import com.corrigeai.api.models.SocketResponse;
import com.corrigeai.api.repositories.SocketResponseRepository;
import com.corrigeai.api.services.ServerCommunicationService;
import com.corrigeai.api.services.SocketConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private ServerCommunicationService serverCommunicationService;

    @Autowired
    private SocketConnectionManager connectionManager;

    @Autowired
    private SocketResponseRepository socketResponseRepository;

    @PostMapping("/test")
    @NonNull
    public ResponseEntity<?> testConnection(@RequestBody @NonNull ConnectRequest request) {
        try {
            logger.info("Recebendo requisição de conexão: {}", request);
            ConnectResponse response = serverCommunicationService.handleConnection(request);
            logger.info("Resposta gerada com sucesso: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao processar requisição de conexão", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Erro ao processar requisição: " + e.getMessage()));
        }
    }

    @GetMapping("/connections")
    public ResponseEntity<List<SocketResponse>> getConnections() {
        try {
            List<SocketResponse> connections = socketResponseRepository.findAll();
            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            logger.error("Erro ao buscar conexões", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/disconnect/{socketId}")
    public ResponseEntity<?> disconnect(@PathVariable String socketId) {
        try {
            logger.info("Desconectando socketId: {}", socketId);
            connectionManager.disconnect(socketId);
            return ResponseEntity.ok(new SuccessResponse("Desconectado com sucesso"));
        } catch (Exception e) {
            logger.error("Erro ao desconectar", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Erro ao desconectar: " + e.getMessage()));
        }
    }

    private static class SuccessResponse {
        private final String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {  
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}