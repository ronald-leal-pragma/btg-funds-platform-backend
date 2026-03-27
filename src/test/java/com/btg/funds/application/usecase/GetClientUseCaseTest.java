package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.exception.FundDomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetClientUseCaseTest {

    @Mock ClientRepository clientRepository;

    @InjectMocks GetClientUseCase useCase;

    @Test
    void should_return_client_when_found() {
        Client client = new Client("1", 500_000L, "email", "user@test.com", List.of(), "user@test.com", "pass123");
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        Client result = useCase.execute("1");

        assertThat(result).isEqualTo(client);
        assertThat(result.balance()).isEqualTo(500_000L);
    }

    @Test
    void should_throw_when_client_not_found() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("1"))
            .isInstanceOf(FundDomainException.class)
            .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void should_query_with_fixed_client_id() {
        Client client = new Client("1", 500_000L, "sms", "+573001234567", List.of("2"), "user@test.com", "pass123");
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));

        useCase.execute("1");

        verify(clientRepository).findById("1");
    }
}
