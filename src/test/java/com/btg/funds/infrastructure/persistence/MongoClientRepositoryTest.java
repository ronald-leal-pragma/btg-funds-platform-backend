package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Client;
import com.btg.funds.infrastructure.persistence.document.ClientDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoClientRepositoryTest {

    @Mock SpringClientRepository springClientRepository;

    @InjectMocks MongoClientRepository repository;

    @Test
    void should_return_client_when_found() {
        ClientDocument doc = buildDocument("1", 500_000L, "email", "u@test.com", List.of("1"));
        when(springClientRepository.findById("1")).thenReturn(Optional.of(doc));

        Optional<Client> result = repository.findById("1");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("1");
        assertThat(result.get().balance()).isEqualTo(500_000L);
        assertThat(result.get().notificationPreference()).isEqualTo("email");
        assertThat(result.get().contactInfo()).isEqualTo("u@test.com");
        assertThat(result.get().activeFundIds()).containsExactly("1");
    }

    @Test
    void should_return_empty_when_not_found() {
        when(springClientRepository.findById("99")).thenReturn(Optional.empty());

        Optional<Client> result = repository.findById("99");

        assertThat(result).isEmpty();
    }

    @Test
    void should_handle_null_active_fund_ids_in_document() {
        ClientDocument doc = buildDocument("1", 500_000L, "sms", "+573001234567", null);
        when(springClientRepository.findById("1")).thenReturn(Optional.of(doc));

        Optional<Client> result = repository.findById("1");

        assertThat(result).isPresent();
        assertThat(result.get().activeFundIds()).isEmpty();
    }

    @Test
    void should_save_and_return_mapped_client() {
        Client client = new Client("1", 425_000L, "email", "u@test.com", List.of("1"));
        ClientDocument savedDoc = buildDocument("1", 425_000L, "email", "u@test.com", List.of("1"));
        when(springClientRepository.save(any())).thenReturn(savedDoc);

        Client result = repository.save(client);

        assertThat(result.id()).isEqualTo("1");
        assertThat(result.balance()).isEqualTo(425_000L);
        assertThat(result.activeFundIds()).containsExactly("1");
    }

    private ClientDocument buildDocument(String id, long balance, String pref, String contact, List<String> fundIds) {
        return ClientDocument.builder()
                .id(id)
                .balance(balance)
                .notificationPreference(pref)
                .contactInfo(contact)
                .activeFundIds(fundIds != null ? new java.util.ArrayList<>(fundIds) : null)
                .build();
    }
}
