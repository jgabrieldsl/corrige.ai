public class PedidoParaSair extends Comunicado
{
    private static final long serialVersionUID = 1L;

    public PedidoParaSair()
    {
        super("DISCONNECT");
    }

    public PedidoParaSair(String userId)
    {
        super("DISCONNECT");
        this.setDado("userId", userId);
        this.setDado("timestamp", System.currentTimeMillis());
    }

    public PedidoParaSair(String userId, String motivo)
    {
        super("DISCONNECT");
        this.setDado("userId", userId);
        this.setDado("motivo", motivo);
        this.setDado("timestamp", System.currentTimeMillis());
    }

    public String getUserId()
    {
        return this.getDadoString("userId");
    }

    public String getMotivo()
    {
        return this.getDadoString("motivo");
    }
}