package com.teste.contas.domain.exception;

public class ContaInvalidException extends RuntimeException {
    public ContaInvalidException(String message) {
        super(message);
    }
}
