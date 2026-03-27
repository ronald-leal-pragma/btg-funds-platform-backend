package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Client;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ClientDocumentMapper {

    public ClientDocument toDocument(Client client) {
        return new ClientDocument(
                client.id(),
                client.balance(),
                client.notificationPreference(),
                client.contactInfo(),
                new ArrayList<>(client.activeFundIds()),
                client.email(),
                client.password()
        );
    }

    public Client toDomain(ClientDocument doc) {
        return new Client(
                doc.getId(),
                doc.getBalance(),
                doc.getNotificationPreference(),
                doc.getContactInfo(),
                doc.getActiveFundIds() != null ? new ArrayList<>(doc.getActiveFundIds()) : new ArrayList<>(),
                doc.getEmail(),
                doc.getPassword()
        );
    }
}
