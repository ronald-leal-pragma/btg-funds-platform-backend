package com.btg.funds.domain.model;

import java.time.Instant;

public record Transaction(
        String id,
        TransactionType type,
        String fundId,
        String fundName,
        long amount,
        Instant timestamp
) {
    public enum TransactionType {
        APERTURA, CANCELACION
    }
}
