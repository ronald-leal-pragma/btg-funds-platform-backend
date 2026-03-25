package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.model.Transaction.TransactionType;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MongoTransactionRepository implements TransactionRepository {

    private final SpringTransactionRepository spring;

    @Override
    public Transaction save(Transaction transaction) {
        TransactionDocument doc = toDocument(transaction);
        return toDomain(spring.save(doc));
    }

    @Override
    public List<Transaction> findAll() {
        return spring.findAll().stream().map(this::toDomain).toList();
    }

    private Transaction toDomain(TransactionDocument doc) {
        return new Transaction(
                doc.getId(),
                TransactionType.valueOf(doc.getType()),
                doc.getFundId(),
                doc.getFundName(),
                doc.getAmount(),
                doc.getTimestamp()
        );
    }

    private TransactionDocument toDocument(Transaction t) {
        return TransactionDocument.builder()
                .id(t.id())
                .type(t.type().name())
                .fundId(t.fundId())
                .fundName(t.fundName())
                .amount(t.amount())
                .timestamp(t.timestamp())
                .build();
    }
}
