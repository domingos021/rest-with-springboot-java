package com.dinisjovete.restwithspringbootjava.controllers.person.v1;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
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
- ============================================================================

- @RequestMapping

- ============================================================================

- Define o caminho base (Base URL) deste Controller.

 *
- Todas as requisições para este Controller começarão com:

 *
-      http://localhost:8080/person/v1

 *
- Assim, todos os métodos desta classe herdam esse caminho base.

- ============================================================================

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
- =========================================================================

- ENDPOINT HTTP

- =========================================================================

- Retorna uma pessoa a partir do seu ID, repassando o DTO retornado pelo service.

     *
- Método HTTP:

-      GET

     *
- Endpoint:

-      http://localhost:8080/person/v1/{id}

     *
- Exemplos:

-      http://localhost:8080/person/v1/4?mediaType=json

-      http://localhost:8080/person/v1/4?mediaType=xml

     *
- Onde:

-      /person/v1 -> recurso (Resource)

-      /4         -> Path Parameter (ID da pessoa)

     *
- O valor informado na URL será recebido pelo parâmetro "id".

- =========================================================================

     */
    @GetMapping(
            // Caminho relativo ao @RequestMapping da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Define que a resposta será enviada no formato JSON/XML/YAML
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTO findById(@PathVariable("id") Long id) {
        // solicita ao Service o PersonDTO correspondente ao ID informado.
        /*
- Fluxo da requisição:

         *
- Controller

-      ↓

- Aciona o método findById() do Service.

         *
- Service

-      ↓

- Aciona o método findById() do Repository.

         *
- Repository

-      ↓

- Consulta o banco de dados utilizando o ID informado.

         *
- Banco de Dados

-      ↓

- Retorna a Entity encontrada.

         *
- Service

-      ↓

- Converte a Entity em PersonDTO.

         *
- Controller

-      ↓

- Retorna o PersonDTO como resposta da API.

         */
        return service.findById(id);

    }

    // GET http://localhost:8080/person/v1
    @GetMapping(
            value = "",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public List<PersonDTO> findAll() {
        // Retorna a lista de PersonDTO já mapeada pelo service
        return service.findAll();
    }


    /*
- ============================================================================

- CREATE - CRIAR UMA NOVA PESSOA

- ============================================================================

- Utiliza o PersonInsertDTO no corpo da requisição (@RequestBody) validado

- pelas regras da anotação @Valid. Retorna o PersonDTO de saída fornecido pelo service

- com o status HTTP 201 (Created).

     *
- ----------------------------------------------------------------------------

- EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/person/v1):

- ----------------------------------------------------------------------------

- {

-   "firstName": "Elon",

-   "lastName": "Musk",

-   "cpf": "99988877766",

-   "email": "elon.musk@email.com",

-   "password": "123456",

-   "address": "Texas - EUA",

-   "gender": "Male",

-   "role": "CLIENT"

- }

- ----------------------------------------------------------------------------
- EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/person/v1):
- ----------------------------------------------------------------------------

- <PersonVO>
-   <id>null</id>
-   <firstName>Elon</firstName>
-   <lastName>Musk</lastName>
-   <cpf>99988877766</cpf>
-   <email>elon.musk@email.com</email>
-   <password>123456</password>
-   <address>Texas - EUA</address>
-   <gender>Male</gender>
-   <role>CLIENT</role>
- </PersonVO>

- ============================================================================

     */
    @PostMapping(
            value = "",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonInsertDTO personDTO) {
        PersonDTO createdPerson = service.create(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPerson);
    }


    /*
- ============================================================================

- UPDATE - ALTERAR UMA PESSOA

- ============================================================================

     *
- Método HTTP:

-      PUT

     *
- Endpoint:

-      http://localhost:8080/person/v1/{id}

     *
- O ID da pessoa é recebido através da URL utilizando @PathVariable.

     *
- Os novos dados são enviados no corpo da requisição (Request Body) em formato

- JSON e convertidos pelo Jackson para o objeto PersonUpdateDTO.

     *
- O PersonUpdateDTO contém somente os campos permitidos para atualização.

     *
- Campos como:

     *
- id

- cpf

- email

- password

- role

- birthDate

     *
- Não fazem parte desta operação, pois possuem regras específicas de alteração.

     *
     *
- ----------------------------------------------------------------------------

- EXEMPLO DE JSON PARA O POSTMAN

- ----------------------------------------------------------------------------

     *
- PUT:

     *
- http://localhost:8080/person/v1/1

     *
     *
- Body (JSON):

     *
- {

-   "firstName": "Ayrton",

-   "lastName": "Senna da Silva",

-   "address": "São Paulo - SP - Brasil",

-   "gender": "Male"

- }

     *
- ----------------------------------------------------------------------------
- EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/person/v1/1):
- ----------------------------------------------------------------------------

- <PersonVO>
-   <firstName>Ayrton</firstName>
-   <lastName>Senna da Silva</lastName>
-   <address>São Paulo - SP - Brasil</address>
-   <gender>Male</gender>
- </PersonVO>

- ============================================================================

     */
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTO update(

            /*
- =====================================================================

- PATH VARIABLE

- =====================================================================

             *
- Recebe o ID da pessoa diretamente pela URL.

             *
- Exemplo:

             *
- PUT /person/v1/1

             *
- O valor 1 será recebido neste parâmetro.

             *
- Esse ID será utilizado pelo Service para localizar a entidade

- existente no banco de dados.

             *
             */
            @PathVariable("id") Long id,


            /*
- =====================================================================

- REQUEST BODY

- =====================================================================

             *
- Recebe o corpo JSON enviado pelo cliente.

             *
- O Jackson converte automaticamente o JSON para um objeto

- PersonUpdateDTO.

             *
- A anotação @Valid executa as validações definidas no DTO:

             *
- @NotBlank

- @Size

- outras regras de validação

             *
- Após a validação, o DTO é enviado para o Service, onde fica a lógica

- de atualização da pessoa.

             *
             */
            @Valid @RequestBody PersonUpdateDTO personUpdate

    ) {

        /*
- Envia para o Service:

         *
- ID da pessoa que será atualizada.

- DTO contendo os novos valores permitidos.

         *
- O Service executa a regra de negócio:

         *
- Busca a entidade.

- Atualiza os campos permitidos.

- Salva no banco.

- Retorna a pessoa atualizada.

         *
         */
        return service.update(id, personUpdate);
    }


    /*
- ============================================================================

- DELETE - REMOVER UMA PESSOA

- ============================================================================

     *
- Método HTTP:

-      DELETE

     *
- Endpoint:

-      http://localhost:8080/person/v1/{id}

     *
- O ID da pessoa que será removida é enviado pela URL.

     *
- ============================================================================

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