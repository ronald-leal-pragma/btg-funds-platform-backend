package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Profile("!aws")
@Repository
@RequiredArgsConstructor
public class MongoClientRepository implements ClientRepository {

    private final SpringClientRepository springRepository;

    @Override
    public Optional<Client> findById(String id) {
        log.debug("[REPO] MongoClientRepository.findById: id={}", id);
        return springRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Client save(Client client) {
        log.debug("[REPO] MongoClientRepository.save: id={}", client.id());
        return toDomain(springRepository.save(toDocument(client)));
    }

    private ClientDocument toDocument(Client client) {
        return new ClientDocument(
                client.id(),
                client.balance(),
                client.notificationPreference(),
                client.contactInfo(),
                new ArrayList<>(client.activeFundIds())
        );
    }

    private Client toDomain(ClientDocument doc) {
        return new Client(
                doc.getId(),
                doc.getBalance(),
                doc.getNotificationPreference(),
                doc.getContactInfo(),
                doc.getActiveFundIds() != null ? new ArrayList<>(doc.getActiveFundIds()) : new ArrayList<>()
        );
    }
}
