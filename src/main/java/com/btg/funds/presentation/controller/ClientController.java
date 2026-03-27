package com.btg.funds.presentation.controller;

import com.btg.funds.application.dto.ClientResponse;
import com.btg.funds.application.dto.CreateClientRequest;
import com.btg.funds.application.dto.LoginRequest;
import com.btg.funds.application.mapper.ClientMapper;
import com.btg.funds.application.usecase.CreateClientUseCase;
import com.btg.funds.application.usecase.GetClientUseCase;
import com.btg.funds.application.usecase.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Client", description = "Gestión del cliente")
public class ClientController {

    private final GetClientUseCase getClientUseCase;
    private final CreateClientUseCase createClientUseCase;
    private final LoginUseCase loginUseCase;
    private final ClientMapper clientMapper;

    @GetMapping("/{clientId}")
    @Operation(summary = "Obtener estado del cliente (balance, fondos activos)")
    public ResponseEntity<ClientResponse> getClient(@PathVariable String clientId) {
        log.info("[REST] GET /api/v1/client/{} - Solicitud estado de cliente", clientId);
        var resp = clientMapper.toResponse(getClientUseCase.execute(clientId));
        log.info("[REST] GET /api/v1/client/{} - Respuesta enviada: balance={}", resp.id(), resp.balance());
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo cliente con balance inicial de COP $500.000")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody CreateClientRequest request) {
        log.info("[REST] POST /api/v1/client - Creando cliente con preferencia={}", request.notificationPreference());
        var resp = clientMapper.toResponse(createClientUseCase.execute(request));
        log.info("[REST] POST /api/v1/client - Cliente creado: id={}", resp.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    @Operation(summary = "Ingresar con correo y contraseña — retorna el cliente con su ID")
    public ResponseEntity<ClientResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("[REST] POST /api/v1/client/login - Intento de ingreso: email={}", request.email());
        var resp = clientMapper.toResponse(loginUseCase.execute(request));
        log.info("[REST] POST /api/v1/client/login - Ingreso exitoso: clientId={}", resp.id());
        return ResponseEntity.ok(resp);
    }
}
