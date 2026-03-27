package com.btg.funds.presentation.controller;

import com.btg.funds.application.dto.TransactionResponse;
import com.btg.funds.application.mapper.TransactionMapper;
import com.btg.funds.application.usecase.GetTransactionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transactions", description = "Historial de transacciones")
public class TransactionController {

    private final GetTransactionsUseCase getTransactionsUseCase;
    private final TransactionMapper transactionMapper;

    @GetMapping
    @Operation(summary = "Obtener historial de transacciones")
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestParam String clientId,
            @RequestParam(value = "sort", required = false) String sort) {
        log.info("[REST] GET /api/v1/transactions - clientId={}, sort={}", clientId, sort);
        var list = getTransactionsUseCase.execute(clientId, sort).stream()
                .map(transactionMapper::toResponse)
                .toList();
        log.info("[REST] GET /api/v1/transactions - Respuesta enviada: total={}", list.size());
        return ResponseEntity.ok(list);
    }
}
