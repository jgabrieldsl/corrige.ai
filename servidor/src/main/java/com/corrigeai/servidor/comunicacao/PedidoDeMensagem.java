package com.corrigeai.servidor.comunicacao;

import java.util.Map;

public class PedidoDeMensagem extends Comunicado {
    private static final long serialVersionUID = 1L;
    
    private String tipo;
    private Map<String, Object> dados;

    public PedidoDeMensagem() {
    }

    public PedidoDeMensagem(String tipo, Map<String, Object> dados) {
        this.tipo = tipo;
        this.dados = dados;
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
    
    public String getMensagem() {
        return (String) dados.get("mensagem");
    }
}
