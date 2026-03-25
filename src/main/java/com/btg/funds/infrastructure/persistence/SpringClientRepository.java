package com.btg.funds.infrastructure.persistence;

import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringClientRepository extends MongoRepository<ClientDocument, String> {}
