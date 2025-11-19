package com.corrigeai.api.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.corrigeai.api.models.Redacao;
import com.corrigeai.api.services.RedacaoService;

@RestController // retornar somente dados
@RequestMapping("/api/redacoes") //define o prefixo de URL para todas as rotas desse controller
public class RedacaoController{

    @Autowired
    private RedacaoService redacaoService;

    // endpoint de upload
    @PostMapping("/upload")
    public ResponseEntity<Redacao> uploadRedacao(@RequestParam("file") MultipartFile arquivoParaSalvar, @RequestParam("userId") String userId){
        try{
            Redacao redacaoSalva = redacaoService.salvarUpload(arquivoParaSalvar, userId);
            return ResponseEntity.ok(redacaoSalva);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}