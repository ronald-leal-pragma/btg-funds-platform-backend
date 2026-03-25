package com.btg.funds.application.usecase;

import com.btg.funds.application.port.NotificationPort;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.model.Transaction.TransactionType;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.domain.service.FundDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscribeFundUseCase {

    private static final String CLIENT_ID = "1";

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationPort notificationPort;

    public Transaction execute(String fundId) {
        Client client = clientRepository.findById(CLIENT_ID)
                .orElseThrow(() -> new FundDomainException("Cliente no encontrado"));

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new FundDomainException("Fondo no encontrado: " + fundId));

        if (client.isSubscribedTo(fundId)) {
            throw new FundDomainException("Ya está suscrito al fondo " + fund.name());
        }

        if (!client.hasEnoughBalance(fund.minAmount())) {
            throw new FundDomainException(
                    "No tiene saldo disponible para vincularse al fondo " + fund.name());
        }

        Client updated = client.deductBalance(fund.minAmount()).addFund(fundId);
        clientRepository.save(updated);

        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                TransactionType.APERTURA,
                fund.id(),
                fund.name(),
                fund.minAmount(),
                Instant.now()
        );
        transactionRepository.save(transaction);

        notificationPort.notifySubscription(updated, fund);

        return transaction;
    }
}
