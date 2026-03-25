package com.btg.funds.domain.model;

import java.util.List;

public record Client(
        String id,
        long balance,
        String notificationPreference,
        String contactInfo,
        List<String> activeFundIds
) {
    public boolean hasEnoughBalance(long amount) {
        return this.balance >= amount;
    }

    public Client deductBalance(long amount) {
        return new Client(id, balance - amount, notificationPreference, contactInfo, activeFundIds);
    }

    public Client refundBalance(long amount) {
        return new Client(id, balance + amount, notificationPreference, contactInfo, activeFundIds);
    }

    public Client addFund(String fundId) {
        var updated = new java.util.ArrayList<>(activeFundIds);
        updated.add(fundId);
        return new Client(id, balance, notificationPreference, contactInfo, List.copyOf(updated));
    }

    public Client removeFund(String fundId) {
        var updated = new java.util.ArrayList<>(activeFundIds);
        updated.remove(fundId);
        return new Client(id, balance, notificationPreference, contactInfo, List.copyOf(updated));
    }

    public boolean isSubscribedTo(String fundId) {
        return activeFundIds.contains(fundId);
    }
}
