package com.btg.funds.presentation.controller;

import com.btg.funds.application.usecase.GetClientUseCase;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.service.FundDomainException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean GetClientUseCase getClientUseCase;

    @Test
    void should_return_client_with_200() throws Exception {
        Client client = new Client("1", 500_000L, "email", "user@test.com", List.of("1", "3"));
        when(getClientUseCase.execute()).thenReturn(client);

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.balance").value(500000))
                .andExpect(jsonPath("$.notificationPreference").value("email"))
                .andExpect(jsonPath("$.contactInfo").value("user@test.com"));
    }

    @Test
    void should_include_active_fund_ids_in_response() throws Exception {
        Client client = new Client("1", 425_000L, "sms", "+573001234567", List.of("2", "5"));
        when(getClientUseCase.execute()).thenReturn(client);

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeFundIds").isArray())
                .andExpect(jsonPath("$.activeFundIds.length()").value(2));
    }

    @Test
    void should_return_400_when_client_not_found() throws Exception {
        when(getClientUseCase.execute())
                .thenThrow(new FundDomainException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }
}
