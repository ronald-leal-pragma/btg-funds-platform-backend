package com.btg.funds.application.usecase;

import com.btg.funds.application.dto.FundResponse;
import com.btg.funds.application.dto.FundWithStatusResponse;
import com.btg.funds.application.mapper.FundMapper;
import com.btg.funds.domain.exception.FundDomainException;
import com.btg.funds.domain.model.Client;
import com.btg.funds.domain.model.Fund;
import com.btg.funds.domain.repository.ClientRepository;
import com.btg.funds.domain.repository.FundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFundsUseCaseTest {

    @Mock FundRepository fundRepository;
    @Mock ClientRepository clientRepository;
    @Mock FundMapper fundMapper;

    @InjectMocks GetFundsUseCase useCase;

    private Client client;
    private List<Fund> funds;

    @BeforeEach
    void setUp() {
        client = new Client("1", 500_000L, "email", "user@test.com", List.of("1", "3"), "user@test.com", "pass123");
        funds = List.of(
                new Fund("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV"),
                new Fund("2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, "FPV"),
                new Fund("3", "DEUDAPRIVADA", 50_000L, "FIC")
        );
        lenient().when(fundMapper.toResponse(any(Fund.class))).thenAnswer(inv -> {
            Fund f = inv.getArgument(0);
            return new FundResponse(f.id(), f.name(), f.minAmount(), f.category());
        });
    }

    @Test
    void should_return_funds_with_subscribed_status() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(fundRepository.findAll()).thenReturn(funds);

        List<FundWithStatusResponse> result = useCase.execute("1");

        assertThat(result).hasSize(3);
    }

    @Test
    void should_mark_subscribed_funds_correctly() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(fundRepository.findAll()).thenReturn(funds);

        List<FundWithStatusResponse> result = useCase.execute("1");

        assertThat(result.get(0).subscribed()).isTrue();
        assertThat(result.get(1).subscribed()).isFalse();
        assertThat(result.get(2).subscribed()).isTrue();
    }

    @Test
    void should_return_empty_list_when_no_funds() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(fundRepository.findAll()).thenReturn(List.of());

        List<FundWithStatusResponse> result = useCase.execute("1");

        assertThat(result).isEmpty();
    }

    @Test
    void should_throw_when_client_not_found() {
        when(clientRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("1"))
            .isInstanceOf(FundDomainException.class)
            .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    void should_include_fund_data_in_result() {
        when(clientRepository.findById("1")).thenReturn(Optional.of(client));
        when(fundRepository.findAll()).thenReturn(funds);

        List<FundWithStatusResponse> result = useCase.execute("1");

        assertThat(result.get(0).fund().name()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.get(0).fund().minAmount()).isEqualTo(75_000L);
    }
}
