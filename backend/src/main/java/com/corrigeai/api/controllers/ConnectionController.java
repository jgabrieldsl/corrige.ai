package com.corrigeai.api.controllers;

import com.corrigeai.api.models.ConnectRequest;
import com.corrigeai.api.models.ConnectResponse;
import com.corrigeai.api.models.SocketResponse;
import com.corrigeai.api.repositories.SocketResponseRepository;
import com.corrigeai.api.services.ServerCommunicationService;
import com.corrigeai.api.services.SocketConnectionManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConnectionController {
    @Autowired
    private ServerCommunicationService serverCommunicationService;

    @Autowired
    private SocketConnectionManager connectionManager;

    @Autowired
    private SocketResponseRepository socketResponseRepository;

    @PostMapping("/connect")
    @NonNull
    public ResponseEntity<ConnectResponse> establishConnection(@RequestBody @NonNull ConnectRequest request) {        
        ConnectResponse response = serverCommunicationService.handleConnection(request);
        
        // Verifica o tipo de resposta para determinar o status HTTP
        if (ConnectResponse.CONNECT_SUCCESS.equals(response.getTipo())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/connections")
    public ResponseEntity<List<SocketResponse>> getConnections() {
        try {
            List<SocketResponse> connections = socketResponseRepository.findAll();
            return ResponseEntity.ok(connections);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/connections/{socketId}")
    public ResponseEntity<?> disconnect(@PathVariable String socketId) {
        try {
            connectionManager.disconnect(socketId);
            return ResponseEntity.ok(new SuccessResponse("Desconectado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Erro ao desconectar: " + e.getMessage()));
        }
    }

    @SuppressWarnings("unused") // getMessage() é usado pelo Jackson para serialização JSON
    private static class SuccessResponse {
        @JsonProperty("message")
        private final String message;

        public SuccessResponse(String message) { this.message = message; }

        public String getMessage() { return message; }
    }

    @SuppressWarnings("unused") // getMessage() é usado pelo Jackson para serialização JSON
    private static class ErrorResponse {
        @JsonProperty("message")
        private final String message;

        public ErrorResponse(String message) { this.message = message; }

        public String getMessage() { return message; }
    }
}