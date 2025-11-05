package com.corrigeai.api.models;

import lombok.Data;

@Data
public class ConnectResponse {
    private String tipo = "CONNECT_SUCCESS";
    private ConnectResponseData dados;

    @Data
    public static class ConnectResponseData {
        private String socketId;
        private Long timestamp;
        private Integer totalUsuarios;
    }
}