package com.btg.funds.domain.model;

public record Fund(
        String id,
        String name,
        long minAmount,
        String category
) {}
