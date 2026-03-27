package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.TransactionResponse;
import com.btg.funds.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.type().name(),
                transaction.fundId(),
                transaction.fundName(),
                transaction.amount(),
                transaction.timestamp()
        );
    }
}
