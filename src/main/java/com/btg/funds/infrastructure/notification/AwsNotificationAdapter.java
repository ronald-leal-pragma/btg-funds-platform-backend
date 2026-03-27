package com.btg.funds.infrastructure.notification;

import com.btg.funds.application.port.out.NotificationPort;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

/**
 * Adaptador de notificaciones usando AWS SES (email) y SNS (SMS).
 * Solo activo con el perfil "aws" — en local usa LogNotificationAdapter.
 */
@Slf4j
@Component
@Profile("aws")
@RequiredArgsConstructor
public class AwsNotificationAdapter implements NotificationPort {

    private final SesClient sesClient;
    private final SnsClient snsClient;

    @Value("${aws.ses.sender-email}")
    private String senderEmail;

    @Override
    public void notifySubscription(Client client, Fund fund) {
        if ("sms".equalsIgnoreCase(client.notificationPreference())) {
            sendSms(client, fund);
        } else {
            sendEmail(client, fund);
        }
    }

    // ---------------------------------------------------------

    private void sendEmail(Client client, Fund fund) {
        try {
            var request = SendEmailRequest.builder()
                    .source(senderEmail)
                    .destination(Destination.builder()
                            .toAddresses(client.contactInfo())
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data("Suscripción exitosa — " + fund.name())
                                    .charset("UTF-8")
                                    .build())
                            .body(Body.builder()
                                    .text(Content.builder()
                                            .data(buildEmailBody(client, fund))
                                            .charset("UTF-8")
                                            .build())
                                    .build())
                            .build())
                    .build();

            sesClient.sendEmail(request);
            log.info("[NOTIFICATION][EMAIL] Enviado a {} — fondo: {}", client.contactInfo(), fund.name());

        } catch (Exception e) {
            log.error("[NOTIFICATION][EMAIL] Error enviando email a {}: {}", client.contactInfo(), e.getMessage());
        }
    }

    private void sendSms(Client client, Fund fund) {
        try {
            var request = PublishRequest.builder()
                    .phoneNumber(client.contactInfo())
                    .message(buildSmsBody(fund))
                    .build();

            snsClient.publish(request);
            log.info("[NOTIFICATION][SMS] Enviado a {} — fondo: {}", client.contactInfo(), fund.name());

        } catch (Exception e) {
            log.error("[NOTIFICATION][SMS] Error enviando SMS a {}: {}", client.contactInfo(), e.getMessage());
        }
    }

    private String buildEmailBody(Client client, Fund fund) {
        return """
                Estimado cliente,

                Te confirmamos que te has suscrito exitosamente al siguiente fondo de inversión:

                  Fondo    : %s
                  Categoría: %s
                  Monto    : COP $%,d

                Puedes consultar tu historial de transacciones en la plataforma BTG Pactual.

                Gracias por confiar en nosotros.

                BTG Pactual — Gestión de Fondos
                """.formatted(fund.name(), fund.category(), fund.minAmount());
    }

    private String buildSmsBody(Fund fund) {
        return "BTG Pactual: Suscripción exitosa al fondo %s por COP $%,d."
                .formatted(fund.name(), fund.minAmount());
    }
}
