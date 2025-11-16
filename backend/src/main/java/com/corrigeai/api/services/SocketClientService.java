package com.corrigeai.api.services;

import com.corrigeai.servidor.comunicacao.PedidoDeConexao;
import com.corrigeai.servidor.comunicacao.RespostaDeConexao;
import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Service
public class SocketClientService {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3001;
    
    public Map<String, Object> sendRequest(String tipo, Map<String, Object> dados) throws Exception {
        Socket conexao = null;
        ObjectOutputStream transmissor = null;
        ObjectInputStream receptor = null;
        
        try {
            conexao = new Socket(SERVER_HOST, SERVER_PORT);
            
            transmissor = new ObjectOutputStream(conexao.getOutputStream());
            receptor = new ObjectInputStream(conexao.getInputStream());
                        
            // Envia o pedido
            PedidoDeConexao pedido = new PedidoDeConexao(tipo, dados);
            transmissor.writeObject(pedido);
            transmissor.flush();
                        
            // Recebe a resposta
            Object resposta = receptor.readObject();
            
            if (resposta instanceof RespostaDeConexao) {
                RespostaDeConexao respostaConexao = (RespostaDeConexao) resposta;
                
                Map<String, Object> resultado = new HashMap<>();
                resultado.put("tipo", respostaConexao.getTipo());
                resultado.put("dados", respostaConexao.getDados());
                
                return resultado;
            } else {
                throw new Exception("Resposta inesperada do servidor");
            }
            
        } catch (Exception e) {
            throw new Exception("Erro na comunicação com o servidor: " + e.getMessage(), e);
        } finally {
            if (transmissor != null) transmissor.close();
            if (receptor != null) receptor.close();
            if (conexao != null) conexao.close();
        }
    }
}
