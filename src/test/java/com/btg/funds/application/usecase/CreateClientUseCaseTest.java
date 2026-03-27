package com.btg.funds.application.usecase;

import com.btg.funds.application.dto.CreateClientRequest;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

    @Mock
    ClientRepository clientRepository;

    @InjectMocks
    CreateClientUseCase useCase;

    @Test
    void should_create_client_with_initial_balance_when_request_is_valid() {
        var request = new CreateClientRequest("user@test.com", "pass123", "email", "user@test.com");
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Client result = useCase.execute(request);

        assertThat(result.balance()).isEqualTo(500_000L);
        assertThat(result.notificationPreference()).isEqualTo("email");
        assertThat(result.contactInfo()).isEqualTo("user@test.com");
        assertThat(result.email()).isEqualTo("user@test.com");
        assertThat(result.activeFundIds()).isEmpty();
    }

    @Test
    void should_generate_uuid_as_client_id_when_creating() {
        var request = new CreateClientRequest("user@test.com", "pass123", "sms", "+573001234567");
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Client result = useCase.execute(request);

        assertThat(result.id()).isNotNull().isNotBlank();
        assertThat(result.id()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    void should_persist_client_with_sms_preference_when_requested() {
        var request = new CreateClientRequest("user@test.com", "pass123", "sms", "+573001234567");
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Client result = useCase.execute(request);

        assertThat(result.notificationPreference()).isEqualTo("sms");
        assertThat(result.contactInfo()).isEqualTo("+573001234567");
    }

    @Test
    void should_call_repository_save_when_creating_client() {
        var request = new CreateClientRequest("user@test.com", "pass123", "email", "user@test.com");
        when(clientRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(request);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().balance()).isEqualTo(500_000L);
    }

    @Test
    void should_throw_when_email_already_exists() {
        var request = new CreateClientRequest("existing@test.com", "pass123", "email", "existing@test.com");
        var existing = new Client("1", 500_000L, "email", "existing@test.com", java.util.List.of(), "existing@test.com", "pass123");
        when(clientRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(FundDomainException.class)
                .hasMessageContaining("Ya existe una cuenta con ese correo");
    }
}
