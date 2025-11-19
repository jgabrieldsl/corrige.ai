package com.corrigeai.servidor;

import java.util.ArrayList;
import java.util.Scanner;

public class ServidorTicket {
    public static String PORTA_PADRAO = "3001";

    public static void main(String[] args) {
        if (args.length > 1) {
            System.err.println("Uso esperado: java ServidorTicket [PORTA]\n");
            return;
        }

        String porta = ServidorTicket.PORTA_PADRAO;

        if (args.length == 1)
            porta = args[0];

        ArrayList<Parceiro> usuarios = new ArrayList<Parceiro>();

        AceitadoraDeConexao aceitadoraDeConexao = null;
        
        try {
            aceitadoraDeConexao = new AceitadoraDeConexao(porta, usuarios);
            aceitadoraDeConexao.start();
        } catch (Exception erro) {
            System.err.println("Escolha uma porta apropriada e liberada para uso!\n");
            return;
        }

        System.out.println("========================================");
        System.out.println("  Servidor de Tickets - Corrige.AI");
        System.out.println("========================================");
        System.out.println("Servidor iniciado na porta: " + porta);
        System.out.println("Aguardando conexões...\n");

        Scanner scanner = new Scanner(System.in);
        
        for (;;) {
            System.out.println("O servidor esta ativo! Para desativa-lo,");
            System.out.println("use o comando \"desativar\"\n");
            System.out.print("> ");

            String comando = scanner.nextLine();
    
            if (comando.toLowerCase().equals("desativar")) {
                synchronized (usuarios) {
                    for (Parceiro usuario : usuarios) {
                        try {
                            usuario.adeus();
                        } catch (Exception erro) {
                        }
                    }
                }

                System.out.println("O servidor foi desativado!\n");
                scanner.close();
                System.exit(0);
            } else if (comando.toLowerCase().equals("status")) {
                System.out.println("\n=== Status do Servidor ===");
                System.out.println("Conexões ativas: " + usuarios.size());
                synchronized (usuarios) {
                    for (int i = 0; i < usuarios.size(); i++) {
                        Parceiro p = usuarios.get(i);
                        System.out.println((i + 1) + ". Socket ID: " + p.getSocketId() + 
                                         " | User: " + p.getUserId() + 
                                         " | Type: " + p.getUserType());
                    }
                }
                System.out.println("========================\n");
            } else {
                System.err.println("Comando invalido! Use 'desativar' ou 'status'\n");
            }
        }
    }
}
