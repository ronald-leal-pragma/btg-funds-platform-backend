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
                new Transaction("uuid-1", Transaction.TransactionType.APERTURA, "1", "FPV_BTG", 75_000L, Instant.now()),
                new Transaction("uuid-2", Transaction.TransactionType.CANCELACION, "1", "FPV_BTG", 75_000L, Instant.now())
        );
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(transactions);
    }

    @Test
    void should_return_empty_list_when_no_transactions() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> result = useCase.execute();

        assertThat(result).isEmpty();
    }

    @Test
    void should_delegate_to_repository() {
        when(transactionRepository.findAll()).thenReturn(List.of());

        useCase.execute();

        verify(transactionRepository).findAll();
    }
}
