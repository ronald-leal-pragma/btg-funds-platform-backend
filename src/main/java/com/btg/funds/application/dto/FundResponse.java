package com.btg.funds.application.dto;

public record FundResponse(
        String id,
        String name,
        long minAmount,
        String category
) {}
