package com.corrigeai.servidor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;
import static org.junit.jupiter.api.Assertions.*;

class ServidorTicketTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final java.io.InputStream originalIn = System.in;
    private final SecurityManager originalSecurityManager = System.getSecurityManager();

    static class ExitTrappedException extends SecurityException {
        final int status;
        ExitTrappedException(int status) { this.status = status; }
    }

    static class NoExitSecurityManager extends SecurityManager {
        @Override
        public void checkPermission(Permission perm) { /* allow other ops */ }
        @Override
        public void checkPermission(Permission perm, Object context) { /* allow other ops */ }
        @Override
        public void checkExit(int status) { throw new ExitTrappedException(status); }
    }

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(errorStream));
        // Prevent System.exit from terminating the test JVM
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        System.setIn(originalIn);
        System.setSecurityManager(originalSecurityManager);
    }

    @Test
    void testMainWithInvalidArguments() {
        String[] args = {"3001", "extraArg"};
        ServidorTicket.main(args);
        assertTrue(errorStream.toString().contains("Uso esperado: java ServidorTicket [PORTA]"));
    }

    @Test
    void testMainWithDefaultPort() throws InterruptedException {
        String[] args = {};
        // provide input that will stop the server
        System.setIn(new ByteArrayInputStream("desativar\n".getBytes()));
        Thread t = new Thread(() -> {
            try {
                ServidorTicket.main(args);
            } catch (ExitTrappedException e) {
                // expected due to System.exit interception
            } catch (Throwable ignored) {}
        });
        t.start();
        Thread.sleep(500);
        String out = outputStream.toString();
        assertTrue(out.contains("Servidor iniciado na porta: 3001"), "Expected startup message for default port");
        t.join(2000);
    }

    @Test
    void testMainWithCustomPort() throws InterruptedException {
        String[] args = {"4000"};
        System.setIn(new ByteArrayInputStream("desativar\n".getBytes()));
        Thread t = new Thread(() -> {
            try {
                ServidorTicket.main(args);
            } catch (ExitTrappedException e) {
            } catch (Throwable ignored) {}
        });
        t.start();
        Thread.sleep(500);
        String out = outputStream.toString();
        assertTrue(out.contains("Servidor iniciado na porta: 4000"), "Expected startup message for custom port");
        t.join(2000);
    }

    @Test
    void testInvalidCommand() throws InterruptedException {
        String[] args = {};
        // send invalid command then desativar to end loop
        System.setIn(new ByteArrayInputStream("invalidCommand\ndesativar\n".getBytes()));
        Thread t = new Thread(() -> {
            try {
                ServidorTicket.main(args);
            } catch (ExitTrappedException e) {
            } catch (Throwable ignored) {}
        });
        t.start();
        Thread.sleep(700);
        String bothOut = outputStream.toString() + errorStream.toString();
        assertTrue(bothOut.contains("Comando invalido! Use 'desativar' ou 'status'"), "Expected invalid command message");
        t.join(2000);
    }

    @Test
    void testStatusCommand() throws InterruptedException {
        String[] args = {};
        // send status then desativar
        System.setIn(new ByteArrayInputStream("status\ndesativar\n".getBytes()));
        Thread t = new Thread(() -> {
            try {
                ServidorTicket.main(args);
            } catch (ExitTrappedException e) {
            } catch (Throwable ignored) {}
        });
        t.start();
        Thread.sleep(700);
        String out = outputStream.toString();
        assertTrue(out.contains("=== Status do Servidor ==="), "Expected status output");
        t.join(2000);
    }

    @Test
    void testPromptAndHelpMessagePresent() throws InterruptedException {
        String[] args = {};
        System.setIn(new ByteArrayInputStream("desativar\n".getBytes()));
        Thread t = new Thread(() -> {
            try {
                ServidorTicket.main(args);
            } catch (ExitTrappedException e) {
            } catch (Throwable ignored) {}
        });
        t.start();
        Thread.sleep(500);
        String out = outputStream.toString();
        assertTrue(out.contains("O servidor esta ativo! Para desativa-lo,"), "Prompt/help message should be printed");
        assertTrue(out.contains("> "), "Prompt symbol should be printed");
        t.join(2000);
    }
}