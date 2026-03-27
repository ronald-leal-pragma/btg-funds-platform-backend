package com.btg.funds.domain.repository;

import com.btg.funds.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByClientId(String clientId);
    List<Transaction> findByClientIdSortedByTimestampDesc(String clientId);
}
