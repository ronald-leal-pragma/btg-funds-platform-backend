package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTransactionsUseCaseTest {

    @Mock TransactionRepository transactionRepository;

    @InjectMocks GetTransactionsUseCase useCase;

    @Test
    void should_return_all_transactions() {
        List<Transaction> transactions = List.of(
                new Transaction("uuid-1", "1", Transaction.TransactionType.APERTURA, "1", "FPV_BTG", 75_000L, Instant.now()),
                new Transaction("uuid-2", "1", Transaction.TransactionType.CANCELACION, "1", "FPV_BTG", 75_000L, Instant.now())
        );
        when(transactionRepository.findByClientIdSortedByTimestampDesc("1")).thenReturn(transactions);

        List<Transaction> result = useCase.execute("1", null);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(transactions);
    }

    @Test
    void should_return_empty_list_when_no_transactions() {
        when(transactionRepository.findByClientIdSortedByTimestampDesc("1")).thenReturn(List.of());

        List<Transaction> result = useCase.execute("1", null);

        assertThat(result).isEmpty();
    }

    @Test
    void should_delegate_to_repository_sorted_desc_by_default() {
        when(transactionRepository.findByClientIdSortedByTimestampDesc("1")).thenReturn(List.of());

        useCase.execute("1", null);

        verify(transactionRepository).findByClientIdSortedByTimestampDesc("1");
    }

    @Test
    void should_delegate_to_repository_unsorted_when_sort_is_asc() {
        when(transactionRepository.findByClientId("1")).thenReturn(List.of());

        useCase.execute("1", "asc");

        verify(transactionRepository).findByClientId("1");
    }
}
