package com.corrigeai.servidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AceitadoraDeConexao extends Thread {
    private ServerSocket pedido;
    private ArrayList<Parceiro> usuarios;

    public AceitadoraDeConexao(String porta, ArrayList<Parceiro> usuarios) throws Exception {
        if (porta == null)
            throw new Exception("Porta ausente");

        try {
            this.pedido = new ServerSocket(Integer.parseInt(porta));
        } catch (Exception erro) {
            throw new Exception("Porta invalida");
        }

        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.usuarios = usuarios;
    }

    public void run() {
        for (;;) {
            Socket conexao = null;
            try {
                conexao = this.pedido.accept();
            } catch (Exception erro) {
                continue;
            }

            SupervisorDeConexao supervisorDeConexao = null;
            try {
                supervisorDeConexao = new SupervisorDeConexao(conexao, usuarios);
            } catch (Exception erro) {
            }
            supervisorDeConexao.start();
        }
    }
}
