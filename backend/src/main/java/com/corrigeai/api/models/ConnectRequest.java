package com.corrigeai.api.models;

import lombok.Data;

@Data
public class ConnectRequest {
    private String tipo;
    private ConnectData dados;

    @Data
    public static class ConnectData {
        private String userId;
        private String userType;
        private String authToken;
    }
}