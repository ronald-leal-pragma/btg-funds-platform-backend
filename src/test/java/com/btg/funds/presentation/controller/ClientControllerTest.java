package com.btg.funds.presentation.controller;

import com.btg.funds.application.mapper.ClientMapper;
import com.btg.funds.application.usecase.CreateClientUseCase;
import com.btg.funds.application.usecase.GetClientUseCase;
import com.btg.funds.application.usecase.LoginUseCase;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.domain.model.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(ClientMapper.class)
class ClientControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean GetClientUseCase getClientUseCase;
    @MockBean CreateClientUseCase createClientUseCase;
    @MockBean LoginUseCase loginUseCase;

    @Test
    void should_return_client_with_200() throws Exception {
        Client client = new Client("1", 500_000L, "email", "user@test.com", List.of("1", "3"), "user@test.com", "pass123");
        when(getClientUseCase.execute(anyString())).thenReturn(client);

        mockMvc.perform(get("/api/v1/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.balance").value(500000))
                .andExpect(jsonPath("$.notificationPreference").value("email"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void should_include_active_fund_ids_in_response() throws Exception {
        Client client = new Client("1", 425_000L, "sms", "+573001234567", List.of("2", "5"), "user@test.com", "pass123");
        when(getClientUseCase.execute(anyString())).thenReturn(client);

        mockMvc.perform(get("/api/v1/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeFundIds").isArray())
                .andExpect(jsonPath("$.activeFundIds.length()").value(2));
    }

    @Test
    void should_return_400_when_client_not_found() throws Exception {
        when(getClientUseCase.execute(anyString()))
            .thenThrow(new FundDomainException("Cliente no encontrado"));

        mockMvc.perform(get("/api/v1/client/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }

    @Test
    void should_login_and_return_client_with_200() throws Exception {
        Client client = new Client("uuid-1", 500_000L, "email", "user@test.com", List.of(), "user@test.com", "pass123");
        when(loginUseCase.execute(any())).thenReturn(client);

        mockMvc.perform(post("/api/v1/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@test.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-1"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void should_return_400_when_login_fails() throws Exception {
        when(loginUseCase.execute(any()))
                .thenThrow(new FundDomainException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/client/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }
}
