package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoClientRepository implements ClientRepository {

    private final SpringClientRepository spring;

    @Override
    public Optional<Client> findById(String id) {
        return spring.findById(id).map(this::toDomain);
    }

    @Override
    public Client save(Client client) {
        ClientDocument doc = toDocument(client);
        return toDomain(spring.save(doc));
    }

    private Client toDomain(ClientDocument doc) {
        return new Client(
                doc.getId(),
                doc.getBalance(),
                doc.getNotificationPreference(),
                doc.getContactInfo(),
                doc.getActiveFundIds() != null ? doc.getActiveFundIds() : new ArrayList<>()
        );
    }

    private ClientDocument toDocument(Client client) {
        return ClientDocument.builder()
                .id(client.id())
                .balance(client.balance())
                .notificationPreference(client.notificationPreference())
                .contactInfo(client.contactInfo())
                .activeFundIds(new ArrayList<>(client.activeFundIds()))
                .build();
    }
}
