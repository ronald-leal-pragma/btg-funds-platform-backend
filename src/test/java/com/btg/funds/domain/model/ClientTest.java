package com.btg.funds.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ClientTest {

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client("1", 500_000L, "email", "user@test.com", List.of("3"), "user@test.com", "pass123");
    }

    @Test
    void should_return_true_when_balance_is_sufficient() {
        assertThat(client.hasEnoughBalance(500_000L)).isTrue();
    }

    @Test
    void should_return_false_when_balance_is_insufficient() {
        assertThat(client.hasEnoughBalance(500_001L)).isFalse();
    }

    @Test
    void should_deduct_balance_and_return_new_client() {
        Client result = client.deductBalance(100_000L);
        assertThat(result.balance()).isEqualTo(400_000L);
        assertThat(result.id()).isEqualTo(client.id());
        assertThat(result.activeFundIds()).isEqualTo(client.activeFundIds());
    }

    @Test
    void should_refund_balance_and_return_new_client() {
        Client result = client.refundBalance(75_000L);
        assertThat(result.balance()).isEqualTo(575_000L);
    }

    @Test
    void should_add_fund_and_return_new_client() {
        Client result = client.addFund("5");
        assertThat(result.activeFundIds()).containsExactlyInAnyOrder("3", "5");
        assertThat(client.activeFundIds()).doesNotContain("5");
    }

    @Test
    void should_remove_fund_and_return_new_client() {
        Client result = client.removeFund("3");
        assertThat(result.activeFundIds()).isEmpty();
        assertThat(client.activeFundIds()).contains("3");
    }

    @Test
    void should_return_true_when_client_is_subscribed_to_fund() {
        assertThat(client.isSubscribedTo("3")).isTrue();
    }

    @Test
    void should_return_false_when_client_is_not_subscribed_to_fund() {
        assertThat(client.isSubscribedTo("1")).isFalse();
    }

    @Test
    void should_not_mutate_original_when_adding_fund() {
        client.addFund("99");
        assertThat(client.activeFundIds()).doesNotContain("99");
    }

    @Test
    void should_not_mutate_original_when_removing_fund() {
        client.removeFund("3");
        assertThat(client.activeFundIds()).contains("3");
    }
}
