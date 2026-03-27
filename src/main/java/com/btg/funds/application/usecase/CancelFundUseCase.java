package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.model.Transaction.TransactionType;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.domain.exception.FondoNoEncontradoException;
import com.btg.funds.domain.exception.FundDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelFundUseCase implements com.btg.funds.application.port.in.CancelFundPort {

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final TransactionRepository transactionRepository;

    public Transaction execute(String clientId, String fundId) {
        log.info("[USECASE] CancelFund - Solicitud para cancelar: clientId={}, fundId={}", clientId, fundId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new FondoNoEncontradoException("Cliente no encontrado: " + clientId));
        log.debug("[USECASE] CancelFund - Cliente cargado: id={}, balance={}, suscripciones={}", client.id(), client.balance(), client.activeFundIds());

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FondoNoEncontradoException("Fondo no encontrado: " + fundId));
        log.debug("[USECASE] CancelFund - Fondo cargado: id={}, nombre={}, montoMinimo={}", fund.id(), fund.name(), fund.minAmount());

        if (!client.isSubscribedTo(fundId)) {
            log.warn("[USECASE] CancelFund - Cliente {} no está suscrito al fondo {}", client.id(), fundId);
            throw new FundDomainException("No está suscrito al fondo " + fund.name());
        }

        Client updated = client.refundBalance(fund.minAmount()).removeFund(fundId);
        clientRepository.save(updated);
        log.info("[USECASE] CancelFund - Cancelación realizada: cliente={}, fondo={}, nuevoBalance={}", client.id(), fundId, updated.balance());

        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                clientId,
                TransactionType.CANCELACION,
                fund.id(),
                fund.name(),
                fund.minAmount(),
                Instant.now()
        );
        transactionRepository.save(transaction);
        log.debug("[USECASE] CancelFund - Transacción guardada: id={}, tipo={}, monto={}", transaction.id(), transaction.type(), transaction.amount());

        return transaction;
    }
}
