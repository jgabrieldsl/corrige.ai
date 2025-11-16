package com.corrigeai.api.models;

import lombok.Data;

@Data
public class ConnectResponse {
    public static final String CONNECT_SUCCESS = "CONNECT_SUCCESS";
    public static final String CONNECT_ERROR = "CONNECT_ERROR";
    
    private String tipo = CONNECT_SUCCESS;
    private ConnectResponseData dados;
    private String mensagem;
    
    // Construtor padrão
    public ConnectResponse() {}
    
    // Construtor para sucesso
    public ConnectResponse(ConnectResponseData dados) {
        this.tipo = CONNECT_SUCCESS;
        this.dados = dados;
    }
    
    // Construtor para erro
    public ConnectResponse(String mensagemErro) {
        this.tipo = CONNECT_ERROR;
        this.mensagem = mensagemErro;
    }

    @Data
    public static class ConnectResponseData {
        private String socketId;
        private Long timestamp;
        private Integer totalUsuarios;
    }
}