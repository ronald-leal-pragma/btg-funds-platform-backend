package com.btg.funds.domain.exception;

/**
 * Excepción de negocio lanzada cuando no se encuentra un fondo por id
 */
public class FondoNoEncontradoException extends FundDomainException {

    public FondoNoEncontradoException(String message) {
        super(message);
    }
}
