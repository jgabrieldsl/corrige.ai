package com.corrigeai.servidor;

import com.corrigeai.servidor.comunicacao.Comunicado;
import com.corrigeai.servidor.comunicacao.PedidoDeConexao;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import com.corrigeai.servidor.comunicacao.PedidoDeMensagem;
import com.corrigeai.servidor.comunicacao.MensagemChat;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class SupervisorDeConexao extends Thread {
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisorDeConexao(Socket conexao, ArrayList<Parceiro> usuarios) throws Exception {
        if (conexao == null)
            throw new Exception("Conexao ausente");
        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    public void run() {
        System.out.println("\n[SUPERVISOR] Nova thread iniciada");
        
        ObjectOutputStream transmissor;
        try {
            System.out.println("[SUPERVISOR] Criando ObjectOutputStream...");
            transmissor = new ObjectOutputStream(this.conexao.getOutputStream());
            transmissor.flush();
            System.out.println("[SUPERVISOR] ObjectOutputStream criado com sucesso");
        } catch (Exception erro) {
            System.err.println("[SUPERVISOR] ERRO ao criar ObjectOutputStream: " + erro.getMessage());
            erro.printStackTrace();
            return;
        }

        ObjectInputStream receptor = null;
        try {
            System.out.println("[SUPERVISOR] Criando ObjectInputStream...");
            receptor = new ObjectInputStream(this.conexao.getInputStream());
            System.out.println("[SUPERVISOR] ObjectInputStream criado com sucesso");
        } catch (Exception erro) {
            System.err.println("[SUPERVISOR] ERRO ao criar ObjectInputStream: " + erro.getMessage());
            erro.printStackTrace();
            try {
                transmissor.close();
            } catch (Exception falha) {
            }
            return;
        }

        try {
            System.out.println("[SUPERVISOR] Criando Parceiro...");
            this.usuario = new Parceiro(this.conexao, receptor, transmissor);
            System.out.println("[SUPERVISOR] Parceiro criado com sucesso");
        } catch (Exception erro) {
            System.err.println("[SUPERVISOR] ERRO ao criar Parceiro: " + erro.getMessage());
            erro.printStackTrace();
            return;
        }

        try {
            for (;;) {
                System.out.println("[SUPERVISOR] Aguardando comunicado...");
                Comunicado comunicado = this.usuario.envie();
                System.out.println("[SUPERVISOR] Comunicado recebido: " + (comunicado != null ? comunicado.getClass().getSimpleName() : "null"));

                if (comunicado == null) {
                    System.out.println("[SUPERVISOR] Comunicado nulo, encerrando...");
                    break;
                }
                else if (comunicado instanceof PedidoDeConexao) {
                    System.out.println("[SUPERVISOR] Processando PedidoDeConexao...");
                    PedidoDeConexao pedido = (PedidoDeConexao) comunicado;
                    
                    // Gera um socketId único
                    String socketId = UUID.randomUUID().toString();
                    long timestamp = System.currentTimeMillis();
                    
                    System.out.println("[SUPERVISOR] Dados recebidos:");
                    System.out.println("  - userId: " + pedido.getUserId());
                    System.out.println("  - userType: " + pedido.getUserType());
                    System.out.println("  - authToken: " + pedido.getAuthToken());
                    
                    // Armazena informações do usuário
                    this.usuario.setUserId(pedido.getUserId());
                    this.usuario.setUserType(pedido.getUserType());
                    this.usuario.setSocketId(socketId);
                    
                    // Adiciona à lista de usuários
                    synchronized (this.usuarios) {
                        this.usuarios.add(this.usuario);
                    }
                    
                    int totalUsuarios = this.usuarios.size();
                    
                    System.out.println("Nova conexão estabelecida:");
                    System.out.println("  Socket ID: " + socketId);
                    System.out.println("  User ID: " + pedido.getUserId());
                    System.out.println("  User Type: " + pedido.getUserType());
                    System.out.println("  Total de conexões: " + totalUsuarios);
                    
                    // Envia resposta de sucesso
                    System.out.println("[SUPERVISOR] Criando RespostaDeConexao...");
                    RespostaDeConexao resposta = new RespostaDeConexao(socketId, timestamp, totalUsuarios);
                    System.out.println("[SUPERVISOR] Enviando resposta...");
                    this.usuario.receba(resposta);
                    System.out.println("[SUPERVISOR] Resposta enviada com sucesso!");
                    
                    // Conexão permanece aberta para próximos comandos
                    System.out.println("[SUPERVISOR] Aguardando próximos comandos...");
                }
                else if (comunicado instanceof PedidoDeMensagem) {
                    System.out.println("[SUPERVISOR] Processando PedidoDeMensagem...");
                    PedidoDeMensagem pedido = (PedidoDeMensagem) comunicado;
                    
                    String mensagem = pedido.getMensagem();
                    long timestamp = System.currentTimeMillis();
                    
                    System.out.println("[CHAT] Mensagem recebida de " + this.usuario.getUserId() + ": " + mensagem);
                    
                    // Cria mensagem de broadcast
                    MensagemChat chatMessage = new MensagemChat(
                        this.usuario.getUserId(),
                        this.usuario.getUserType(),
                        mensagem,
                        timestamp
                    );
                    
                    // Transmite para todos os usuários conectados
                    synchronized (this.usuarios) {
                        int enviadas = 0;
                        for (Parceiro parceiro : this.usuarios) {
                            try {
                                parceiro.receba(chatMessage);
                                enviadas++;
                            } catch (Exception e) {
                                System.err.println("[CHAT] Erro ao enviar mensagem para " + parceiro.getSocketId() + ": " + e.getMessage());
                            }
                        }
                        System.out.println("[CHAT] Mensagem transmitida para " + enviadas + " usuário(s)");
                    }
                }
            }
        } catch (Exception erro) {
            // Verifica se é um erro de socket fechado (desconexão normal)
            if (erro.getMessage() != null && 
                (erro.getMessage().contains("Connection reset") || 
                 erro.getMessage().contains("Erro de recepcao") ||
                 erro.getMessage().contains("Socket closed"))) {
                System.out.println("[SUPERVISOR] Cliente desconectado: " + 
                    (this.usuario.getSocketId() != null ? this.usuario.getSocketId() : "desconhecido"));
            } else {
                System.err.println("[SUPERVISOR] ERRO no loop principal: " + erro.getMessage());
                erro.printStackTrace();
            }
        } finally {
            // Limpa recursos
            System.out.println("[SUPERVISOR] Limpando recursos...");
            synchronized (this.usuarios) {
                if (this.usuario != null) {
                    this.usuarios.remove(this.usuario);
                    System.out.println("[SUPERVISOR] Usuário removido. Total de conexões: " + this.usuarios.size());
                }
            }
            try {
                if (transmissor != null) transmissor.close();
                if (receptor != null) receptor.close();
                if (this.conexao != null && !this.conexao.isClosed()) this.conexao.close();
            } catch (Exception falha) {}
        }
    }
}
