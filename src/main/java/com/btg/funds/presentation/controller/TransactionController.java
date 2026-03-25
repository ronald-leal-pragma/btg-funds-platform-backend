package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.GetTransactionsUseCase;
import com.btg.funds.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Historial de transacciones")
public class TransactionController {

    private final GetTransactionsUseCase getTransactionsUseCase;

    @GetMapping
    @Operation(summary = "Obtener historial de transacciones")
    public ResponseEntity<List<Transaction>> listTransactions() {
        return ResponseEntity.ok(getTransactionsUseCase.execute());
    }
}
