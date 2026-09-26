package com.dinisjovete.restwithspringbootjava.controllers;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.PersonService;
import jakarta.validation.Valid;
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
     * Retorna uma pessoa a partir do seu ID, repassando o DTO retornado pelo service.
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
            method = RequestMethod.GET,

            // Define que a resposta será enviada no formato JSON
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonDTO findById(@PathVariable("id") Long id) {
        // solicita ao Service o PersonDTO correspondente ao ID informado.
        /*
         * Fluxo da requisição:
         *
         * Controller
         *      ↓
         * Aciona o método findById() do Service.
         *
         * Service
         *      ↓
         * Aciona o método findById() do Repository.
         *
         * Repository
         *      ↓
         * Consulta o banco de dados utilizando o ID informado.
         *
         * Banco de Dados
         *      ↓
         * Retorna a Entity encontrada.
         *
         * Service
         *      ↓
         * Converte a Entity em PersonDTO.
         *
         * Controller
         *      ↓
         * Retorna o PersonDTO como resposta da API.
         */
        return service.findById(id);

    }

    // GET http://localhost:8080/person
    @RequestMapping(
            value = "",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<PersonDTO> findAll() {
        // Retorna a lista de PersonDTO já mapeada pelo service
        return service.findAll();
    }


    /*
     * ============================================================================
     * CREATE - CRIAR UMA NOVA PESSOA
     * ============================================================================
     * Utiliza o PersonInsertDTO no corpo da requisição (@RequestBody) validado
     * pelas regras da anotação @Valid. Retorna o PersonDTO de saída fornecido pelo service.
     * ============================================================================
     */
    @RequestMapping(
            value = "",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonDTO create(@Valid @RequestBody PersonInsertDTO personDTO) {
        return service.create(personDTO);
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
     * O ID vem pela URL (Path Variable) e os novos dados de entrada vêm pelo
     * corpo da requisição utilizando o PersonUpdateDTO com @Valid.
     *
     * ============================================================================
     */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PersonDTO update(
            @PathVariable("id") Long id,
            @Valid @RequestBody PersonUpdateDTO personUpdate
    ) {
        return service.update(id, personUpdate);
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
     * O ID da pessoa que será removida é enviado pela URL.
     *
     * ============================================================================
     */
    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}