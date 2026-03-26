package com.btg.funds.application.dto;

import java.time.Instant;

public record TransactionResponse(
        String id,
        String type,
        String fundId,
        String fundName,
        long amount,
        Instant timestamp
) {}
