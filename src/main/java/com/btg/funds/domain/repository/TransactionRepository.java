package com.btg.funds.domain.repository;

import com.btg.funds.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
    List<Transaction> findAllSortedByTimestampDesc();
}
