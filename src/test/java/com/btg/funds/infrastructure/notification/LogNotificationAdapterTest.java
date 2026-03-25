package com.btg.funds.infrastructure.notification;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LogNotificationAdapterTest {

    private final LogNotificationAdapter adapter = new LogNotificationAdapter();

    @Test
    void should_notify_via_email_channel_when_preference_is_email() {
        Client client = new Client("1", 425_000L, "email", "user@test.com", List.of("1"));
        Fund fund = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");

        assertDoesNotThrow(() -> adapter.notifySubscription(client, fund));
    }

    @Test
    void should_notify_via_sms_channel_when_preference_is_sms() {
        Client client = new Client("1", 425_000L, "sms", "+573001234567", List.of("1"));
        Fund fund = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");

        assertDoesNotThrow(() -> adapter.notifySubscription(client, fund));
    }

    @Test
    void should_treat_unknown_preference_as_email_channel() {
        Client client = new Client("1", 425_000L, "push", "user@test.com", List.of("1"));
        Fund fund = new Fund("2", "DEUDAPRIVADA", 50_000L, "FIC");

        assertDoesNotThrow(() -> adapter.notifySubscription(client, fund));
    }
}
