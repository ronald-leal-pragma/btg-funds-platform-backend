package com.btg.funds.infrastructure.persistence;

import com.btg.funds.infrastructure.persistence.document.FundDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpringFundRepository extends MongoRepository<FundDocument, String> {}
