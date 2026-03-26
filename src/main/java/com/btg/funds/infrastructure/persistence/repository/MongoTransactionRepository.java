package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.mapper.TransactionDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoTransactionRepository implements TransactionRepository {

    private final SpringTransactionRepository spring;
    private final TransactionDocumentMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("[REPO] MongoTransactionRepository - save: id={}, type={}, amount={}", transaction.id(), transaction.type(), transaction.amount());
        return mapper.toDomain(spring.save(mapper.toDocument(transaction)));
    }

    @Override
    public List<Transaction> findAll() {
        log.debug("[REPO] MongoTransactionRepository - findAll");
        return spring.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findAllSortedByTimestampDesc() {
        log.debug("[REPO] MongoTransactionRepository - findAllSortedByTimestampDesc");
        return spring.findAll(Sort.by(Sort.Direction.DESC, "timestamp")).stream().map(mapper::toDomain).toList();
    }
}
