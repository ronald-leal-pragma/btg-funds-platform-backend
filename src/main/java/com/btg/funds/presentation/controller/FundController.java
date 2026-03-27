package com.btg.funds.presentation.controller;

import com.btg.funds.application.dto.FundWithStatusResponse;
import com.btg.funds.application.dto.TransactionResponse;
import com.btg.funds.application.mapper.TransactionMapper;
import com.btg.funds.application.usecase.CancelFundUseCase;
import com.btg.funds.application.usecase.GetFundsUseCase;
import com.btg.funds.application.usecase.SubscribeFundUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funds")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Funds", description = "Gestión de fondos de inversión")
public class FundController {

    private final GetFundsUseCase getFundsUseCase;
    private final SubscribeFundUseCase subscribeFundUseCase;
    private final CancelFundUseCase cancelFundUseCase;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = "Listar todos los fondos con estado de suscripción del cliente")
    public ResponseEntity<List<FundWithStatusResponse>> listFunds(
            @RequestParam(required = false) String clientId) {
        if (clientId == null) {
            log.info("[REST] GET /api/v1/funds - Solicitud listado de fondos (todos)");
            var list = getFundsUseCase.execute();
            log.info("[REST] GET /api/v1/funds - Listado obtenido: total={}", list.size());
            return ResponseEntity.ok(list);
        }

        log.info("[REST] GET /api/v1/funds?clientId={} - Solicitud listado de fondos", clientId);
        var list = getFundsUseCase.execute(clientId);
        log.info("[REST] GET /api/v1/funds - Listado obtenido: total={}", list.size());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/{id}/subscribe")
    @Operation(summary = "Suscribirse a un fondo")
    public ResponseEntity<TransactionResponse> subscribe(
            @PathVariable String id,
            @RequestParam String clientId) {
        log.info("[REST] POST /api/v1/funds/{}/subscribe - clientId={}", id, clientId);
        var resp = transactionMapper.toResponse(subscribeFundUseCase.execute(clientId, id));
        log.info("[REST] POST /api/v1/funds/{}/subscribe - Suscripción realizada", id);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancelar suscripción a un fondo")
    public ResponseEntity<TransactionResponse> cancel(
            @PathVariable String id,
            @RequestParam String clientId) {
        log.info("[REST] DELETE /api/v1/funds/{}/cancel - clientId={}", id, clientId);
        var resp = transactionMapper.toResponse(cancelFundUseCase.execute(clientId, id));
        log.info("[REST] DELETE /api/v1/funds/{}/cancel - Cancelación realizada", id);
        return ResponseEntity.ok(resp);
    }
}
