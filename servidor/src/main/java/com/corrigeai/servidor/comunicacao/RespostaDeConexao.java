package com.corrigeai.servidor.comunicacao;

import java.util.HashMap;
import java.util.Map;

public class RespostaDeConexao extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private Map<String, Object> dados;

    public RespostaDeConexao(String socketId, long timestamp, int totalUsuarios) {
        this.tipo = "CONNECT_SUCCESS";
        this.dados = new HashMap<>();
        this.dados.put("socketId", socketId);
        this.dados.put("timestamp", timestamp);
        this.dados.put("totalUsuarios", totalUsuarios);
    }

    public String getTipo() {
        return tipo;
    }

    public Map<String, Object> getDados() {
        return dados;
    }
    
    public String getSocketId() {
        return (String) dados.get("socketId");
    }
    
    public long getTimestamp() {
        return (Long) dados.get("timestamp");
    }
}
