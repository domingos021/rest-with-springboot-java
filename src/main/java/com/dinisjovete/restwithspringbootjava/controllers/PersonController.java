package com.dinisjovete.restwithspringbootjava.controllers;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.services.PersonService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@RestController
/*
 * ============================================================================
 * @RequestMapping
 * ============================================================================
 * Define o caminho base (Base URL) deste Controller.
 *
 * Todas as requisições para este Controller começarão com:
 *
 *      http://localhost:8080/person
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/person")
public class PersonController {

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final PersonService service;

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    //
    // A partir do Spring Boot 3.x, quando existe apenas um construtor,
    // a anotação @Autowired não é mais necessária.
    // =========================================================================
    public PersonController(PersonService personService) {
        this.service = personService;
    }

    /*
     * =========================================================================
     * ENDPOINT HTTP
     * =========================================================================
     * Retorna uma pessoa a partir do seu ID.
     *
     * Método HTTP:
     *      GET
     *
     * Endpoint:
     *      http://localhost:8080/person/{id}
     *
     * Exemplo:
     *      http://localhost:8080/person/1
     *
     * Onde:
     *      /person -> recurso (Resource)
     *      /1      -> Path Parameter (ID da pessoa)
     *
     * O valor informado na URL será recebido pelo parâmetro "id".
     * =========================================================================
     */
    @RequestMapping(

            // Caminho relativo ao @RequestMapping("/person") da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Define que este endpoint responderá apenas a requisições HTTP GET.
            // GET é utilizado para consultar ou recuperar informações.
            method = RequestMethod.GET,

            // Define que a resposta será enviada no formato JSON
            // (JavaScript Object Notation), o padrão utilizado em APIs REST.
            produces = MediaType.APPLICATION_JSON_VALUE

    )
    public Person findById(@PathVariable("id") String id) {
        return service.findById(id);
    }
}


