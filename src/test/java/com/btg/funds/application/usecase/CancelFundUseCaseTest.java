package com.btg.funds.application.usecase;

import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.model.Transaction;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import com.btg.funds.domain.repository.TransactionRepository;
import com.btg.funds.domain.exception.FundDomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelFundUseCaseTest {

    @Mock ClientRepository clientRepository;
    @Mock FundRepository fundRepository;
    @Mock TransactionRepository transactionRepository;

    @InjectMocks CancelFundUseCase useCase;

    private Client clientSubscribed;
    private Client clientNotSubscribed;
    private Fund fund1;

    @BeforeEach
    void setUp() {
        fund1 = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");
        clientSubscribed = new Client("1", 425_000L, "email", "user@test.com", List.of("1"));
        clientNotSubscribed = new Client("1", 500_000L, "email", "user@test.com", List.of());
    }

    @Test
    void should_cancel_subscription_successfully() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientSubscribed));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = useCase.execute("1");

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(Transaction.TransactionType.CANCELACION);
        assertThat(result.fundId()).isEqualTo("1");
        assertThat(result.amount()).isEqualTo(75_000L);
    }

    @Test
    void should_refund_balance_to_client_when_cancelling() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientSubscribed));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("1");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().balance()).isEqualTo(500_000L);
        assertThat(captor.getValue().activeFundIds()).doesNotContain("1");
    }

    @Test
    void should_throw_when_client_not_found() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("1"))
                .isInstanceOf(FundDomainException.class)
                .hasMessage("Cliente no encontrado");
    }

    @Test
    void should_throw_when_fund_not_found() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientSubscribed));
        when(fundRepository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("99"))
                .isInstanceOf(FundDomainException.class)
                .hasMessageContaining("Fondo no encontrado");
    }

    @Test
    void should_throw_when_not_subscribed_to_fund() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientNotSubscribed));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));

        assertThatThrownBy(() -> useCase.execute("1"))
                .isInstanceOf(FundDomainException.class)
                .hasMessageContaining("No está suscrito al fondo");
    }

    @Test
    void should_save_cancelacion_transaction_to_repository() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientSubscribed));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("1");

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        assertThat(captor.getValue().type()).isEqualTo(Transaction.TransactionType.CANCELACION);
    }
}
