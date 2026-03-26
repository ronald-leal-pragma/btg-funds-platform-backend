package com.btg.funds.application.dto;

import java.util.List;

public record ClientResponse(
        String id,
        long balance,
        String notificationPreference,
        String contactInfo,
        List<String> activeFundIds
) {}
