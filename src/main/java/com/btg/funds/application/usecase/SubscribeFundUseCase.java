package com.btg.funds.application.usecase;

import com.btg.funds.application.port.NotificationPort;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.model.Transaction.TransactionType;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.domain.exception.FondoNoEncontradoException;
import com.btg.funds.domain.exception.SaldoInsuficienteException;
import com.btg.funds.domain.exception.FundDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeFundUseCase {

    private static final String CLIENT_ID = "1";

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationPort notificationPort;

    public Transaction execute(String fundId) {
        log.info("[USECASE] SubscribeFund - Solicitud para suscribirse: fundId={}", fundId);
        Client client = clientRepository.findById(CLIENT_ID)
                .orElseThrow(() -> new FondoNoEncontradoException("Cliente no encontrado"));
        log.debug("[USECASE] SubscribeFund - Cliente cargado: id={}, balance={}, suscripciones={}", client.id(), client.balance(), client.activeFundIds());

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FondoNoEncontradoException("Fondo no encontrado: " + fundId));
        log.debug("[USECASE] SubscribeFund - Fondo cargado: id={}, nombre={}, montoMinimo={}", fund.id(), fund.name(), fund.minAmount());

        if (client.isSubscribedTo(fundId)) {
            log.warn("[USECASE] SubscribeFund - Cliente {} ya está suscrito al fondo {}", client.id(), fundId);
            throw new FundDomainException("Ya está suscrito al fondo " + fund.name());
        }

        if (!client.hasEnoughBalance(fund.minAmount())) {
            log.warn("[USECASE] SubscribeFund - Saldo insuficiente: cliente={}, balance={}, requerido={}", client.id(), client.balance(), fund.minAmount());
            throw new SaldoInsuficienteException("No tiene saldo disponible para vincularse al fondo " + fund.name());
        }

        Client updated = client.deductBalance(fund.minAmount()).addFund(fundId);
        clientRepository.save(updated);
        log.info("[USECASE] SubscribeFund - Suscripción realizada: cliente={}, fondo={}, nuevoBalance={}", client.id(), fundId, updated.balance());

        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.APERTURA,
                fund.id(),
                fund.name(),
                fund.minAmount(),
                Instant.now()
        );
        transactionRepository.save(transaction);
        log.debug("[USECASE] SubscribeFund - Transacción guardada: id={}, tipo={}, monto={}", transaction.id(), transaction.type(), transaction.amount());

        notificationPort.notifySubscription(updated, fund);
        log.info("[USECASE] SubscribeFund - Notificación enviada: cliente={}, fondo={}", client.id(), fundId);

        return transaction;
    }
}
