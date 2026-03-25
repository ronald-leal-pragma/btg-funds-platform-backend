package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.service.FundDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetClientUseCase {

    private static final String CLIENT_ID = "1";

    private final ClientRepository clientRepository;

    public Client execute() {
        return clientRepository.findById(CLIENT_ID)
                .orElseThrow(() -> new FundDomainException("Cliente no encontrado"));
    }
}
