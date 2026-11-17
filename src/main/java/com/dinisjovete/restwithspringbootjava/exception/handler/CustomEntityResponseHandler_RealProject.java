package com.dinisjovete.restwithspringbootjava.exception.handler;

import com.dinisjovete.restwithspringbootjava.exception.ExceptionResponse;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Centraliza o tratamento de exceções lançadas durante as requisições
 * dos controllers e retorna respostas HTTP padronizadas.
 *
 * O Spring identifica o método adequado por meio de {@code @ExceptionHandler}.
 */
@RestControllerAdvice
public class CustomEntityResponseHandler_RealProject
        extends ResponseEntityExceptionHandler {

    /**
     * Trata exceções lançadas quando um recurso solicitado não é encontrado.
     *
     * @return resposta HTTP 404 (NOT_FOUND)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundExceptions(
            ResourceNotFoundException exception,
            WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                exception.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Trata erros de validação causados por argumentos inválidos, como falhas
     * nas validações de DTOs anotados com {@code @Valid}.
     *
     * As mensagens de validação são reunidas em uma única resposta HTTP 400
     * (BAD_REQUEST).
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            errors.append(errorMessage).append("; ");
        });

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                errors.toString().trim(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata pedidos de ordenação que usam um campo inexistente na entidade,
     * como em {@code ?sort=campoInexistente}.
     *
     * @return resposta HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public final ResponseEntity<ExceptionResponse> handleInvalidSortProperty(
            PropertyReferenceException exception,
            WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                "Campo de ordenação inválido: '" + exception.getPropertyName() + "'",
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata exceções sem um tratamento específico nesta classe.
     *
     * @return resposta HTTP 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(
            Exception exception,
            WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                exception.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}