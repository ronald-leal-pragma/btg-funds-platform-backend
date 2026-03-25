package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.GetTransactionsUseCase;
import com.btg.funds.domain.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean GetTransactionsUseCase getTransactionsUseCase;

    @Test
    void should_list_transactions_with_200() throws Exception {
        List<Transaction> transactions = List.of(
                new Transaction("uuid-1", Transaction.TransactionType.APERTURA, "1",
                        "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, Instant.parse("2024-01-01T00:00:00Z")),
                new Transaction("uuid-2", Transaction.TransactionType.CANCELACION, "1",
                        "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, Instant.parse("2024-01-02T00:00:00Z"))
        );
        when(getTransactionsUseCase.execute()).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].type").value("APERTURA"))
                .andExpect(jsonPath("$[1].type").value("CANCELACION"));
    }

    @Test
    void should_return_empty_array_when_no_transactions() throws Exception {
        when(getTransactionsUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void should_include_amount_and_fund_name_in_response() throws Exception {
        when(getTransactionsUseCase.execute()).thenReturn(List.of(
                new Transaction("uuid-1", Transaction.TransactionType.APERTURA, "2",
                        "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, Instant.parse("2024-03-01T00:00:00Z"))
        ));

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fundName").value("FPV_BTG_PACTUAL_ECOPETROL"))
                .andExpect(jsonPath("$[0].amount").value(125000));
    }
}
