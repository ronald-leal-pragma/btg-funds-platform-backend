package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.infrastructure.persistence.document.FundDocument;
import org.springframework.stereotype.Component;

@Component
public class FundDocumentMapper {

    public FundDocument toDocument(Fund fund) {
        return new FundDocument(fund.id(), fund.name(), fund.minAmount(), fund.category());
    }

    public Fund toDomain(FundDocument doc) {
        return new Fund(doc.getId(), doc.getName(), doc.getMinAmount(), doc.getCategory());
    }
}
