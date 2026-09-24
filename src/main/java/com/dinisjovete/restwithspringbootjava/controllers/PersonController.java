package com.dinisjovete.restwithspringbootjava.controllers;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.services.PersonService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

   //GET http://localhost:8080/person
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
       public List<Person> findAll() {
        return service.findAll();
    }


    //http://localhost:8080/person/create
    @RequestMapping(
            value = "/create",

            // Define que este endpoint aceita apenas requisições HTTP POST.
            // POST é utilizado para enviar dados e criar novos recursos.
            method = RequestMethod.POST,

            // Define o formato dos dados recebidos no corpo da requisição.
            // Neste caso, a API espera um JSON.
            consumes = MediaType.APPLICATION_JSON_VALUE,

            // Define o formato da resposta enviada pela API.
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Person create(@RequestBody Person person) {

        return service.create(person);

    }



    /*
     * ============================================================================
     * UPDATE - ALTERAR UMA PESSOA
     * ============================================================================
     *
     * Método HTTP:
     *      PUT
     *
     * Endpoint:
     *      http://localhost:8080/person/{id}
     *
     * Exemplo:
     *
     *      PUT http://localhost:8080/person/6
     *
     * O ID vem pela URL (Path Variable)
     * e os novos dados vêm pelo corpo da requisição (Request Body).
     *
     * ============================================================================
     */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Person update(
            @PathVariable("id") String id,
            @RequestBody Person person
    ) {

        return service.update(id, person);
    }



    /*
     * ============================================================================
     * DELETE - REMOVER UMA PESSOA
     * ============================================================================
     *
     * Método HTTP:
     *      DELETE
     *
     * Endpoint:
     *      http://localhost:8080/person/{id}
     *
     * Exemplo:
     *
     *      DELETE http://localhost:8080/person/6
     *
     * O ID da pessoa que será removida é enviado pela URL.
     *
     * ============================================================================
     */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> delete(
            @PathVariable("id") String id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

}


