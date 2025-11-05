package com.corrigeai.servidor;

import com.corrigeai.servidor.comunicacao.Comunicado;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Parceiro {
    private Socket conexao;
    private ObjectInputStream receptor;
    private ObjectOutputStream transmissor;
    private Comunicado proximoComunicado = null;
    private Semaphore mutex = new Semaphore(1, true);
    
    private String userId;
    private String userType;
    private String socketId;

    public Parceiro(Socket conexao, ObjectInputStream receptor, ObjectOutputStream transmissor) throws Exception {
        if (conexao == null)
            throw new Exception("Conexao ausente");
        if (receptor == null)
            throw new Exception("Receptor ausente");
        if (transmissor == null)
            throw new Exception("Transmissor ausente");

        this.conexao = conexao;
        this.receptor = receptor;
        this.transmissor = transmissor;
    }

    public void receba(Comunicado x) throws Exception {
        try {
            this.transmissor.writeObject(x);
            this.transmissor.flush();
        } catch (Exception erro) {
            throw new Exception("Erro de transmissao");
        }
    }

    public Comunicado espie() throws Exception {
        try {
            this.mutex.acquireUninterruptibly();
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado) this.receptor.readObject();
            this.mutex.release();
            return this.proximoComunicado;
        } catch (Exception erro) {
            throw new Exception("Erro de recepcao");
        }
    }

    public Comunicado envie() throws Exception {
        try {
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado) this.receptor.readObject();
            Comunicado ret = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        } catch (Exception erro) {
            throw new Exception("Erro de recepcao");
        }
    }

    public void adeus() throws Exception {
        try {
            this.transmissor.close();
            this.receptor.close();
            this.conexao.close();
        } catch (Exception erro) {
            throw new Exception("Erro de desconexao");
        }
    }
    
    // Getters e Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getSocketId() {
        return socketId;
    }
    
    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }
}
