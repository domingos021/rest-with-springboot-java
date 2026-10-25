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

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/*
 * ============================================================================
 * CONTROLLER - PersonControllerV2 (v2)
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java. Recebe a requisição, valida o formato da entrada,
 * delega ao Service e devolve a resposta com o status HTTP correto e os links
 * HATEOAS.
 *
 * O Controller NÃO contém regra de negócio: ela mora no PersonServiceV2.
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * PersonControllerDocsV2, que esta classe implementa (inclusive o @Tag).
 * ============================================================================
 */
@RestController
/*
 * ============================================================================
 * @RequestMapping
 * ============================================================================
 * Define o caminho base (Base URL) deste Controller.
 *
 * Todas as requisições para este Controller começarão com:
 *
 *      http://localhost:8080/person/v2
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/person/v2")
public class PersonControllerV2 implements PersonControllerDocsV2 { // documentação Swagger na interface

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final PersonServiceV2 service;
    private final PersonHateoasAssemblerV2 assemblerV2; // adiciona os links HATEOAS aos DTOs

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
     * ============================================================================
     * GET /person/v2/{id}  ->  BUSCAR UMA PESSOA PELO ID
     * ============================================================================
     *
     * Retorna uma pessoa a partir do seu ID, repassando o DTO retornado pelo Service.
     *
     *      GET http://localhost:8080/person/v2/4
     *
     *      /person/v2  -> recurso (Resource)
     *      /4          -> Path Parameter: o valor 4 é recebido no parâmetro "id"
     *
     * O formato da resposta é escolhido pelo cabeçalho Accept:
     *
     *      Accept: application/json
     *      Accept: application/xml
     *      Accept: application/yaml
     *
     * Respostas:
     *
     *      200 OK          pessoa encontrada (corpo: PersonDTOV2 com links)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número (ex.: /person/v2/abc)
     * ============================================================================
     */
    // @Override: este método implementa findById(Long) de PersonControllerDocsV2.
    // A assinatura (nome + tipos dos parâmetros) precisa bater; senão o compilador acusa erro.
    @Override
    @GetMapping(
            // Caminho relativo ao @RequestMapping("/person/v2") da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Formatos que o endpoint produz (negociados pelo cabeçalho Accept).
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
         * Converte a Entity em PersonDTOV2.
         *
         * Controller
         *      ↓
         * Retorna o PersonDTOV2 como resposta da API.
         */
        PersonDTOV2 person = service.findById(id);

        // O DTO volta enriquecido com os links HATEOAS (ex.: link "self").
        return assemblerV2.addLinks(person);
    }


    /*
     * ============================================================================
     * GET /person/v2  ->  LISTAR TODAS AS PESSOAS (com HATEOAS)
     * ============================================================================
     *
     * Retorna todos os registros de pessoas com suporte completo a HATEOAS
     * (Hypermedia as the Engine of Application State).
     *
     * POR QUE FAZEMOS ISSO?
     * Para transformar uma listagem comum em uma API HATEOAS, permitindo que o
     * cliente navegue pela API através de links dinâmicos e auto-descritivos,
     * tanto para os itens individuais quanto para a coleção como um todo.
     *
     * Respostas:
     *
     *      200 OK  sempre. Se não houver ninguém, a coleção vem vazia (isso não é erro).
     * ============================================================================
     */
    // @Override: implementa findAll() de PersonControllerDocsV2.
    @Override
    @GetMapping(
            value = "",

            // Formatos que o endpoint produz (negociados pelo cabeçalho Accept).
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public CollectionModel<PersonDTOV2> findAll() {

        // 1º Passo: Acessa o banco via Service para buscar os dados brutos (DTOs).
        // Motivo: o Service/Repository gerencia a persistência, retornando uma lista comum do Java.
        List<PersonDTOV2> persons = service.findAll();

        // 2º Passo: Adiciona os links HATEOAS individuais em cada objeto da lista.
        // Motivo: cada item da lista precisa expor seus próprios links (ex.: link "self"),
        // e o 'assemblerV2' concentra essa montagem num único lugar, evitando código duplicado.
        persons.forEach(assemblerV2::addLinks);

        // 3º Passo: Empacota a lista em um CollectionModel e adiciona o link da própria coleção.
        // Motivo: o Spring HATEOAS exige um 'CollectionModel' para conseguir anexar links
        // referentes à coleção inteira (como o link 'self' da listagem).
        return CollectionModel.of(persons,
                linkTo(
                        methodOn(PersonControllerV2.class)
                                .findAll()
                )
                        .withSelfRel() // link "self" da coleção
        );
    }


    /*
     * ============================================================================
     * POST /person/v2  ->  CRIAR UMA NOVA PESSOA
     * ============================================================================
     *
     * Recebe um PersonInsertDTOV2 no corpo da requisição (@RequestBody), valida
     * com @Valid e devolve o PersonDTOV2 criado com o status 201 (Created).
     *
     * Respostas:
     *
     *      201 Created     pessoa criada (corpo: PersonDTOV2 com links)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/person/v2)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Elon",
     *        "lastName": "Musk",
     *        "birthDate": "28/06/1971",
     *        "cpf": "99988877766",
     *        "phone": "+1 512 99999-9999",
     *        "email": "elon.musk@email.com",
     *        "password": "123456",
     *        "address": "Texas - EUA",
     *        "gender": "Male",
     *        "role": "CLIENT"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/person/v2)
     * ----------------------------------------------------------------------------
     *
     *      <PersonInsertDTOV2>
     *        <firstName>Elon</firstName>
     *        <lastName>Musk</lastName>
     *        <birthDate>28/06/1971</birthDate>
     *        <cpf>99988877766</cpf>
     *        <phone>+1 512 99999-9999</phone>
     *        <email>elon.musk@email.com</email>
     *        <password>123456</password>
     *        <address>Texas - EUA</address>
     *        <gender>Male</gender>
     *        <role>CLIENT</role>
     *      </PersonInsertDTOV2>
     *
     * O DTO de entrada não possui "id": quem gera o identificador é o banco.
     * ============================================================================
     */
    // @Override: implementa create(PersonInsertDTOV2) de PersonControllerDocsV2.
    @Override
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assemblerV2.addLinks(createdPerson)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /person/v2/{id}  ->  ATUALIZAR UMA PESSOA
     * ============================================================================
     *
     *      PUT http://localhost:8080/person/v2/1
     *
     * O ID vem pela URL (@PathVariable) e os novos dados vêm pelo corpo da
     * requisição (@RequestBody), convertidos em um PersonUpdateDTOV2 e validados
     * com @Valid.
     *
     * Respostas:
     *
     *      200 OK          pessoa atualizada (corpo: PersonDTOV2 com links)
     *      400 Bad Request corpo malformado, inválido ou ID que não é número
     *      404 Not Found   ID inexistente
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/person/v2/1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Ayrton",
     *        "lastName": "Senna da Silva",
     *        "address": "São Paulo - SP - Brasil",
     *        "gender": "Male",
     *        "role": "ADMIN"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/person/v2/1)
     * ----------------------------------------------------------------------------
     *
     *      <PersonUpdateDTOV2>
     *        <firstName>Ayrton</firstName>
     *        <lastName>Senna da Silva</lastName>
     *        <address>São Paulo - SP - Brasil</address>
     *        <gender>Male</gender>
     *        <role>ADMIN</role>
     *      </PersonUpdateDTOV2>
     * ============================================================================
     */
    // @Override: implementa update(Long, PersonUpdateDTOV2) de PersonControllerDocsV2.
    @Override
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
     * ============================================================================
     * DELETE /person/v2/{id}  ->  REMOVER UMA PESSOA
     * ============================================================================
     *
     *      DELETE http://localhost:8080/person/v2/{id}
     *
     * O ID da pessoa a remover vem pela URL.
     *
     * Respostas:
     *
     *      204 No Content  removida com sucesso (sem corpo na resposta)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número
     *
     * O status 204 é o correto para uma operação concluída que não tem nada a
     * devolver. É o ResponseEntity.noContent() que define esse status e garante
     * o corpo vazio; o tipo ResponseEntity<?> apenas indica que não há corpo.
     * ============================================================================
     */
    // @Override: implementa delete(Long) de PersonControllerDocsV2.
    @Override
    @DeleteMapping(
            value = "/{id}"
    )
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204, sem conteúdo no corpo
    }

}

//Swagger UI: http://localhost:8080/swagger-ui/index.html
//YAML da documentação: http://localhost:8080/v3/api-docs.yaml