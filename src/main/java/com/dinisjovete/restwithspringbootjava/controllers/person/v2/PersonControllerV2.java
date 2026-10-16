package com.dinisjovete.restwithspringbootjava.controllers.person.v2;


import com.dinisjovete.restwithspringbootjava.config.hateoas.v2.PersonHateoasAssemblerV2;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.services.person.v2.PersonServiceV2;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
-      http://localhost:8080/person/v2

 *
- Assim, todos os métodos desta classe herdam esse caminho base.

- ============================================================================

 */
@RequestMapping("/person/v2")
public class PersonControllerV2 {

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final PersonServiceV2 service;
    private final PersonHateoasAssemblerV2 assemblerV2; // Configuration HATEOAS

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    //
    // A partir do Spring Boot 3.x, quando existe apenas um construtor,
    // a anotação @Autowired não é mais necessária.
    // =========================================================================
    public PersonControllerV2(PersonServiceV2 personService, PersonHateoasAssemblerV2 personHateoasAssembler) {
        this.service = personService;
        this.assemblerV2 = personHateoasAssembler;
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

-      http://localhost:8080/person/v2/{id}

     *
- Exemplos:

-      http://localhost:8080/person/v2/4?mediaType=json

-      http://localhost:8080/person/v2/4?mediaType=xml

     *
- Onde:

-      /person -> recurso (Resource)

-      /4      -> Path Parameter (ID da pessoa)

     *
- O valor informado na URL será recebido pelo parâmetro "id".

- =========================================================================

     */
    @GetMapping(
            // Caminho relativo ao @RequestMapping("/person/v2") da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Define que a resposta será enviada no formato JSON/XML/YAML
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    /*
                     * ============================================================================
                     * MEDIA TYPE YAML PERSONALIZADO
                     * ============================================================================
                     *
                     * CustomMediaType.APPLICATION_YAML_VALUE representa o valor:
                     *
                     *      application/yaml
                     *
                     * Este MediaType não existe como constante padrão no Spring, diferente de:
                     *
                     *      MediaType.APPLICATION_JSON_VALUE
                     *      MediaType.APPLICATION_XML_VALUE
                     *
                     * Por isso foi criada uma constante personalizada na classe CustomMediaType.
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * Este MediaType funciona em conjunto com o conversor personalizado:
                     *
                     *      YamlJackson2HttpMessageConverter
                     *
                     * Esse conversor utiliza o YAMLMapper do Jackson para transformar objetos Java
                     * em respostas no formato YAML.
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * O conversor permanece como um componente "adormecido" dentro da aplicação.
                     *
                     * Ele não é chamado diretamente por nenhum Controller, Service ou Repository.
                     *
                     * O Spring mantém esse conversor registrado e somente "acorda" ele no momento
                     * em que uma requisição HTTP solicitar uma resposta compatível com YAML.
                     *
                     * Exemplo:
                     *
                     *      GET http://localhost:8080/person/v2/4
                     *
                     *      Header:
                     *
                     *          Accept: application/yaml
                     *
                     *
                     * Nesse momento o fluxo acontece:
                     *
                     *      Cliente solicita YAML
                     *              |
                     *              v
                     *      Spring MVC analisa o Header Accept
                     *              |
                     *              v
                     *      Encontra o YamlJackson2HttpMessageConverter
                     *              |
                     *              v
                     *      YAMLMapper converte o objeto Java
                     *              |
                     *              v
                     *      Resposta YAML enviada ao cliente
                     *
                     * ----------------------------------------------------------------------------
                     *
                     * Enquanto o cliente não solicitar YAML, esse conversor continua "adormecido"
                     * e o Spring utiliza os conversores padrões para JSON ou XML.
                     *
                     * Exemplo:
                     *
                     *      Accept: application/json
                     *
                     *      -> MappingJackson2HttpMessageConverter
                     *
                     *
                     *      Accept: application/xml
                     *
                     *      -> Conversor XML
                     *
                     * ============================================================================
                     */
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTOV2 findById(@PathVariable("id") Long id) {
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


        PersonDTOV2 person = service.findById(id);
        return assemblerV2.addLinks(person); // Adiciona links HATEOAS ao PersonDTO antes de retornar

    }

    // GET http://localhost:8080/person/v2
    @GetMapping(
            value = "",
            /*
            *usa as configurações da class WebConfig para aceitar tanto JSON quanto XML como resposta, dependendo do parâmetro de consulta "mediaType" na URL.
             */
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )

    /**
     * Retorna todos os registros de pessoas com suporte completo a HATEOAS.
     *
     * POR QUE FAZEMOS ISSO?
     * - Para transformar uma listagem comum em uma API HATEOAS (Hypermedia as the Engine of Application State),
     *   permitindo que o cliente navegue pela API através de links dinâmicos e auto-descritivos,
     *   tanto para os itens individuais quanto para a coleção como um todo.
     */
    public CollectionModel<PersonDTOV2> findAll() {

        // 1º Passo: Acessa o banco via service para buscar os dados brutos (DTOs)
        // Motivo: O service/repository gerencia a persistência, retornando uma lista comum do Java.
        List<PersonDTOV2> persons = service.findAll();

        // 2º Passo: Adiciona os links HATEOAS individuais em cada objeto da lista
        // Motivo: Cada item da lista precisa expor seus próprios links (ex: link de detalhes/self),
        // e o 'assemblerV2' encapsula essa regra de negócio para evitar código duplicado.
        persons.forEach(assemblerV2::addLinks);

        // 3º Passo: Empacota a lista em um CollectionModel e adiciona o link global da rota
        // Motivo: O Spring HATEOAS exige um 'CollectionModel' para conseguir anexar links
        // referentes à coleção inteira (como o link 'self' da listagem).
        return CollectionModel.of(persons,
                linkTo(
                        methodOn(PersonControllerV2.class)
                                .findAll()
                )
                        .withSelfRel() //adiciona o link global da rota (selfRel)
        );
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

- EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/person/v2):

- ----------------------------------------------------------------------------

     {
      "firstName": "Elon",
      "lastName": "Musk",
      "birthDate": "28/06/1971",
      "cpf": "99988877766",
      "phone": "+1 512 99999-9999",
      "email": "elon.musk@email.com",
      "password": "123456",
      "address": "Texas - EUA",
      "gender": "Male",
      "role": "CLIENT"
    }

- ----------------------------------------------------------------------------
- EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/person/v2):
- ----------------------------------------------------------------------------

     <PersonVO>
         <id>null</id>
         <firstName>Elon</firstName>
         <lastName>Musk</lastName>
         <birthDate>28/06/1971</birthDate>
         <cpf>99988877766</cpf>
         <phone>+1 512 99999-9999</phone>
         <email>elon.musk@email.com</email>
         <password>123456</password>
         <address>Texas - EUA</address>
         <gender>Male</gender>
         <role>CLIENT</role>
     </PersonVO>
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
                    /*
                     * CustomMediaType.APPLICATION_YAML_VALUE é uma constante definida na classe CustomMediaType
                     * que representa o Media Type "application/yaml". Isso permite que o Spring
                     * reconheça este conversor como capaz de produzir respostas nesse formato.
                     */

            }
    )
    public ResponseEntity<PersonDTOV2> create(@Valid @RequestBody PersonInsertDTOV2 personDTO) {
        PersonDTOV2 createdPerson = service.create(personDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdPerson);
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

-      http://localhost:8080/person/v2/{id}

     *
- O ID vem pela URL (Path Variable) e os novos dados de entrada vêm pelo

- corpo da requisição utilizando o PersonUpdateDTO com @Valid.

     *
- ----------------------------------------------------------------------------

- EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/person/v2/1):

- ----------------------------------------------------------------------------

- {

-   "firstName": "Ayrton",

-   "lastName": "Senna da Silva",

-   "address": "São Paulo - SP - Brasil",

-   "gender": "Male",

-   "role": "ADMIN"

- }

- ----------------------------------------------------------------------------
- EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/person/v2/1):
- ----------------------------------------------------------------------------

- <PersonVO>
-   <firstName>Ayrton</firstName>
-   <lastName>Senna da Silva</lastName>
-   <address>São Paulo - SP - Brasil</address>
-   <gender>Male</gender>
-   <role>ADMIN</role>
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
    public PersonDTOV2 update(
            @PathVariable("id") Long id,
            @Valid @RequestBody PersonUpdateDTOV2 personUpdate
    ) {
        return assemblerV2.addLinks(
            service.update(id, personUpdate)
        );
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

-      http://localhost:8080/person/v2/{id}

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