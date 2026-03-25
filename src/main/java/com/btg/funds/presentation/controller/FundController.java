package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.CancelFundUseCase;
import com.btg.funds.application.usecase.GetFundsUseCase;
import com.btg.funds.application.usecase.SubscribeFundUseCase;
import com.btg.funds.domain.model.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funds")
@RequiredArgsConstructor
@Tag(name = "Funds", description = "Gestión de fondos de inversión")
public class FundController {

    private final GetFundsUseCase getFundsUseCase;
    private final SubscribeFundUseCase subscribeFundUseCase;
    private final CancelFundUseCase cancelFundUseCase;

    @GetMapping
    @Operation(summary = "Listar todos los fondos con estado de suscripción")
    public ResponseEntity<List<GetFundsUseCase.FundWithStatus>> listFunds() {
        return ResponseEntity.ok(getFundsUseCase.execute());
    }

    @PostMapping("/{id}/subscribe")
    @Operation(summary = "Suscribirse a un fondo")
    public ResponseEntity<Transaction> subscribe(@PathVariable String id) {
        return ResponseEntity.ok(subscribeFundUseCase.execute(id));
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancelar suscripción a un fondo")
    public ResponseEntity<Transaction> cancel(@PathVariable String id) {
        return ResponseEntity.ok(cancelFundUseCase.execute(id));
    }
}
