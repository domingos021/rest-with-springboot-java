package com.dinisjovete.restwithspringbootjava.exception;

import java.util.Date;

/*
 * ExceptionResponse
 *
 * Objeto responsável por representar a estrutura
 * padrão das respostas de erro da API.
 *
 * Responsabilidade:
 * Armazenar as informações da exceção que serão
 * enviadas ao cliente em uma resposta HTTP.
 *
 * Campos:
 *
 * timestamp -> data e hora em que o erro ocorreu.
 * message   -> mensagem informando o motivo do erro.
 * details   -> informações adicionais sobre a requisição.
 *
 * Essa classe não realiza o tratamento da exceção.
 * Ela apenas serve como modelo de resposta utilizado
 * pelo CustomEntityResponseHandler.
 */
public record ExceptionResponse(
        Date timestamp,
        String message,
        String details
) {}