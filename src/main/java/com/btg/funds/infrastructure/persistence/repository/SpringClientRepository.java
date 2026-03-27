package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

@Profile("!aws")
public interface SpringClientRepository extends MongoRepository<ClientDocument, String> {
}
