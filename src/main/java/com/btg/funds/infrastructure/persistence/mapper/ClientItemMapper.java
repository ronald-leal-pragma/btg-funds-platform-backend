package com.btg.funds.infrastructure.persistence.mapper;

import com.btg.funds.domain.model.Client;
import com.btg.funds.infrastructure.persistence.item.ClientItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ClientItemMapper {

    public ClientItem toItem(Client client) {
        return new ClientItem(
                client.id(),
                client.balance(),
                client.notificationPreference(),
                client.contactInfo(),
                new ArrayList<>(client.activeFundIds()),
                client.email(),
                client.password()
        );
    }

    public Client toDomain(ClientItem item) {
        return new Client(
                item.getId(),
                item.getBalance(),
                item.getNotificationPreference(),
                item.getContactInfo(),
                item.getActiveFundIds() != null ? new ArrayList<>(item.getActiveFundIds()) : new ArrayList<>(),
                item.getEmail(),
                item.getPassword()
        );
    }
}
