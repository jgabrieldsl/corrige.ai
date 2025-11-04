import java.io.*;
import java.net.*;

public class Cliente {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 3000;
    private static boolean nomeEnviado = false;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            System.out.println(" Conectado ao servidor!");
            System.out.println(" Digite /sair para se desconectar");
            System.out.println("----------------------------------------");

            // Thread para receber mensagens do servidor
            Thread receiveThread = new Thread(new ReceiveHandler(socket));
            receiveThread.start();

            // Thread principal envia mensagens
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String message;
            while (true) {
                // Só mostra "Envie sua mensagem:" depois do nome ser enviado
                if (nomeEnviado) {
                    System.out.print("Envie sua mensagem: ");
                }

                message = Teclado.getUmString();

                if (message == null || message.equalsIgnoreCase("/sair")) {
                    out.println("/sair");
                    System.out.println("Desconectando...");
                    break;
                }

                out.println(message);

                // Marca que o nome foi enviado após a primeira entrada
                if (!nomeEnviado) {
                    nomeEnviado = true;
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }

    // Handler para receber mensagens do servidor
    static class ReceiveHandler implements Runnable {
        private Socket socket;

        public ReceiveHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()))) {

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }

            } catch (IOException e) {
                System.err.println("Conexão encerrada.");
            }
        }
    }
}