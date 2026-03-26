package com.btg.funds.application.dto;

public record FundWithStatusResponse(
        FundResponse fund,
        boolean subscribed
) {}
