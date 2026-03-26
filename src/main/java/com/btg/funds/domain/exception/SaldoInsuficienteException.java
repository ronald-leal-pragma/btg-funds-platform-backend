package com.btg.funds.domain.exception;

/**
 * Excepción de negocio lanzada cuando el cliente no tiene saldo suficiente
 */
public class SaldoInsuficienteException extends FundDomainException {

    public SaldoInsuficienteException(String message) {
        super(message);
    }
}
