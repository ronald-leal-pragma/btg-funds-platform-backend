package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.exception.FondoNoEncontradoException;
import com.btg.funds.domain.exception.FundDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetClientUseCase implements com.btg.funds.application.port.in.GetClientPort {

    private static final String CLIENT_ID = "1";

    private final ClientRepository clientRepository;

    public Client execute() {
        log.info("[USECASE] GetClient - Solicitud de estado de cliente");
        Client client = clientRepository.findById(CLIENT_ID)
                .orElseThrow(() -> new FondoNoEncontradoException("Cliente no encontrado"));
        log.debug("[USECASE] GetClient - Cliente cargado: id={}, balance={}, suscripciones={}", client.id(), client.balance(), client.activeFundIds());
        return client;
    }
}
