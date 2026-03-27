package com.btg.funds.domain.repository;

import com.btg.funds.domain.model.Client;

import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findById(String id);
    Optional<Client> findByEmail(String email);
    Client save(Client client);
}
