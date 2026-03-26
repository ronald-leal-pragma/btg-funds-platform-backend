package com.btg.funds.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SaldoInsuficienteExceptionTest {

    @Test
    void should_create_exception_with_message() {
        SaldoInsuficienteException ex = new SaldoInsuficienteException("saldo insuficiente");
        assertThat(ex.getMessage()).isEqualTo("saldo insuficiente");
    }

    @Test
    void should_be_instance_of_fund_domain_exception() {
        assertThat(new SaldoInsuficienteException("test")).isInstanceOf(FundDomainException.class);
    }
}
