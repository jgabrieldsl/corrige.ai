import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Servidor {
    private static final int PORT = 3000;
    private static Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Servidor de Chat iniciado na porta " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Envia mensagem para todos os clientes conectados
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Remove cliente desconectado
    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    // Handler para cada cliente conectado
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Solicita nome do usuário

                out.println("Digite seu nome:");
                username = in.readLine();

                System.out.println( username + " conectado!");
                broadcast(username + " entrou no chat!", this);


                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/sair")) {
                        break;
                    }
                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, this);
                }

            } catch (IOException e) {
                System.err.println("Erro com cliente " + username + ": " + e.getMessage());
            } finally {
                disconnect();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        private void disconnect() {
            try {
                System.out.println(username + " desconectado!");
                broadcast(username + " saiu do chat.", this);
                removeClient(this);
                socket.close();
            } catch (IOException e) {
                System.err.println("Erro ao desconectar: " + e.getMessage());
            }
        }
    }
}