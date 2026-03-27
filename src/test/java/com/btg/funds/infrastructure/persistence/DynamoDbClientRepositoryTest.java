package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Client;
import com.btg.funds.infrastructure.persistence.item.ClientItem;
import com.btg.funds.infrastructure.persistence.mapper.ClientItemMapper;
import com.btg.funds.infrastructure.persistence.repository.DynamoDbClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbClientRepositoryTest {

    @Mock
    DynamoDbEnhancedClient enhancedClient;

    @Mock
    DynamoDbTable<ClientItem> table;

    DynamoDbClientRepository repository;

    @BeforeEach
    void setUp() {
        when(enhancedClient.table(eq("Clients"), any(TableSchema.class))).thenReturn(table);
        repository = new DynamoDbClientRepository(enhancedClient, "Clients", new ClientItemMapper());
    }

    @Test
    void should_return_client_when_found() {
        var item = new ClientItem("1", 500_000L, "email", "u@test.com", new ArrayList<>(List.of("1")), "u@test.com", "pass123");
        when(table.getItem(any(Key.class))).thenReturn(item);

        Optional<Client> result = repository.findById("1");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("1");
        assertThat(result.get().balance()).isEqualTo(500_000L);
        assertThat(result.get().notificationPreference()).isEqualTo("email");
        assertThat(result.get().contactInfo()).isEqualTo("u@test.com");
        assertThat(result.get().activeFundIds()).containsExactly("1");
        assertThat(result.get().email()).isEqualTo("u@test.com");
    }

    @Test
    void should_return_empty_when_not_found() {
        when(table.getItem(any(Key.class))).thenReturn(null);

        Optional<Client> result = repository.findById("99");

        assertThat(result).isEmpty();
    }

    @Test
    void should_handle_null_active_fund_ids() {
        var item = new ClientItem("1", 500_000L, "sms", "+573001234567", null, "u@test.com", "pass123");
        when(table.getItem(any(Key.class))).thenReturn(item);

        Optional<Client> result = repository.findById("1");

        assertThat(result).isPresent();
        assertThat(result.get().activeFundIds()).isEmpty();
    }

    @Test
    void should_save_and_return_client() {
        var client = new Client("1", 425_000L, "email", "u@test.com", List.of("1"), "u@test.com", "pass123");

        Client result = repository.save(client);

        verify(table).putItem(any(ClientItem.class));
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.balance()).isEqualTo(425_000L);
        assertThat(result.activeFundIds()).containsExactly("1");
    }
}
