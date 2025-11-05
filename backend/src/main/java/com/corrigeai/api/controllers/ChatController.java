package com.corrigeai.api.controllers;

import com.corrigeai.api.services.SocketConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private SocketConnectionManager connectionManager;
    
    // Mapa de emitters SSE por socketId
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    
    // Cache para deduplicação de mensagens (userId + timestamp)
    private final Map<String, Long> processedMessages = new ConcurrentHashMap<>();
    
    // Flag para garantir que o listener é registrado apenas uma vez
    private volatile boolean listenerRegistered = false;
    
    /**
     * Registra o listener de mensagens uma única vez
     */
    private void ensureListenerRegistered() {
        if (!listenerRegistered) {
            synchronized (this) {
                if (!listenerRegistered) {
                    connectionManager.addChatMessageListener(mensagem -> {
                        try {
                            // Deduplicação: chave única baseada em userId + timestamp
                            String messageKey = mensagem.getUserId() + "_" + mensagem.getTimestamp();
                            
                            // Verifica se já processou esta mensagem
                            if (processedMessages.containsKey(messageKey)) {
                                logger.debug("[BROADCAST] Mensagem duplicada ignorada: {}", messageKey);
                                return;
                            }
                            
                            // Marca como processada
                            processedMessages.put(messageKey, System.currentTimeMillis());
                            
                            // Limpa mensagens antigas (mais de 1 minuto)
                            long now = System.currentTimeMillis();
                            processedMessages.entrySet().removeIf(entry -> 
                                now - entry.getValue() > 60000
                            );
                            
                            logger.info("[BROADCAST] Enviando mensagem para {} emitter(s)", emitters.size());
                            // Envia para todos os clientes conectados
                            for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
                                try {
                                    Map<String, Object> data = Map.of(
                                        "userId", mensagem.getUserId(),
                                        "userType", mensagem.getUserType(),
                                        "mensagem", mensagem.getMensagem(),
                                        "timestamp", mensagem.getTimestamp()
                                    );
                                    entry.getValue().send(SseEmitter.event()
                                            .name("chat-message")
                                            .data(data));
                                    logger.info("[BROADCAST] Mensagem enviada para {}", entry.getKey());
                                } catch (IOException e) {
                                    logger.warn("Erro ao enviar para emitter {}, removendo...", entry.getKey());
                                    emitters.remove(entry.getKey());
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Erro ao processar mensagem de chat", e);
                        }
                    });
                    listenerRegistered = true;
                    logger.info("Listener de mensagens registrado");
                }
            }
        }
    }
    
    /**
     * Endpoint para enviar mensagem de chat
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request) {
        try {
            logger.info("Enviando mensagem do socketId: {}", request.getSocketId());
            connectionManager.sendChatMessage(request.getSocketId(), request.getMensagem());
            return ResponseEntity.ok(new SuccessResponse("Mensagem enviada com sucesso"));
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem", e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Erro ao enviar mensagem: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint SSE para receber mensagens em tempo real
     */
    @GetMapping(value = "/stream/{socketId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessages(@PathVariable String socketId) {
        logger.info("Cliente conectado ao stream de mensagens: {}", socketId);
        
        // Garante que o listener está registrado
        ensureListenerRegistered();
        
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(socketId, emitter);
        
        logger.info("Total de emitters ativos: {}", emitters.size());
        
        emitter.onCompletion(() -> {
            logger.info("Stream completado para socketId: {}", socketId);
            emitters.remove(socketId);
        });
        
        emitter.onTimeout(() -> {
            logger.info("Stream timeout para socketId: {}", socketId);
            emitters.remove(socketId);
        });
        
        emitter.onError((e) -> {
            logger.error("Erro no stream para socketId: {}", socketId, e);
            emitters.remove(socketId);
        });
        
        return emitter;
    }
    
    // DTOs
    private static class SendMessageRequest {
        private String socketId;
        private String mensagem;
        
        public String getSocketId() { return socketId; }
        public void setSocketId(String socketId) { this.socketId = socketId; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
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
