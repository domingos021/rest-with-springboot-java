package com.dinisjovete.restwithspringbootjava.controllers.person.v1;

import com.dinisjovete.restwithspringbootjava.config.hateoas.v1.PersonHateoasAssembler;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.person.v1.PersonService;
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
 * CONTROLLER - PersonController (v1)
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java. Recebe a requisição, valida o formato da entrada,
 * delega ao Service e devolve a resposta com o status HTTP correto e os links
 * HATEOAS.
 *
 * O Controller NÃO contém regra de negócio: ela mora no PersonService.
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * PersonControllerDocs, que esta classe implementa.
 *
 *
 * POSIÇÃO NO FLUXO
 * ----------------
 *
 *      Cliente (Postman, navegador, front-end)
 *          |
 *          v
 *      PersonController          <- esta classe
 *          |
 *          +------ @Valid ------> valida o DTO de entrada (400 se inválido)
 *          |
 *          v
 *      PersonService             -> regras de negócio
 *          |
 *          v
 *      Repository -> Banco de Dados
 *          |
 *          v
 *      Service devolve PersonDTO
 *          |
 *          v
 *      PersonHateoasAssembler    -> adiciona os links ao DTO
 *          |
 *          v
 *      Resposta HTTP para o cliente
 *
 *
 * ENDPOINTS
 * ---------
 *
 *      Verbo    Rota                  Sucesso          Erros previstos
 *      ------   -------------------   --------------   ----------------------------
 *      GET      /person/v1/{id}       200 OK           404 (não existe)
 *      GET      /person/v1            200 OK           -
 *      POST     /person/v1            201 Created      400 (entrada inválida)
 *      PUT      /person/v1/{id}       200 OK           400 (entrada inválida), 404
 *      DELETE   /person/v1/{id}       204 No Content   404 (não existe)
 *
 * Os erros de negócio (como 404) são lançados pelo Service como exceção e
 * convertidos em status HTTP pelo ControllerAdvice global.
 *
 *
 * FORMATOS ACEITOS E PRODUZIDOS
 * -----------------------------
 * JSON, XML e YAML. O formato da resposta é escolhido pelo cabeçalho Accept da
 * requisição e o formato do corpo enviado é indicado pelo cabeçalho Content-Type.
 *
 * Exemplos de cabeçalho:
 *
 *      Accept: application/json
 *      Accept: application/xml
 *      Accept: <valor definido em CustomMediaType.APPLICATION_YAML_VALUE>
 * ============================================================================
 */
@RestController
/*
 * @RequestMapping("/person/v1")
 *
 * Define o caminho base deste Controller. Todos os métodos herdam esse prefixo:
 *
 *      http://localhost:8080/person/v1
 *
 * O "v1" faz parte do versionamento da API: a versão 2 vive em outro Controller
 * (/person/v2) e os dois podem coexistir sem se afetar.
 */
@RequestMapping("/person/v1")
public class PersonController implements PersonControllerDocs { // documentação Swagger na interface


    // =========================================================================
    // INJEÇÃO DE DEPENDÊNCIA
    // =========================================================================
    // 'final' impede que a referência seja trocada depois da construção do
    // objeto: a classe fica mais segura e previsível.
    // =========================================================================
    private final PersonService service;
    private final PersonHateoasAssembler assembler; // adiciona os links HATEOAS aos DTOs

    // =========================================================================
    // CONSTRUTOR
    // =========================================================================
    // O Spring usa este construtor para injetar as dependências.
    //
    // Desde o Spring Boot 3.x, quando existe um único construtor, a anotação
    // @Autowired é opcional. Injeção por construtor também facilita os testes:
    // basta passar mocks na criação do objeto.
    // =========================================================================
    public PersonController(PersonService personService, PersonHateoasAssembler assembler) {
        this.service = personService;
        this.assembler = assembler;
    }


    /*
     * ============================================================================
     * GET /person/v1/{id}  ->  BUSCAR UMA PESSOA PELO ID
     * ============================================================================
     *
     * Devolve a pessoa correspondente ao ID informado na URL.
     *
     *      GET http://localhost:8080/person/v1/4
     *
     *      /person/v1  -> recurso (Resource)
     *      /4          -> Path Parameter: o valor 4 é recebido no parâmetro "id"
     *
     * Respostas:
     *
     *      200 OK        pessoa encontrada (corpo: PersonDTO com links)
     *      404 Not Found ID inexistente (ResourceNotFoundException no Service)
     *      400 Bad Request ID que não é número (ex.: /person/v1/abc)
     *
     * O "produces" lista os formatos que este endpoint sabe devolver. Se o
     * cliente pedir um formato fora da lista, o Spring responde 406.
     * ============================================================================
     */
    /*
     * @Override: este método implementa o método findById(Long)
     * declarado na interface PersonControllerDocs.
     *
     * A assinatura do método (nome + tipos dos parâmetros) precisa ser
     * exatamente igual à declaração da interface; caso contrário,
     * o compilador acusará erro.
     *
     * O @Override não injeta a documentação da interface.
     * Ele apenas informa ao Java que este método está implementando
     * um contrato definido anteriormente.
     *
     * Representação da relação:
     *
     *
     *        PersonControllerDocs => (Interface)
     *                  |
     *                  | contrato:
     *                  | findById(Long)
     *                  ↓
     *        PersonController  => (implementação)
     *
     *
     *         findById(Long)  -> inplementa aqui o contrato conrespondente
     *
     * O SpringDoc/OpenAPI utiliza essa relação entre interface e implementação
     * para encontrar as anotações de documentação (@Operation, @ApiResponse, etc.)
     * declaradas na interface e associá-las a este endpoint do Controller.
     */
    @Override
    @GetMapping(
            // Caminho relativo ao @RequestMapping da classe.
            // "{id}" representa um Path Parameter.
            value = "/{id}",

            // Formatos que o endpoint produz (negociados pelo cabeçalho Accept).
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public PersonDTO findById(@PathVariable("id") Long id) {

        // Fluxo: Controller -> Service -> Repository -> Banco -> Entity
        //        -> Service converte em PersonDTO -> Controller devolve.
        PersonDTO person = service.findById(id);

        // O DTO volta enriquecido com os links HATEOAS (ex.: link "self").
        return assembler.addLinks(person);

    }


    /*
     * ============================================================================
     * GET /person/v1  ->  LISTAR TODAS AS PESSOAS (com HATEOAS)
     * ============================================================================
     *
     * Devolve todas as pessoas cadastradas.
     *
     * POR QUE O RETORNO É UM CollectionModel E NÃO UMA List?
     *
     * Uma List comum não tem onde guardar links. O CollectionModel embrulha a
     * lista e permite anexar links à COLEÇÃO inteira (como o "self" da
     * listagem), além dos links individuais de cada item. Assim o cliente pode
     * navegar pela API seguindo os links, sem montar URLs na mão.
     *
     * Respostas:
     *
     *      200 OK  sempre. Se não houver ninguém, a coleção vem vazia (isso não é erro).
     * ============================================================================
     */
    @Override
    @GetMapping(
            value = "",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public CollectionModel<PersonDTO> findAll() {

        // 1º passo: busca a lista de DTOs no Service.
        // O Service cuida da persistência e da conversão Entity -> DTO.
        List<PersonDTO> persons = service.findAll();

        // 2º passo: adiciona os links HATEOAS de cada item (ex.: "self" -> /person/v1/{id}).
        // O 'assembler' concentra essa montagem de links num único lugar,
        // evitando código repetido entre os endpoints.
        persons.forEach(assembler::addLinks);

        // 3º passo: empacota a lista em um CollectionModel e anexa o link da
        // própria coleção (self -> /person/v1).
        return CollectionModel.of(persons,
                linkTo(
                        methodOn(PersonController.class)
                                .findAll()
                )
                        .withSelfRel() // link "self" da coleção
        );
    }


    /*
     * ============================================================================
     * POST /person/v1  ->  CRIAR UMA NOVA PESSOA
     * ============================================================================
     *
     * Recebe um PersonInsertDTO no corpo da requisição (@RequestBody), valida
     * com @Valid e devolve o PersonDTO criado com o status 201 (Created).
     *
     * Respostas:
     *
     *      201 Created     pessoa criada (corpo: PersonDTO com links, sem senha)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * @Valid executa as regras de validação declaradas no PersonInsertDTO
     * (@NotBlank, @Email, @Size etc.) ANTES de o método rodar. Se a validação
     * falhar, o Service nem é chamado.
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON (POST http://localhost:8080/person/v1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Elon",
     *        "lastName": "Musk",
     *        "cpf": "99988877766",
     *        "email": "elon.musk@email.com",
     *        "password": "123456",
     *        "address": "Texas - EUA",
     *        "gender": "Male",
     *        "role": "CLIENT"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML (POST http://localhost:8080/person/v1)
     * ----------------------------------------------------------------------------
     *
     *      <PersonInsertDTO>
     *        <firstName>Elon</firstName>
     *        <lastName>Musk</lastName>
     *        <cpf>99988877766</cpf>
     *        <email>elon.musk@email.com</email>
     *        <password>123456</password>
     *        <address>Texas - EUA</address>
     *        <gender>Male</gender>
     *        <role>CLIENT</role>
     *      </PersonInsertDTO>
     *
     * O DTO de entrada não possui "id": quem gera o identificador é o banco.
     * ============================================================================
     */
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
            }
    )
    public ResponseEntity<PersonDTO> create(@Valid @RequestBody PersonInsertDTO personDTO) {
        PersonDTO createdPerson = service.create(personDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.addLinks(createdPerson)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /person/v1/{id}  ->  ATUALIZAR UMA PESSOA
     * ============================================================================
     *
     *      PUT http://localhost:8080/person/v1/1
     *
     * O ID vem da URL (@PathVariable) e os novos dados vêm no corpo da requisição
     * (@RequestBody), convertidos pelo Jackson em um PersonUpdateDTO.
     *
     * O PersonUpdateDTO contém SOMENTE os campos que a API permite alterar.
     * Estes campos não fazem parte da atualização, pois têm regras próprias:
     *
     *      id, cpf, email, password, role, birthDate
     *
     * Isso é uma lista branca de campos editáveis: o que não está no DTO não
     * pode ser alterado por este endpoint, mesmo que o cliente envie no JSON.
     *
     * Respostas:
     *
     *      200 OK          pessoa atualizada (corpo: PersonDTO com links)
     *      400 Bad Request corpo malformado, inválido ou ID que não é número
     *      404 Not Found   ID inexistente
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON (PUT http://localhost:8080/person/v1/1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "firstName": "Ayrton",
     *        "lastName": "Senna da Silva",
     *        "address": "São Paulo - SP - Brasil",
     *        "gender": "Male"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML (PUT http://localhost:8080/person/v1/1)
     * ----------------------------------------------------------------------------
     *
     *      <PersonUpdateDTO>
     *        <firstName>Ayrton</firstName>
     *        <lastName>Senna da Silva</lastName>
     *        <address>São Paulo - SP - Brasil</address>
     *        <gender>Male</gender>
     *      </PersonUpdateDTO>
     * ============================================================================
     */
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
    public PersonDTO update(

            // ID da pessoa a atualizar, recebido pela URL (PUT /person/v1/1 -> id = 1).
            // O Service usa esse ID para localizar a entidade existente.
            @PathVariable("id") Long id,

            // Corpo da requisição convertido em PersonUpdateDTO pelo Jackson.
            // @Valid aplica as regras de validação declaradas no DTO antes de o
            // método rodar. Depois disso, a lógica de atualização fica no Service.
            @Valid @RequestBody PersonUpdateDTO personUpdate

    ) {

        // O Service busca a entidade, aplica os campos permitidos, salva e devolve
        // a pessoa atualizada. O assembler adiciona os links HATEOAS à resposta.
        return assembler.addLinks(
                service.update(id, personUpdate)
        );
    }


    /*
     * ============================================================================
     * DELETE /person/v1/{id}  ->  REMOVER UMA PESSOA
     * ============================================================================
     *
     *      DELETE http://localhost:8080/person/v1/{id}
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