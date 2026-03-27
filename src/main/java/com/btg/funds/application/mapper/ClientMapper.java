package com.btg.funds.application.mapper;

import com.btg.funds.application.dto.ClientResponse;
import com.btg.funds.domain.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.id(),
                client.balance(),
                client.notificationPreference(),
                client.contactInfo(),
                client.activeFundIds(),
                client.email()
        );
    }
}
