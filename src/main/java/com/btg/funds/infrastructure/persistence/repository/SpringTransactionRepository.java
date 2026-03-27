package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@Profile("!aws")
public interface SpringTransactionRepository extends MongoRepository<TransactionDocument, String> {

    List<TransactionDocument> findByClientId(String clientId);

    @Query(value = "{ 'client_id': ?0 }", sort = "{ 'timestamp': -1 }")
    List<TransactionDocument> findByClientIdOrderByTimestampDesc(String clientId);
}
