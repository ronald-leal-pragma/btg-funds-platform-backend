package com.btg.funds.application.usecase;

import com.btg.funds.application.dto.FundWithStatusResponse;
import com.btg.funds.application.mapper.FundMapper;
import com.btg.funds.domain.exception.FondoNoEncontradoException;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetFundsUseCase implements com.btg.funds.application.port.in.GetFundsPort {

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;
    private final FundMapper fundMapper;

    public List<FundWithStatusResponse> execute(String clientId) {
        log.info("[USECASE] GetFunds - Solicitud listado de fondos para cliente: id={}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new FondoNoEncontradoException("Cliente no encontrado: " + clientId));
        log.debug("[USECASE] GetFunds - Cliente cargado: id={}, balance={}, suscripciones={}", client.id(), client.balance(), client.activeFundIds());

        return fundRepository.findAll().stream()
                .map(fund -> new FundWithStatusResponse(
                        fundMapper.toResponse(fund),
                        client.isSubscribedTo(fund.id())
                ))
                .toList();
    }

    /**
     * Lista todos los fondos sin necesidad de un cliente — marcado como no suscrito.
     */
    public List<FundWithStatusResponse> execute() {
        log.info("[USECASE] GetFunds - Solicitud listado de todos los fondos (sin cliente)");
        return fundRepository.findAll().stream()
                .map(fund -> new FundWithStatusResponse(
                        fundMapper.toResponse(fund),
                        false
                ))
                .toList();
    }
}
