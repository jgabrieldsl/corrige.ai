import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Parceiro
{
    private Socket             conexao;
    private ObjectInputStream  receptor;
    private ObjectOutputStream transmissor;

    // Dados do usuário conectado
    private String socketId;
    private String userId;
    private String userType;  // STUDENT ou TEACHER
    private long   timestamp;

    private Comunicado proximoComunicado = null;
    private Semaphore mutEx = new Semaphore(1, true);

    public Parceiro(Socket conexao,
                    ObjectInputStream receptor,
                    ObjectOutputStream transmissor)
            throws Exception
    {
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

    // Getters e Setters para dados do usuário
    public void setSocketId(String socketId)
    {
        this.socketId = socketId;
    }

    public String getSocketId()
    {
        return this.socketId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getUserId()
    {
        return this.userId;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getUserType()
    {
        return this.userType;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public long getTimestamp()
    {
        return this.timestamp;
    }

    public boolean isAutenticado()
    {
        return this.socketId != null && this.userId != null;
    }

    public void receba(Comunicado x) throws Exception
    {
        try
        {
            this.transmissor.writeObject(x);
            this.transmissor.flush();
        }
        catch (IOException erro)
        {
            throw new Exception("Erro de transmissao");
        }
    }

    public Comunicado espie() throws Exception
    {
        try
        {
            this.mutEx.acquireUninterruptibly();
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado)this.receptor.readObject();
            this.mutEx.release();
            return this.proximoComunicado;
        }
        catch (Exception erro)
        {
            throw new Exception("Erro de recepcao");
        }
    }

    public Comunicado envie() throws Exception
    {
        try
        {
            if (this.proximoComunicado == null)
                this.proximoComunicado = (Comunicado)this.receptor.readObject();
            Comunicado ret = this.proximoComunicado;
            this.proximoComunicado = null;
            return ret;
        }
        catch (Exception erro)
        {
            throw new Exception("Erro de recepcao");
        }
    }

    public void adeus() throws Exception
    {
        try
        {
            this.transmissor.close();
            this.receptor.close();
            this.conexao.close();
        }
        catch (Exception erro)
        {
            throw new Exception("Erro de desconexao");
        }
    }

    @Override
    public String toString()
    {
        return "Parceiro[socketId=" + socketId +
                ", userId=" + userId +
                ", userType=" + userType +
                ", timestamp=" + timestamp + "]";
    }
}