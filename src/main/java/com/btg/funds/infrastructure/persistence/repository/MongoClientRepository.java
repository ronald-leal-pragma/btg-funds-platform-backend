package com.btg.funds.infrastructure.persistence.repository;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.infrastructure.persistence.mapper.ClientDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Profile("!aws")
@Repository
@RequiredArgsConstructor
public class MongoClientRepository implements ClientRepository {

    private final SpringClientRepository springRepository;
    private final ClientDocumentMapper mapper;

    @Override
    public Optional<Client> findById(String id) {
        log.debug("[REPO] MongoClientRepository.findById: id={}", id);
        return springRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        log.debug("[REPO] MongoClientRepository.findByEmail: email={}", email);
        return springRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Client save(Client client) {
        log.debug("[REPO] MongoClientRepository.save: id={}", client.id());
        return mapper.toDomain(springRepository.save(mapper.toDocument(client)));
    }
}
