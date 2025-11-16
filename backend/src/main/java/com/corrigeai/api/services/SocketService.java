package com.corrigeai.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class SocketService {
    private static final String SERVER_URL = "http://localhost:3000/servidor";
    
    private final RestTemplate restTemplate;
    
    public SocketService() {
        this.restTemplate = new RestTemplate();
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> sendRequest(String tipo, Map<String, Object> dados) throws Exception {
        try {            
            // Prepara o corpo da requisição
            Map<String, Object> requestBody = Map.of(
                "tipo", tipo,
                "dados", dados
            );
            
            // Configura os headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Cria a entidade HTTP com body e headers
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Faz a requisição POST
            Map<String, Object> response = restTemplate.postForObject(
                SERVER_URL,
                request,
                Map.class
            );
            
            return response;
            
        } catch (Exception e) {
            throw e;
        }
    }
}