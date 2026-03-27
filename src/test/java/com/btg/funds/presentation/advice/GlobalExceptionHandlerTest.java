package com.btg.funds.presentation.advice;

import com.btg.funds.application.mapper.ClientMapper;
import com.btg.funds.application.usecase.GetClientUseCase;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.presentation.controller.ClientController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@Import(ClientMapper.class)
class GlobalExceptionHandlerTest {

    @Autowired MockMvc mockMvc;

    @MockBean GetClientUseCase getClientUseCase;

    @Test
    void should_return_400_with_message_when_domain_exception() throws Exception {
        when(getClientUseCase.execute())
                .thenThrow(new FundDomainException("Error de dominio"));

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de dominio"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void should_return_500_with_generic_message_when_unexpected_exception() throws Exception {
        when(getClientUseCase.execute())
                .thenThrow(new RuntimeException("error inesperado"));

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error interno del servidor"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void should_not_expose_internal_error_details_in_500_response() throws Exception {
        when(getClientUseCase.execute())
                .thenThrow(new RuntimeException("NullPointerException: sensitive internal detail"));

        mockMvc.perform(get("/api/v1/client"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));
    }
}
