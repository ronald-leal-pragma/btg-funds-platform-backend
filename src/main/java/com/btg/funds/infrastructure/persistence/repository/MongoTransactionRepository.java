package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.infrastructure.persistence.mapper.TransactionDocumentMapper;
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
    private final TransactionDocumentMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("[REPO] MongoTransactionRepository.save: id={}", transaction.id());
        return mapper.toDomain(springRepository.save(mapper.toDocument(transaction)));
    }

    @Override
    public List<Transaction> findByClientId(String clientId) {
        return springRepository.findByClientId(clientId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Transaction> findByClientIdSortedByTimestampDesc(String clientId) {
        log.debug("[REPO] MongoTransactionRepository.findByClientIdSortedByTimestampDesc: clientId={}", clientId);
        return springRepository.findByClientIdOrderByTimestampDesc(clientId).stream().map(mapper::toDomain).toList();
    }
}
