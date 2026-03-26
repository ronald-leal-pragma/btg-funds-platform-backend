package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Transaction;
import com.btg.funds.infrastructure.persistence.document.TransactionDocument;
import com.btg.funds.infrastructure.persistence.mapper.TransactionDocumentMapperImpl;
import com.btg.funds.infrastructure.persistence.repository.MongoTransactionRepository;
import com.btg.funds.infrastructure.persistence.repository.SpringTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class MongoTransactionRepositoryTest {

    @Mock
    SpringTransactionRepository springTransactionRepository;

    MongoTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MongoTransactionRepository(springTransactionRepository, new TransactionDocumentMapperImpl());
    }

    @Test
    void should_save_transaction_and_return_domain_object() {
        Instant now = Instant.now();
        Transaction transaction = new Transaction("uuid-1", Transaction.TransactionType.APERTURA,
                "1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, now);
        TransactionDocument savedDoc = buildDoc("uuid-1", "APERTURA", "1",
                "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, now);

        when(springTransactionRepository.save(any())).thenReturn(savedDoc);

        Transaction result = repository.save(transaction);

        assertThat(result.id()).isEqualTo("uuid-1");
        assertThat(result.type()).isEqualTo(Transaction.TransactionType.APERTURA);
        assertThat(result.fundId()).isEqualTo("1");
        assertThat(result.amount()).isEqualTo(75_000L);
    }

    @Test
    void should_map_transaction_fields_to_document_on_save() {
        Instant now = Instant.now();
        Transaction transaction = new Transaction("uuid-2", Transaction.TransactionType.CANCELACION,
                "2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, now);
        TransactionDocument savedDoc = buildDoc("uuid-2", "CANCELACION", "2",
                "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, now);

        when(springTransactionRepository.save(any())).thenReturn(savedDoc);

        repository.save(transaction);

        ArgumentCaptor<TransactionDocument> captor = ArgumentCaptor.forClass(TransactionDocument.class);
        verify(springTransactionRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo("CANCELACION");
        assertThat(captor.getValue().getFundId()).isEqualTo("2");
        assertThat(captor.getValue().getAmount()).isEqualTo(125_000L);
    }

    @Test
    void should_return_all_transactions_mapped_to_domain() {
        Instant now = Instant.now();
        when(springTransactionRepository.findAll()).thenReturn(List.of(
                buildDoc("uuid-1", "APERTURA", "1", "FPV_BTG", 75_000L, now),
                buildDoc("uuid-2", "CANCELACION", "1", "FPV_BTG", 75_000L, now)
        ));

        List<Transaction> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(Transaction.TransactionType.APERTURA);
        assertThat(result.get(1).type()).isEqualTo(Transaction.TransactionType.CANCELACION);
    }

    @Test
    void should_return_empty_list_when_no_transactions() {
        when(springTransactionRepository.findAll()).thenReturn(List.of());

        List<Transaction> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    private TransactionDocument buildDoc(String id, String type, String fundId, String fundName, long amount, Instant timestamp) {
        return TransactionDocument.builder()
                .id(id)
                .type(type)
                .fundId(fundId)
                .fundName(fundName)
                .amount(amount)
                .timestamp(timestamp)
                .build();
    }
}
