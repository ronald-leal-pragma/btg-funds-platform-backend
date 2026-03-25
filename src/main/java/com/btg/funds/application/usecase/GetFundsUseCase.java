package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.service.FundDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetFundsUseCase {

    private static final String CLIENT_ID = "1";

    private final FundRepository fundRepository;
    private final ClientRepository clientRepository;

    public record FundWithStatus(Fund fund, boolean subscribed) {}

    public List<FundWithStatus> execute() {
        Client client = clientRepository.findById(CLIENT_ID)
                .orElseThrow(() -> new FundDomainException("Cliente no encontrado"));

        return fundRepository.findAll().stream()
                .map(fund -> new FundWithStatus(fund, client.isSubscribedTo(fund.id())))
                .toList();
    }
}
