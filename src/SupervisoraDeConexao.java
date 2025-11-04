import java.io.*;
import java.net.*;
import java.util.*;

public class SupervisoraDeConexao extends Thread
{
    private Parceiro usuario;
    private Socket conexao;
    private ArrayList<Parceiro> usuarios;

    public SupervisoraDeConexao(Socket conexao, ArrayList<Parceiro> usuarios)
            throws Exception
    {
        if (conexao == null)
            throw new Exception("Conexao ausente");

        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.conexao = conexao;
        this.usuarios = usuarios;
    }

    public void run()
    {
        ObjectOutputStream transmissor;
        try
        {
            transmissor = new ObjectOutputStream(
                    this.conexao.getOutputStream());
        }
        catch (Exception erro)
        {
            return;
        }

        ObjectInputStream receptor = null;
        try
        {
            receptor = new ObjectInputStream(
                    this.conexao.getInputStream());
        }
        catch (Exception err0)
        {
            try
            {
                transmissor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }

        try
        {
            this.usuario = new Parceiro(this.conexao, receptor, transmissor);
        }
        catch (Exception erro)
        {} // sei que passei os parametros corretos

        try
        {
            // Aguarda mensagem CONNECT do cliente
            Comunicado comunicadoInicial = this.usuario.envie();

            if (comunicadoInicial == null ||
                    !comunicadoInicial.getTipo().equals("CONNECT"))
            {
                System.err.println("Conexao rejeitada: mensagem CONNECT nao recebida");
                this.usuario.adeus();
                return;
            }

            // Extrai dados do CONNECT
            String userId = comunicadoInicial.getDadoString("userId");
            String userType = comunicadoInicial.getDadoString("userType");
            String authToken = comunicadoInicial.getDadoString("authToken");

            // Validação básica (aqui você pode adicionar validação do authToken)
            if (userId == null || userType == null || authToken == null)
            {
                System.err.println("Conexao rejeitada: dados incompletos");
                this.usuario.adeus();
                return;
            }

            // Gera socketId e timestamp
            String socketId = "socket" + System.currentTimeMillis();
            long timestamp = System.currentTimeMillis();

            // Configura o parceiro
            this.usuario.setSocketId(socketId);
            this.usuario.setUserId(userId);
            this.usuario.setUserType(userType);
            this.usuario.setTimestamp(timestamp);

            // Envia CONNECT_SUCCESS
            Comunicado resposta = new Comunicado("CONNECT_SUCCESS");
            resposta.setDado("socketId", socketId);
            resposta.setDado("timestamp", timestamp);
            this.usuario.receba(resposta);

            System.out.println("Usuario conectado: " + this.usuario);

            // Adiciona à lista de usuários conectados
            synchronized (this.usuarios)
            {
                this.usuarios.add(this.usuario);
            }

            // Loop principal - processa mensagens do cliente
            for (;;)
            {
                Comunicado comunicado = this.usuario.envie();

                if (comunicado == null)
                {
                    break;
                }
                else if (comunicado.getTipo().equals("DISCONNECT") ||
                        comunicado instanceof PedidoParaSair)
                {
                    System.out.println("Usuario desconectando: " + this.usuario.getUserId());
                    break;
                }
                else if (comunicado.getTipo().equals("MESSAGE"))
                {
                    // Processa mensagem de chat
                    String destinatarioId = comunicado.getDadoString("to");
                    String mensagem = comunicado.getDadoString("message");

                    System.out.println("Mensagem de " + userId + " para " + destinatarioId + ": " + mensagem);

                    // Encontra destinatário e encaminha mensagem
                    synchronized (this.usuarios)
                    {
                        for (Parceiro parceiro : this.usuarios)
                        {
                            if (parceiro.getUserId().equals(destinatarioId))
                            {
                                Comunicado mensagemParaDestinatario = new Comunicado("MESSAGE");
                                mensagemParaDestinatario.setDado("from", userId);
                                mensagemParaDestinatario.setDado("message", mensagem);
                                mensagemParaDestinatario.setDado("timestamp", System.currentTimeMillis());

                                parceiro.receba(mensagemParaDestinatario);
                                break;
                            }
                        }
                    }
                }
            }

            // Remove usuário da lista
            synchronized (this.usuarios)
            {
                this.usuarios.remove(this.usuario);
            }
            this.usuario.adeus();
        }
        catch (Exception erro)
        {
            System.err.println("Erro na supervisora: " + erro.getMessage());

            try
            {
                synchronized (this.usuarios)
                {
                    this.usuarios.remove(this.usuario);
                }
                transmissor.close();
                receptor.close();
            }
            catch (Exception falha)
            {} // so tentando fechar antes de acabar a thread

            return;
        }
    }
}