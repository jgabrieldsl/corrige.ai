package com.corrigeai.api.models; // Verifique se o pacote 'com.corrigeai.backend' está correto

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// @Document diz ao Spring que esta classe deve ser salva na "collection" (tabela) "conexoes"
@Document(collection = "conexoes")
public class RegistroConexao {

    @Id
    private String id;

    private String socketId;
    private Long timestamp;

    // Construtor vazio (obrigatório para o Spring/MongoDB)
    public RegistroConexao() {}

    // Construtor
    public RegistroConexao(String socketId, Long timestamp) {
        this.socketId = socketId;
        this.timestamp = timestamp;
    }


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
}