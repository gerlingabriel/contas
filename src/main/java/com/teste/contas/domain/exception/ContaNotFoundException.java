package com.teste.contas.domain.exception;

public class ContaNotFoundException extends RuntimeException {
    public ContaNotFoundException(String message) {
        super(message);
    }
}
