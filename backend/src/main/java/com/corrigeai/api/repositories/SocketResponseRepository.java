package com.corrigeai.api.repositories;

import com.corrigeai.api.models.SocketResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocketResponseRepository extends MongoRepository<SocketResponse, String> {}