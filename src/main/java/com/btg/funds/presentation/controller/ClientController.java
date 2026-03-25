package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.GetClientUseCase;
import com.btg.funds.domain.model.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Estado del cliente")
public class ClientController {

    private final GetClientUseCase getClientUseCase;

    @GetMapping
    @Operation(summary = "Obtener estado del cliente (balance, fondos activos)")
    public ResponseEntity<Client> getClient() {
        return ResponseEntity.ok(getClientUseCase.execute());
    }
}
