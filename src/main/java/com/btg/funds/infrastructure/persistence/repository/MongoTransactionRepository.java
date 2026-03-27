package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Profile("!aws")
@Repository
@RequiredArgsConstructor
public class MongoTransactionRepository implements TransactionRepository {

    private final SpringTransactionRepository springRepository;

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("[REPO] MongoTransactionRepository.save: id={}", transaction.id());
        return toDomain(springRepository.save(toDocument(transaction)));
    }

    @Override
    public List<Transaction> findAll() {
        return springRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Transaction> findAllSortedByTimestampDesc() {
        log.debug("[REPO] MongoTransactionRepository.findAllSortedByTimestampDesc");
        return springRepository.findAllByOrderByTimestampDesc().stream().map(this::toDomain).toList();
    }

    private TransactionDocument toDocument(Transaction t) {
        return new TransactionDocument(
                t.id(),
                t.type().name(),
                t.fundId(),
                t.fundName(),
                t.amount(),
                t.timestamp()
        );
    }

    private Transaction toDomain(TransactionDocument doc) {
        return new Transaction(
                doc.getId(),
                Transaction.TransactionType.valueOf(doc.getType()),
                doc.getFundId(),
                doc.getFundName(),
                doc.getAmount(),
                doc.getTimestamp()
        );
    }
}
