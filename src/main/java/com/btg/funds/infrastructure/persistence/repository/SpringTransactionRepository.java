package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@Profile("!aws")
public interface SpringTransactionRepository extends MongoRepository<TransactionDocument, String> {

    @Query(sort = "{ 'timestamp': -1 }")
    List<TransactionDocument> findAllByOrderByTimestampDesc();
}
