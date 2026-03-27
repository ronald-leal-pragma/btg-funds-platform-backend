package com.btg.funds.application.usecase;

import com.btg.funds.application.dto.CreateClientRequest;
import com.btg.funds.application.port.in.CreateClientPort;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateClientUseCase implements CreateClientPort {

    private static final long INITIAL_BALANCE = 500_000L;

    private final ClientRepository clientRepository;

    @Override
    public Client execute(CreateClientRequest request) {
        log.info("[USECASE] CreateClient - Creando cliente con email={}, preferencia={}", request.email(), request.notificationPreference());

        if (clientRepository.findByEmail(request.email()).isPresent()) {
            throw new FundDomainException("Ya existe una cuenta con ese correo");
        }

        String id = UUID.randomUUID().toString();

        var client = new Client(
                id,
                INITIAL_BALANCE,
                request.notificationPreference(),
                request.contactInfo(),
                new ArrayList<>(),
                request.email(),
                request.password()
        );

        Client saved = clientRepository.save(client);
        log.info("[USECASE] CreateClient - Cliente creado: id={}, email={}", saved.id(), saved.email());
        return saved;
    }
}
