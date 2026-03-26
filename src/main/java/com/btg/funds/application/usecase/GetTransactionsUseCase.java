package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTransactionsUseCase implements com.btg.funds.application.port.in.GetTransactionsPort {

    private final TransactionRepository transactionRepository;

    public List<Transaction> execute() {
        return transactionRepository.findAll();
    }

    public List<Transaction> execute(String sort) {
        if (sort == null || "desc".equalsIgnoreCase(sort)) {
            return transactionRepository.findAllSortedByTimestampDesc();
        }
        return transactionRepository.findAll();
    }
}
