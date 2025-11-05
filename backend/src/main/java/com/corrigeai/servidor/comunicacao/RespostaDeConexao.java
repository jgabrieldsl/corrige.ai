package com.corrigeai.servidor.comunicacao;

import java.util.HashMap;
import java.util.Map;

public class RespostaDeConexao extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private Map<String, Object> dados;

    public RespostaDeConexao() {
    }

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
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Map<String, Object> getDados() {
        return dados;
    }
    
    public void setDados(Map<String, Object> dados) {
        this.dados = dados;
    }
    
    public String getSocketId() {
        return (String) dados.get("socketId");
    }
    
    public Long getTimestamp() {
        return (Long) dados.get("timestamp");
    }
    
    public Integer getTotalUsuarios() {
        return (Integer) dados.get("totalUsuarios");
    }
}
