package com.btg.funds.infrastructure.notification;

import com.btg.funds.application.port.out.NotificationPort;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogNotificationAdapter implements NotificationPort {

    @Override
    public void notifySubscription(Client client, Fund fund) {
        String channel = client.notificationPreference().equalsIgnoreCase("sms") ? "SMS" : "Email";
        log.info("[NOTIFICATION][{}] Suscripción confirmada — fondo: {} — contacto: {}",
                channel, fund.name(), client.contactInfo());
    }
}
