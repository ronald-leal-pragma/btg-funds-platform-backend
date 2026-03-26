package com.btg.funds.application.usecase;

import com.btg.funds.application.port.out.NotificationPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscribeFundUseCaseTest {

    @Mock ClientRepository clientRepository;
    @Mock FundRepository fundRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock NotificationPort notificationPort;

    @InjectMocks SubscribeFundUseCase useCase;

    private Client clientWithNoFunds;
    private Client clientSubscribedToFund1;
    private Fund fund1;

    @BeforeEach
    void setUp() {
        fund1 = new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV");
        clientWithNoFunds = new Client("1", 500_000L, "email", "user@test.com", List.of());
        clientSubscribedToFund1 = new Client("1", 425_000L, "email", "user@test.com", List.of("1"));
    }

    @Test
    void should_subscribe_successfully_when_balance_is_sufficient() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientWithNoFunds));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transaction result = useCase.execute("1");

        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(Transaction.TransactionType.APERTURA);
        assertThat(result.fundId()).isEqualTo("1");
        assertThat(result.fundName()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.amount()).isEqualTo(75_000L);
        assertThat(result.id()).isNotNull();
    }

    @Test
    void should_deduct_balance_from_client_when_subscribing() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientWithNoFunds));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("1");

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        assertThat(captor.getValue().balance()).isEqualTo(425_000L);
        assertThat(captor.getValue().activeFundIds()).contains("1");
    }

    @Test
    void should_call_notification_port_after_subscribing() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientWithNoFunds));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("1");

        verify(notificationPort).notifySubscription(any(Client.class), eq(fund1));
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
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientWithNoFunds));
        when(fundRepository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("99"))
                .isInstanceOf(FundDomainException.class)
                .hasMessageContaining("Fondo no encontrado");
    }

    @Test
    void should_throw_when_already_subscribed_to_fund() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientSubscribedToFund1));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));

        assertThatThrownBy(() -> useCase.execute("1"))
                .isInstanceOf(FundDomainException.class)
                .hasMessageContaining("Ya está suscrito al fondo");
    }

    @Test
    void should_throw_when_balance_insufficient() {
        Client poorClient = new Client("1", 10_000L, "email", "user@test.com", List.of());
        when(clientRepository.findById("1")).thenReturn(Optional.of(poorClient));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));

        assertThatThrownBy(() -> useCase.execute("1"))
                .isInstanceOf(FundDomainException.class)
                .hasMessage("No tiene saldo disponible para vincularse al fondo FPV_BTG_PACTUAL_RECAUDADORA");
    }

    @Test
    void should_save_transaction_to_repository() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(clientWithNoFunds));
        when(fundRepository.findById("1")).thenReturn(Optional.of(fund1));
        when(clientRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute("1");

        verify(transactionRepository).save(any(Transaction.class));
    }
}
