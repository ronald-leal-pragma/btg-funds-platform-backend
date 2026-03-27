package com.btg.funds.application.usecase;

import com.btg.funds.application.dto.LoginRequest;
import com.btg.funds.application.port.in.LoginPort;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase implements LoginPort {

    private final ClientRepository clientRepository;

    @Override
    public Client execute(LoginRequest request) {
        log.info("[USECASE] Login - Intento de ingreso: email={}", request.email());

        Client client = clientRepository.findByEmail(request.email())
                .orElseThrow(() -> new FundDomainException("Credenciales inválidas"));

        if (!request.password().equals(client.password())) {
            log.warn("[USECASE] Login - Contraseña incorrecta: email={}", request.email());
            throw new FundDomainException("Credenciales inválidas");
        }

        log.info("[USECASE] Login - Ingreso exitoso: clientId={}", client.id());
        return client;
    }
}
