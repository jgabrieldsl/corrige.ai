public class TratadoraDeComunicadoDeDesligamento extends Thread
{
    private Parceiro servidor;

    public TratadoraDeComunicadoDeDesligamento(Parceiro servidor) throws Exception
    {
        if (servidor == null)
            throw new Exception("Servidor ausente");

        this.servidor = servidor;
    }

    public void run()
    {
        for (;;)
        {
            try
            {
                Comunicado comunicado = this.servidor.espie();

                if (comunicado != null &&
                        (comunicado instanceof ComunicadoDeDesligamento ||
                                comunicado.getTipo().equals("SERVER_SHUTDOWN")))
                {
                    ComunicadoDeDesligamento desligamento;

                    if (comunicado instanceof ComunicadoDeDesligamento)
                    {
                        desligamento = (ComunicadoDeDesligamento) comunicado;
                    }
                    else
                    {
                        // Cria um ComunicadoDeDesligamento a partir do Comunicado genérico
                        desligamento = new ComunicadoDeDesligamento(
                                comunicado.getDadoString("mensagem")
                        );
                    }

                    String mensagem = desligamento.getMensagem();
                    Integer tempoParaDesligar = desligamento.getTempoParaDesligar();

                    System.out.println("\n==============================================");
                    System.out.println("AVISO DE DESLIGAMENTO DO SERVIDOR");
                    System.out.println("==============================================");

                    if (mensagem != null)
                        System.out.println("Mensagem: " + mensagem);

                    if (tempoParaDesligar != null && tempoParaDesligar > 0)
                    {
                        System.out.println("Tempo para desligamento: " + tempoParaDesligar + " segundos");
                        System.out.println("\nAguardando desligamento...");

                        try
                        {
                            Thread.sleep(tempoParaDesligar * 1000);
                        }
                        catch (InterruptedException e)
                        {}
                    }

                    System.out.println("\nO servidor foi desligado.");
                    System.err.println("Por favor, volte mais tarde!\n");
                    System.exit(0);
                }

                // Pequena pausa para não sobrecarregar a CPU
                Thread.sleep(100);
            }
            catch (Exception erro)
            {
                // Se houve erro de comunicação, servidor provavelmente caiu
                System.err.println("\nConexao com o servidor perdida.");
                System.exit(1);
            }
        }
    }
}