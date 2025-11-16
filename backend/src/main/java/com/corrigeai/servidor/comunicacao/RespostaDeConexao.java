package com.corrigeai.servidor.comunicacao;

import java.util.HashMap;
import java.util.Map;

public class RespostaDeConexao extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    // Constantes para tipos de resposta
    public static final String CONNECT_SUCCESS = "CONNECT_SUCCESS";
    public static final String CONNECT_ERROR = "CONNECT_ERROR";
    
    private String tipo;
    private Map<String, Object> dados;

    public RespostaDeConexao() {
    }

    // Construtor para sucesso
    public RespostaDeConexao(String socketId, long timestamp, int totalUsuarios) {
        this.tipo = CONNECT_SUCCESS;
        this.dados = new HashMap<>();
        this.dados.put("socketId", socketId);
        this.dados.put("timestamp", timestamp);
        this.dados.put("totalUsuarios", totalUsuarios);
    }
    
    // Construtor para erro
    public RespostaDeConexao(String mensagemErro) {
        this.tipo = CONNECT_ERROR;
        this.dados = new HashMap<>();
        this.dados.put("mensagem", mensagemErro);
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
