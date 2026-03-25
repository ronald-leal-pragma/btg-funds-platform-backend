package com.btg.funds.infrastructure.persistence;

import com.btg.funds.domain.model.Fund;
import com.btg.funds.infrastructure.persistence.document.FundDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoFundRepositoryTest {

    @Mock SpringFundRepository springFundRepository;

    @InjectMocks MongoFundRepository repository;

    @Test
    void should_return_all_funds_mapped_to_domain() {
        when(springFundRepository.findAll()).thenReturn(List.of(
                buildDoc("1", "FPV_BTG_PACTUAL_RECAUDADORA", 75_000L, "FPV"),
                buildDoc("2", "FPV_BTG_PACTUAL_ECOPETROL", 125_000L, "FPV")
        ));

        List<Fund> result = repository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo("1");
        assertThat(result.get(0).name()).isEqualTo("FPV_BTG_PACTUAL_RECAUDADORA");
        assertThat(result.get(0).minAmount()).isEqualTo(75_000L);
        assertThat(result.get(0).category()).isEqualTo("FPV");
    }

    @Test
    void should_return_empty_list_when_no_funds() {
        when(springFundRepository.findAll()).thenReturn(List.of());

        List<Fund> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void should_find_fund_by_id() {
        when(springFundRepository.findById("3")).thenReturn(Optional.of(
                buildDoc("3", "DEUDAPRIVADA", 50_000L, "FIC")
        ));

        Optional<Fund> result = repository.findById("3");

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("3");
        assertThat(result.get().category()).isEqualTo("FIC");
    }

    @Test
    void should_return_empty_when_fund_not_found() {
        when(springFundRepository.findById("99")).thenReturn(Optional.empty());

        Optional<Fund> result = repository.findById("99");

        assertThat(result).isEmpty();
    }

    private FundDocument buildDoc(String id, String name, long minAmount, String category) {
        return FundDocument.builder()
                .id(id)
                .name(name)
                .minAmount(minAmount)
                .category(category)
                .build();
    }
}
