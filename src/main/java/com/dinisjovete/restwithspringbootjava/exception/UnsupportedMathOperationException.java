package com.dinisjovete.restwithspringbootjava.exception;
/*
 * UnsupportedMathOperationException
 *
 * Exceção personalizada da aplicação.
 *
 * Responsabilidade:
 * Representar erros específicos relacionados a operações
 * matemáticas inválidas durante o processamento das requisições.
 *
 * Essa classe apenas define um tipo de exceção.
 * O tratamento e a criação da resposta HTTP são realizados
 * pelo CustomEntityResponseHandler através do @ExceptionHandler.
 */

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedMathOperationException extends RuntimeException {

    public UnsupportedMathOperationException(String message) {
        super(message);
    }
}
