package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.exception.FondoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetClientUseCase implements com.btg.funds.application.port.in.GetClientPort {

    private final ClientRepository clientRepository;

    public Client execute(String clientId) {
        log.info("[USECASE] GetClient - Solicitud de estado de cliente: id={}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new FondoNoEncontradoException("Cliente no encontrado: " + clientId));
        log.debug("[USECASE] GetClient - Cliente cargado: id={}, balance={}, suscripciones={}", client.id(), client.balance(), client.activeFundIds());
        return client;
    }
}
