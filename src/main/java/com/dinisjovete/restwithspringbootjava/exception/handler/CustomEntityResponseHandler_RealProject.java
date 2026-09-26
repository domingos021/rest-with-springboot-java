package com.dinisjovete.restwithspringbootjava.exception.handler;

/*
 * CustomEntityResponseHandler_RealProject
 *
 * Classe responsável pelo tratamento global
 * das exceções ocorridas durante o processamento
 * das requisições dos Controllers.
 *
 * Trabalha em conjunto com @ControllerAdvice e
 * @ExceptionHandler para capturar exceções lançadas
 * pelos Controllers e transformar em respostas HTTP
 * padronizadas para o cliente.
 */
 /*
 Cliente faz uma requisição HTTP
            |
            v
       Controller
            |
            | ocorre uma exceção
            v
   CustomEntityResponseHandler_RealProject
            |
            | @ExceptionHandler identifica o tipo
            v
   Monta resposta HTTP adequada
            |
            v
       Cliente recebe o erro
  */
/*
                  EXCEÇÃO ACONTECE
                        |
                        v
        ResourceNotFoundException / Exception
        (representa o tipo específico ou genérico do erro)
                        |
                        v
        CustomEntityResponseHandler_RealProject
        (centraliza e decide como tratar)
                        |
                        v
             ExceptionResponse
        (modela o formato da resposta)
                        |
                        v
                  Cliente recebe JSON
 */

import com.dinisjovete.restwithspringbootjava.exception.ExceptionResponse;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

    /*
    ResourceNotFoundException
              |
              | representa o erro
              | "o recurso solicitado não foi encontrado no banco de dados"
              v

    CustomEntityResponseHandler_RealProject
              |
              | @ExceptionHandler(ResourceNotFoundException.class)
              | captura esse tipo de erro
              v

    Cria a resposta HTTP
              |
              v

    Cliente recebe:
    HTTP 404 NOT_FOUND
     */

/*
 * @ControllerAdvice
 *
 * Define uma classe como responsável pelo tratamento global
 * de exceções dos Controllers da aplicação.
 *
 * Quando uma exceção é lançada durante uma requisição,
 * o Spring procura um método compatível anotado com
 * @ExceptionHandler dentro desta classe.
 *
 * Objetivo:
 * Centralizar o tratamento de erros, evitando repetição de código
 * em vários Controllers e mantendo um padrão nas respostas enviadas
 * ao cliente.
 *
 * Fluxo:
 *
 * Controller
 *     |
 *     | lança uma exceção
 *     v
 *
 * @ControllerAdvice
 *     |
 *     | procura o @ExceptionHandler correspondente
 *     v
 *
 * Retorna uma resposta HTTP personalizada ao cliente.
 */
@ControllerAdvice
@RestController
public class CustomEntityResponseHandler_RealProject extends ResponseEntityExceptionHandler {


    /*
     * Objetivo:
     * Captura erros específicos quando um recurso buscado
     * não é encontrado (ex: ID inexistente).
     *
     * Retorna:
     * HTTP 404 - NOT_FOUND
     */
    /*
     * ResourceNotFoundException.class
     *
     * Exceção personalizada criada pela aplicação
     * para representar falhas de busca por registros.
     *
     * O @ExceptionHandler responsável captura essa exceção
     * e retorna uma resposta HTTP adequada (404 NOT_FOUND).
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


    /*
     * Objetivo:
     * Captura qualquer exceção genérica que não possui
     * um tratamento específico.
     * Retorna:
     * HTTP 500 - INTERNAL_SERVER_ERROR
     */
    /*
     * Exception.class
     *
     * Classe base genérica do Java para exceções.
     *
     * Neste caso é utilizada para capturar qualquer exceção
     * que não tenha um tratamento específico definido anteriormente.
     *
     * Exemplo:
     * Caso uma exceção inesperada aconteça no sistema,
     * este método será responsável por tratá-la.
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(
            Exception exception,
            WebRequest request) {

        ExceptionResponse response = new ExceptionResponse(
                new Date(),
                exception.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

    /*
    Cliente faz uma requisição
              |
              v
         Controller -> ponto 1
              |
              |
              | ocorre um erro -> ponto 1.1
              v
    Exceção é lançada -> ponto 1.2
              |
              v
    Spring procura um @ExceptionHandler compatível -> ponto 2
              |
              v
    @ControllerAdvice encontra o método responsável -> ponto 2.1
              |
              v
    Método trata a exceção -> ponto 2.2
              |
              v
    Cria uma resposta HTTP personalizada
              |
              v
    Resposta retorna ao cliente
    (com status HTTP e informações do erro)
     */

/*
  @ControllerAdvice
        |
        |
        +--> ResourceNotFoundException
        |          |
        |          +--> recurso não encontrado
        |              HTTP 404
        |
        +--> Exception
                   |
                   +--> qualquer erro inesperado
                       HTTP 500
 */