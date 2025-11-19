package com.corrigeai.api.repositories;
import com.corrigeai.api.models.Redacao;
import org.springframework.data.mongodb.repository.MongoRepository;
// interface que conversa com o MongoDB, Spring boot que cuidará disso
public interface RedacaoRepository extends MongoRepository<Redacao, String> {}