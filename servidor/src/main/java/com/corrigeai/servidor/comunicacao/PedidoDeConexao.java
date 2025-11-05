package com.corrigeai.servidor.comunicacao;

import java.util.Map;

public class PedidoDeConexao extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private Map<String, Object> dados;

    public PedidoDeConexao(String tipo, Map<String, Object> dados) {
        this.tipo = tipo;
        this.dados = dados;
    }

    public String getTipo() {
        return tipo;
    }

    public Map<String, Object> getDados() {
        return dados;
    }
    
    public String getUserId() {
        return (String) dados.get("userId");
    }
    
    public String getUserType() {
        return (String) dados.get("userType");
    }
    
    public String getAuthToken() {
        return (String) dados.get("authToken");
    }
}
