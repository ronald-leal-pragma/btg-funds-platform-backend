package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.mapper.ClientDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MongoClientRepository implements ClientRepository {

    private final SpringClientRepository spring;
    private final ClientDocumentMapper mapper;

    @Override
    public Optional<Client> findById(String id) {
        log.debug("[REPO] MongoClientRepository - findById: id={}", id);
        return spring.findById(id).map(mapper::toDomain);
    }

    @Override
    public Client save(Client client) {
        log.debug("[REPO] MongoClientRepository - save: id={}, balance={}", client.id(), client.balance());
        return mapper.toDomain(spring.save(mapper.toDocument(client)));
    }
}
