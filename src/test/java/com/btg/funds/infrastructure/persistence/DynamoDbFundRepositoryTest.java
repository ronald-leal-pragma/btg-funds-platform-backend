package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.infrastructure.persistence.item.FundItem;
import com.btg.funds.infrastructure.persistence.repository.DynamoDbFundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbFundRepositoryTest {

    @Mock
    DynamoDbEnhancedClient enhancedClient;

    @Mock
    DynamoDbTable<FundItem> table;

    @Mock
    PageIterable<FundItem> pageIterable;

    DynamoDbFundRepository repository;

    @BeforeEach
    void setUp() {
        when(enhancedClient.table(eq("Funds"), any(TableSchema.class))).thenReturn(table);
        repository = new DynamoDbFundRepository(enhancedClient, "Funds");
    }

    @Test
    void should_return_all_funds() {
        SdkIterable<FundItem> items = List.of(
                new FundItem("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV"),
                new FundItem("2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, "FPV")
        )::iterator;
        when(pageIterable.items()).thenReturn(items);
        when(table.scan()).thenReturn(pageIterable);

        List<Fund> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).name()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.get(0).minAmount()).isEqualTo(75_000L);
        assertThat(result.get(0).category()).isEqualTo("FPV");
    }

    @Test
    void should_return_empty_list_when_no_funds() {
        SdkIterable<FundItem> empty = List.<FundItem>of()::iterator;
        when(pageIterable.items()).thenReturn(empty);
        when(table.scan()).thenReturn(pageIterable);

        List<Fund> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_fund_by_id() {
        var item = new FundItem("3", "DEUDAPRIVADA", 50_000L, "FIC");
        when(table.getItem(any(Key.class))).thenReturn(item);

        Optional<Fund> result = repository.findById("3");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("3");
        assertThat(result.get().category()).isEqualTo("FIC");
    }

    @Test
    void should_return_empty_when_fund_not_found() {
        when(table.getItem(any(Key.class))).thenReturn(null);

        Optional<Fund> result = repository.findById("99");

        assertThat(result).isEmpty();
    }

    @Test
    void should_save_fund() {
        var fund = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");

        Fund result = repository.save(fund);

        verify(table).putItem(any(FundItem.class));
        assertThat(result.id()).isEqualTo("1");
    }
}
