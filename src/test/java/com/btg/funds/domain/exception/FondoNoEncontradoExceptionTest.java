package com.btg.funds.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FondoNoEncontradoExceptionTest {

    @Test
    void should_create_exception_with_message() {
        FondoNoEncontradoException ex = new FondoNoEncontradoException("fondo no encontrado");
        assertThat(ex.getMessage()).isEqualTo("fondo no encontrado");
    }

    @Test
    void should_be_instance_of_fund_domain_exception() {
        assertThat(new FondoNoEncontradoException("test")).isInstanceOf(FundDomainException.class);
    }
}
