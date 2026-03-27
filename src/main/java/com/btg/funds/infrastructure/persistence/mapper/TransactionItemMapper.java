package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.infrastructure.persistence.item.TransactionItem;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TransactionItemMapper {

    public TransactionItem toItem(Transaction t) {
        return new TransactionItem(
                t.clientId(),
                t.timestamp().toString(),
                t.id(),
                t.type().name(),
                t.fundId(),
                t.fundName(),
                t.amount()
        );
    }

    public Transaction toDomain(TransactionItem item) {
        return new Transaction(
                item.getTransactionId(),
                item.getClientId(),
                Transaction.TransactionType.valueOf(item.getType()),
                item.getFundId(),
                item.getFundName(),
                item.getAmount(),
                Instant.parse(item.getTimestamp())
        );
    }
}
