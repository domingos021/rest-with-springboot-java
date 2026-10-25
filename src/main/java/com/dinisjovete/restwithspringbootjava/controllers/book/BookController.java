package com.dinisjovete.restwithspringbootjava.controllers.book;


import com.dinisjovete.restwithspringbootjava.config.hateoas.book.BookHateoasAssembler;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.book.BookService;
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
 * CONTROLLER - BookController
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java. Recebe a requisição, valida o formato da entrada,
 * delega ao Service e devolve a resposta com o status HTTP correto e os links
 * HATEOAS.
 *
 * O Controller NÃO contém regra de negócio: ela mora no BookService.
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * BookControllerDocs, que esta classe implementa (inclusive o @Tag).
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
 *      http://localhost:8080/book
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/book")
public class BookController implements BookControllerDocs { // documentação Swagger na interface

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final BookService service;
    private final BookHateoasAssembler assembler; // adiciona os links HATEOAS aos DTOs

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    //
    // A partir do Spring Boot 3.x, quando existe apenas um construtor,
    // a anotação @Autowired não é mais necessária.
    // =========================================================================
    public BookController(BookService bookService, BookHateoasAssembler bookHateoasAssembler) {
        this.service = bookService;
        this.assembler = bookHateoasAssembler;
    }

    /*
     * ============================================================================
     * GET /book/{id}  ->  BUSCAR UM LIVRO PELO ID
     * ============================================================================
     *
     * Retorna um livro a partir do seu ID, repassando o DTO retornado pelo Service.
     *
     *      GET http://localhost:8080/book/4
     *
     *      /book  -> recurso (Resource)
     *      /4     -> Path Parameter: o valor 4 é recebido no parâmetro "id"
     *
     * O formato da resposta é escolhido pelo cabeçalho Accept:
     *
     *      Accept: application/json
     *      Accept: application/xml
     *      Accept: application/yaml
     *
     * Respostas:
     *
     *      200 OK          livro encontrado (corpo: BookDTO com links)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número (ex.: /book/abc)
     * ============================================================================
     */
    // @Override: este método implementa findById(Long) de BookControllerDocs.
    // A assinatura (nome + tipos dos parâmetros) precisa bater; senão o compilador acusa erro.
    @Override
    @GetMapping(
            // Caminho relativo ao @RequestMapping("/book") da classe.
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
                     *      GET http://localhost:8080/book/4
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
    public BookDTO findById(@PathVariable("id") Long id) {
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
         * Converte a Entity em BookDTO.
         *
         * Controller
         *      ↓
         * Retorna o BookDTO como resposta da API.
         */
        BookDTO book = service.findById(id);

        // O DTO volta enriquecido com os links HATEOAS (ex.: link "self").
        return assembler.addLinks(book);
    }


    /*
     * ============================================================================
     * GET /book  ->  LISTAR TODOS OS LIVROS (com HATEOAS)
     * ============================================================================
     *
     * Retorna todos os registros de livros com suporte completo a HATEOAS
     * (Hypermedia as the Engine of Application State).
     *
     * POR QUE FAZEMOS ISSO?
     * Para transformar uma listagem comum em uma API HATEOAS, permitindo que o
     * cliente navegue pela API através de links dinâmicos e auto-descritivos,
     * tanto para os itens individuais quanto para a coleção como um todo.
     *
     * Respostas:
     *
     *      200 OK  sempre. Se não houver nenhum livro, a coleção vem vazia (isso não é erro).
     * ============================================================================
     */
    // @Override: implementa findAll() de BookControllerDocs.
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
    public CollectionModel<BookDTO> findAll() {

        // 1º Passo: Acessa o banco via Service para buscar os dados brutos (DTOs).
        // Motivo: o Service/Repository gerencia a persistência, retornando uma lista comum do Java.
        List<BookDTO> books = service.findAll();

        // 2º Passo: Adiciona os links HATEOAS individuais em cada objeto da lista.
        // Motivo: cada item da lista precisa expor seus próprios links (ex.: link "self"),
        // e o 'assembler' concentra essa montagem num único lugar, evitando código duplicado.
        books.forEach(assembler::addLinks);

        // 3º Passo: Empacota a lista em um CollectionModel e adiciona o link da própria coleção.
        // Motivo: o Spring HATEOAS exige um 'CollectionModel' para conseguir anexar links
        // referentes à coleção inteira (como o link 'self' da listagem).
        return CollectionModel.of(books,
                linkTo(
                        methodOn(BookController.class)
                                .findAll()
                )
                        .withSelfRel() // link "self" da coleção
        );
    }


    /*
     * ============================================================================
     * POST /book  ->  CRIAR UM NOVO LIVRO
     * ============================================================================
     *
     * Recebe um BookInsertDTO no corpo da requisição (@RequestBody), valida
     * com @Valid e devolve o BookDTO criado com o status 201 (Created).
     *
     * Respostas:
     *
     *      201 Created     livro criado (corpo: BookDTO com links)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/book)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "author": "Robert C. Martin",
     *        "launchDate": "01/08/2008",
     *        "price": 89.90,
     *        "title": "Clean Code"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/book)
     * ----------------------------------------------------------------------------
     *
     *      <BookInsertDTO>
     *        <author>Robert C. Martin</author>
     *        <launchDate>01/08/2008</launchDate>
     *        <price>89.90</price>
     *        <title>Clean Code</title>
     *      </BookInsertDTO>
     *
     * O DTO de entrada não possui "id": quem gera o identificador é o banco.
     * ============================================================================
     */
    // @Override: implementa create(BookInsertDTO) de BookControllerDocs.
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
    public ResponseEntity<BookDTO> create(@Valid @RequestBody BookInsertDTO bookInsert) {
        BookDTO createdBook = service.create(bookInsert);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.addLinks(createdBook)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /book/{id}  ->  ATUALIZAR UM LIVRO
     * ============================================================================
     *
     *      PUT http://localhost:8080/book/1
     *
     * O ID vem pela URL (@PathVariable) e os novos dados vêm pelo corpo da
     * requisição (@RequestBody), convertidos em um BookUpdateDTO e validados
     * com @Valid.
     *
     * Respostas:
     *
     *      200 OK          livro atualizado (corpo: BookDTO com links)
     *      400 Bad Request corpo malformado, inválido ou ID que não é número
     *      404 Not Found   ID inexistente
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/book/1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "author": "Robert C. Martin",
     *        "price": 79.90,
     *        "title": "Clean Code - Edição Revisada"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/book/1)
     * ----------------------------------------------------------------------------
     *
     *      <BookUpdateDTO>
     *        <author>Robert C. Martin</author>
     *        <price>79.90</price>
     *        <title>Clean Code - Edição Revisada</title>
     *      </BookUpdateDTO>
     * ============================================================================
     */
    // @Override: implementa update(Long, BookUpdateDTO) de BookControllerDocs.
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
    public BookDTO update(
            @PathVariable("id") Long id,
            @Valid @RequestBody BookUpdateDTO bookUpdate
    ) {
        return assembler.addLinks(
                service.update(id, bookUpdate)
        );
    }


    /*
     * ============================================================================
     * DELETE /book/{id}  ->  REMOVER UM LIVRO
     * ============================================================================
     *
     *      DELETE http://localhost:8080/book/{id}
     *
     * O ID do livro a remover vem pela URL.
     *
     * Respostas:
     *
     *      204 No Content  removido com sucesso (sem corpo na resposta)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número
     *
     * O status 204 é o correto para uma operação concluída que não tem nada a
     * devolver. É o ResponseEntity.noContent() que define esse status e garante
     * o corpo vazio; o tipo ResponseEntity<?> apenas indica que não há corpo.
     * ============================================================================
     */
    // @Override: implementa delete(Long) de BookControllerDocs.
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