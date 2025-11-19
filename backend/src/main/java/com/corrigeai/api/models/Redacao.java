package com.corrigeai.api.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "redacoes")
public class Redacao{
    @Id
    private String id; // id da redação gerado pelo mongo
    private String conteudo; // texto da redacão
    private String status; // PENDENTE, CONCLUIDA
    private String nota; // retornado pela IA
    private String feedbackIA; 
    private Date dataEnvio; //usar date por causa do Spring data mongoDB que faz conversão automática
    private String userId;

    public String getId(){
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getFeedbackIA() {
        return feedbackIA;
    }

    public void setFeedbackIA(String feedbackIA) {
        this.feedbackIA = feedbackIA;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }
}

