package com.btg.funds.infrastructure.persistence;

import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringTransactionRepository extends MongoRepository<TransactionDocument, String> {}
