package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public List<Transaction> execute() {
        return transactionRepository.findAll();
    }
}
