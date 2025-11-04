public class ComunicadoDeDesligamento extends Comunicado
{
    private static final long serialVersionUID = 1L;

    public ComunicadoDeDesligamento()
    {
        super("SERVER_SHUTDOWN");
        this.setDado("mensagem", "Servidor esta sendo desligado");
        this.setDado("timestamp", System.currentTimeMillis());
    }

    public ComunicadoDeDesligamento(String mensagem)
    {
        super("SERVER_SHUTDOWN");
        this.setDado("mensagem", mensagem);
        this.setDado("timestamp", System.currentTimeMillis());
    }

    public ComunicadoDeDesligamento(String mensagem, int tempoParaDesligar)
    {
        super("SERVER_SHUTDOWN");
        this.setDado("mensagem", mensagem);
        this.setDado("tempoParaDesligar", tempoParaDesligar);
        this.setDado("timestamp", System.currentTimeMillis());
    }

    public String getMensagem()
    {
        return this.getDadoString("mensagem");
    }

    public Integer getTempoParaDesligar()
    {
        Object tempo = this.getDado("tempoParaDesligar");
        return tempo != null ? (Integer) tempo : null;
    }
}