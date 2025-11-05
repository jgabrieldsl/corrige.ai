package com.corrigeai.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "socket_responses")
public class SocketResponse {
    @Id
    private String id;
    private String socketId;
    private Long timestamp;
    private String tipo;
    private Integer totalUsuarios;

    public SocketResponse() {
    }

    public SocketResponse(String socketId, Long timestamp, String tipo, Integer totalUsuarios) {
        this.socketId = socketId;
        this.timestamp = timestamp;
        this.tipo = tipo;
        this.totalUsuarios = totalUsuarios;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(Integer totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }
}