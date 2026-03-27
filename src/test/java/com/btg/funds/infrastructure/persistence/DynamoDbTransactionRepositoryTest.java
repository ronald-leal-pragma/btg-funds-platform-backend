package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.infrastructure.persistence.item.TransactionItem;
import com.btg.funds.infrastructure.persistence.repository.DynamoDbTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbTransactionRepositoryTest {

    @Mock
    DynamoDbEnhancedClient enhancedClient;

    @Mock
    DynamoDbTable<TransactionItem> table;

    @Mock
    PageIterable<TransactionItem> pageIterable;

    DynamoDbTransactionRepository repository;

    @BeforeEach
    void setUp() {
        when(enhancedClient.table(eq("Transactions"), any(TableSchema.class))).thenReturn(table);
        repository = new DynamoDbTransactionRepository(enhancedClient, "Transactions");
    }

    @Test
    void should_save_transaction_and_return_domain_object() {
        var now = Instant.now();
        var transaction = new Transaction("uuid-1", Transaction.TransactionType.APERTURA,
                "1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, now);

        Transaction result = repository.save(transaction);

        verify(table).putItem(any(TransactionItem.class));
        assertThat(result.id()).isEqualTo("uuid-1");
        assertThat(result.type()).isEqualTo(Transaction.TransactionType.APERTURA);
        assertThat(result.amount()).isEqualTo(75_000L);
    }

    @Test
    void should_map_transaction_to_item_correctly_on_save() {
        var now = Instant.now();
        var transaction = new Transaction("uuid-2", Transaction.TransactionType.CANCELACION,
                "2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, now);

        repository.save(transaction);

        var captor = ArgumentCaptor.forClass(TransactionItem.class);
        verify(table).putItem(captor.capture());
        assertThat(captor.getValue().getClientId()).isEqualTo("1");
        assertThat(captor.getValue().getTransactionId()).isEqualTo("uuid-2");
        assertThat(captor.getValue().getType()).isEqualTo("CANCELACION");
        assertThat(captor.getValue().getTimestamp()).isEqualTo(now.toString());
        assertThat(captor.getValue().getAmount()).isEqualTo(125_000L);
    }

    @Test
    void should_return_all_transactions_sorted_desc() {
        var now = Instant.now();
        SdkIterable<TransactionItem> items = List.of(
                buildItem("uuid-1", "APERTURA", "1", "FPV_BTG", 75_000L, now),
                buildItem("uuid-2", "CANCELACION", "1", "FPV_BTG", 75_000L, now.minusSeconds(10))
        )::iterator;
        when(pageIterable.items()).thenReturn(items);
        when(table.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);

        List<Transaction> result = repository.findAllSortedByTimestampDesc();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(Transaction.TransactionType.APERTURA);
        assertThat(result.get(1).type()).isEqualTo(Transaction.TransactionType.CANCELACION);
    }

    @Test
    void should_return_empty_list_when_no_transactions() {
        SdkIterable<TransactionItem> empty = List.<TransactionItem>of()::iterator;
        when(pageIterable.items()).thenReturn(empty);
        when(table.query(any(QueryEnhancedRequest.class))).thenReturn(pageIterable);

        List<Transaction> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    private TransactionItem buildItem(String txId, String type, String fundId,
                                      String fundName, long amount, Instant timestamp) {
        return new TransactionItem("1", timestamp.toString(), txId, type, fundId, fundName, amount);
    }
}
