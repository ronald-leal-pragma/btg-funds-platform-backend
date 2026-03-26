package com.btg.funds.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FundDomainExceptionTest {

    @Test
    void should_create_exception_with_message() {
        FundDomainException ex = new FundDomainException("mensaje de error");
        assertThat(ex.getMessage()).isEqualTo("mensaje de error");
    }

    @Test
    void should_be_instance_of_runtime_exception() {
        assertThat(new FundDomainException("test")).isInstanceOf(RuntimeException.class);
    }

    @Test
    void should_throw_and_be_catchable() {
        assertThatThrownBy(() -> { throw new FundDomainException("fondo no encontrado"); })
                .isInstanceOf(FundDomainException.class)
                .hasMessage("fondo no encontrado");
    }
}
