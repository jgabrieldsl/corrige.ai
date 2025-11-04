import java.net.*;
import java.util.*;

public class AceitadoraDeConexao extends Thread
{
    private ServerSocket pedido;
    private ArrayList<Parceiro> usuarios;

    public AceitadoraDeConexao(String porta, ArrayList<Parceiro> usuarios)
            throws Exception
    {
        if (porta == null)
            throw new Exception("Porta ausente");

        try
        {
            this.pedido = new ServerSocket(Integer.parseInt(porta));
        }
        catch (Exception erro)
        {
            throw new Exception("Porta invalida");
        }

        if (usuarios == null)
            throw new Exception("Usuarios ausentes");

        this.usuarios = usuarios;
    }

    public void run()
    {
        System.out.println("Servidor aguardando conexoes na porta " +
                this.pedido.getLocalPort() + "...");

        for (;;)
        {
            Socket conexao = null;
            try
            {
                conexao = this.pedido.accept();
                System.out.println("Nova conexao recebida de: " +
                        conexao.getInetAddress().getHostAddress());
            }
            catch (Exception erro)
            {
                System.err.println("Erro ao aceitar conexao: " + erro.getMessage());
                continue;
            }

            SupervisoraDeConexao supervisoraDeConexao = null;
            try
            {
                supervisoraDeConexao = new SupervisoraDeConexao(conexao, usuarios);
            }
            catch (Exception erro)
            {
                System.err.println("Erro ao criar supervisora: " + erro.getMessage());
            }

            if (supervisoraDeConexao != null)
                supervisoraDeConexao.start();
        }
    }
}