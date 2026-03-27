package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import org.springframework.stereotype.Component;

@Component
public class TransactionDocumentMapper {

    public TransactionDocument toDocument(Transaction t) {
        return new TransactionDocument(
                t.id(),
                t.clientId(),
                t.type().name(),
                t.fundId(),
                t.fundName(),
                t.amount(),
                t.timestamp()
        );
    }

    public Transaction toDomain(TransactionDocument doc) {
        return new Transaction(
                doc.getId(),
                doc.getClientId(),
                Transaction.TransactionType.valueOf(doc.getType()),
                doc.getFundId(),
                doc.getFundName(),
                doc.getAmount(),
                doc.getTimestamp()
        );
    }
}
