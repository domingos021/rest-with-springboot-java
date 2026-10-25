package com.dinisjovete.restwithspringbootjava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * Lançada quando a operação viola uma regra de negócio por conflito de estado.
 * Exemplos: e-mail ou CPF já cadastrados, pessoa com livros que não pode ser removida.
 * Resulta em HTTP 409 (Conflict).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message) {
        super(message);
    }
}
