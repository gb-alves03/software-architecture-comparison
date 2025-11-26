package com.tcc.antifraud_service.repository;

import com.tcc.antifraud_service.repository.mongo.FraudCheckDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FraudRepository extends MongoRepository<FraudCheckDocument, String> {
}
