package com.corrigeai.api.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import com.corrigeai.api.models.Redacao;
import com.corrigeai.api.repositories.RedacaoRepository;

@Service
public class RedacaoService {

    @Autowired // "dependencia" serve para conectar com a classe que  salva no Mongo
    private RedacaoRepository redacaoRepository; //lugar no qual os arquivos serão salvos

    public Redacao salvarUpload(MultipartFile arquivoParaSalvar, String userId) throws Exception{

        if(arquivoParaSalvar == null) throw new NullPointerException("ArquivoParaSalvar é nulo");

        // ler o arquivo recebido do tipo MultipartFile
        byte[] bytes = arquivoParaSalvar.getBytes();
        String conteudo = new String(bytes, "UTF-8");

        // criar o objeto Redacao
        Redacao novaRedacao = new Redacao();
        novaRedacao.setConteudo(conteudo);
        novaRedacao.setStatus("PENDENTE");
        novaRedacao.setDataEnvio(new Date());
        novaRedacao.setUserId(userId);

        // salvar no Mongo através da interface repository
        return redacaoRepository.save(novaRedacao);
    }

}