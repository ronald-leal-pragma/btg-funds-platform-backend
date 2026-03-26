package com.btg.funds.presentation.controller;

import com.btg.funds.application.dto.ClientResponse;
import com.btg.funds.application.mapper.ClientMapper;
import com.btg.funds.application.usecase.GetClientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client", description = "Estado del cliente")
public class ClientController {

    private final GetClientUseCase getClientUseCase;
    private final ClientMapper clientMapper;

    @GetMapping
    @Operation(summary = "Obtener estado del cliente (balance, fondos activos)")
    public ResponseEntity<ClientResponse> getClient() {
        log.info("[REST] GET /api/v1/client - Solicitud estado de cliente");
        var resp = clientMapper.toResponse(getClientUseCase.execute());
        log.info("[REST] GET /api/v1/client - Respuesta enviada: cliente={}, balance={}", resp.id(), resp.balance());
        return ResponseEntity.ok(resp);
    }
}
