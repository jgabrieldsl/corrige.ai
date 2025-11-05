package com.corrigeai.servidor.comunicacao;

import java.util.HashMap;
import java.util.Map;

public class MensagemChat extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private Map<String, Object> dados;

    public MensagemChat() {
    }

    public MensagemChat(String userId, String userType, String mensagem, long timestamp) {
        this.tipo = "CHAT_MESSAGE";
        this.dados = new HashMap<>();
        this.dados.put("userId", userId);
        this.dados.put("userType", userType);
        this.dados.put("mensagem", mensagem);
        this.dados.put("timestamp", timestamp);
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
    
    public String getUserId() {
        return (String) dados.get("userId");
    }
    
    public String getUserType() {
        return (String) dados.get("userType");
    }
    
    public String getMensagem() {
        return (String) dados.get("mensagem");
    }
    
    public Long getTimestamp() {
        return (Long) dados.get("timestamp");
    }
}
