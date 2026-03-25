package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.CancelFundUseCase;
import com.btg.funds.application.usecase.GetFundsUseCase;
import com.btg.funds.application.usecase.SubscribeFundUseCase;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.service.FundDomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FundController.class)
class FundControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean GetFundsUseCase getFundsUseCase;
    @MockBean SubscribeFundUseCase subscribeFundUseCase;
    @MockBean CancelFundUseCase cancelFundUseCase;

    @Test
    void should_list_funds_with_200() throws Exception {
        Fund fund = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");
        when(getFundsUseCase.execute()).thenReturn(List.of(
                new GetFundsUseCase.FundWithStatus(fund, false)
        ));

        mockMvc.perform(get("/api/v1/funds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fund.id").value("1"))
                .andExpect(jsonPath("$[0].fund.name").value("FPV_BTG_PACTUAL_RECAUDADORA"))
                .andExpect(jsonPath("$[0].subscribed").value(false));
    }

    @Test
    void should_return_empty_list_when_no_funds() throws Exception {
        when(getFundsUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/funds"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void should_subscribe_to_fund_with_200() throws Exception {
        Transaction tx = new Transaction("uuid-1", Transaction.TransactionType.APERTURA,
                "1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, Instant.parse("2024-01-01T00:00:00Z"));
        when(subscribeFundUseCase.execute("1")).thenReturn(tx);

        mockMvc.perform(post("/api/v1/funds/1/subscribe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.type").value("APERTURA"))
                .andExpect(jsonPath("$.amount").value(75000));
    }

    @Test
    void should_return_400_when_subscribe_throws_domain_exception() throws Exception {
        when(subscribeFundUseCase.execute("1"))
                .thenThrow(new FundDomainException("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA"));

        mockMvc.perform(post("/api/v1/funds/1/subscribe"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA"));
    }

    @Test
    void should_cancel_fund_with_200() throws Exception {
        Transaction tx = new Transaction("uuid-2", Transaction.TransactionType.CANCELACION,
                "1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, Instant.parse("2024-01-01T00:00:00Z"));
        when(cancelFundUseCase.execute("1")).thenReturn(tx);

        mockMvc.perform(delete("/api/v1/funds/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("CANCELACION"));
    }

    @Test
    void should_return_400_when_cancel_throws_domain_exception() throws Exception {
        when(cancelFundUseCase.execute("1"))
                .thenThrow(new FundDomainException("No está suscrito al fondo FPV_BTG_PACTUAL_RECAUDADORA"));

        mockMvc.perform(delete("/api/v1/funds/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No está suscrito al fondo FPV_BTG_PACTUAL_RECAUDADORA"));
    }
}
