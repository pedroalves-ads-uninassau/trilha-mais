package com.trilhamais.backend.exception;

/**
 * Excecao lancada quando ha tentativa de cadastrar um e-mail ja existente no sistema.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
