package com.dinisjovete.restwithspringbootjava.controllers.person.v1;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.person.v1.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 *      http://localhost:8080/person/v1
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/person/v1")
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
     *      http://localhost:8080/person/v1/{id}
     *
     * Exemplo:
     *      http://localhost:8080/person/v1/1
     *
     * Onde:
     *      /person/v1 -> recurso (Resource)
     *      /1         -> Path Parameter (ID da pessoa)
     *
     * O valor informado na URL será recebido pelo parâmetro "id".
     * =========================================================================
     */
    @GetMapping(
            // Caminho relativo ao @RequestMapping da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

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

    // GET http://localhost:8080/person/v1
    @GetMapping(
            value = "",
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
     * pelas regras da anotação @Valid. Retorna o PersonDTO de saída fornecido pelo service
     * com o status HTTP 201 (Created).
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/person/v1):
     * ----------------------------------------------------------------------------
     * {
     *   "firstName": "Elon",
     *   "lastName": "Musk",
     *   "cpf": "99988877766",
     *   "email": "elon.musk@email.com",
     *   "password": "123456",
     *   "address": "Texas - EUA",
     *   "gender": "Male",
     *   "role": "CLIENT"
     * }
     * ============================================================================
     */
    @PostMapping(
            value = "",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonInsertDTO personDTO) {
        PersonDTO createdPerson = service.create(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
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
     *      http://localhost:8080/person/v1/{id}
     *
     * O ID vem pela URL (Path Variable) e os novos dados de entrada vêm pelo
     * corpo da requisição utilizando o PersonUpdateDTO com @Valid.
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/person/v1/1):
     * ----------------------------------------------------------------------------
     * {
     *   "firstName": "Ayrton",
     *   "lastName": "Senna da Silva",
     *   "address": "São Paulo - SP - Brasil",
     *   "gender": "Male",
     *   "role": "ADMIN"
     * }
     * ============================================================================
     */
    @PutMapping(
            value = "/{id}",
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
     *      http://localhost:8080/person/v1/{id}
     *
     * O ID da pessoa que será removida é enviado pela URL.
     *
     * ============================================================================
     */
    @DeleteMapping(
            value = "/{id}"
    )
    //ResponseEntity<?> -> retorna o status code 204 que e o correto nesse caso
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // não retorna nenhum conteúdo no body de resposta, apenas 204
    }

}