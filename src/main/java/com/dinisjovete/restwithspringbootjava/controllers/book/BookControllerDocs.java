package com.dinisjovete.restwithspringbootjava.controllers.book;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/*
 * ============================================================================
 * INTERFACE DE DOCUMENTAÇÃO - BookControllerDocs
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Concentrar TODA a documentação Swagger/OpenAPI do BookController.
 * O BookController implementa esta interface e fica só com o que é dele:
 * rotas, formatos, validação, chamada ao Service e links HATEOAS.
 *
 * POR QUE SEPARAR?
 * ----------------
 * As anotações do Swagger (@Operation, @ApiResponse...) ocupavam mais linhas
 * do que a lógica dos métodos. Movendo-as para cá, o Controller volta a mostrar
 * o que importa e a documentação fica num lugar único.
 *
 * O QUE FICA NO CONTROLLER E NÃO PODE VIR PARA CÁ?
 * ------------------------------------------------
 * Tudo que é do Spring MVC e da validação:
 *
 *      @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
 *      @PathVariable, @RequestBody, @Valid
 *
 * O @Valid em especial não pode ser declarado na interface: o Bean Validation
 * proíbe que o método que sobrescreve (o do Controller) altere as regras de
 * validação herdadas do método da interface.
 *
 * Os parâmetros aqui não têm anotação nenhuma: o Spring e o springdoc leem as
 * anotações do método do Controller e usam esta interface só para a documentação.
 *
 * SOBRE O @Tag
 * ------------
 * O @Tag abaixo, no nível da interface, já agrupa todos os endpoints em
 * "Book". Por isso os @Operation não repetem o atributo "tags".
 * ============================================================================
 */
@Tag(name = "Book", description = "Endpoints of book management") // swagger
public interface BookControllerDocs {

    /*
     * Declara o método que será implementado pelo Controller.
     *
     * O Swagger utiliza esta assinatura juntamente com as anotações
     * abaixo para gerar a documentação deste endpoint.
     */
    // GET /book/{id}
    @Operation(
            summary = "Find a book",
            description = "Here we are getting one book by its ID",
            responses = {
                    /*
                     * 200 - sucesso
                     *
                     * Documenta a resposta de sucesso da requisição.
                     *
                     * O bloco content informa ao Swagger que esta resposta pode ser
                     * retornada em JSON, XML ou YAML.
                     *
                     * Durante a execução, quem escolhe qual formato será enviado é o
                     * Spring (Content Negotiation), analisando o cabeçalho HTTP Accept
                     * enviado pelo cliente.
                     *
                     * O Swagger apenas exibe essas opções na documentação.
                     */
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the book",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            /*
                                             * Usa a classe BookDTO como modelo (Schema) da resposta.
                                             * O Swagger lê seus atributos e gera automaticamente a estrutura
                                             * da documentação.
                                             */
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = CustomMediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    )
                            }
                    ),
                    /*
                     * 400 - requisição inválida
                     * Documenta o caso em que o ID da URL não é um número (ex.: /book/abc).
                     */
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID (not a number)"
                    ),
                    /*
                     * 404 - não encontrado
                     * Documenta o caso em que não existe livro com o ID informado.
                     */
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"
                    )
            }
    )
    BookDTO findById(Long id);


    // GET /book
    @Operation(
            summary = "Find all Books",
            description = "Here we are getting all data from book table",
            responses = {
                    /*
                     * Sem o bloco "content" de propósito: o retorno é um CollectionModel
                     * (HATEOAS), e não um array puro de BookDTO. Assim o springdoc monta o
                     * schema a partir do tipo de retorno e dos formatos declarados no
                     * "produces" do Controller, em vez de mostrar um array que a API
                     * não devolve de fato.
                     */
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved all books, with HATEOAS links (an empty collection is returned when there are no records)"
                    )
            }
    )
    CollectionModel<BookDTO> findAll();


    // POST /book
    @Operation(
            summary = "Create a Book",
            description = "Here we are creating a new book from the data sent in the request body",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Book successfully created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = CustomMediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Malformed body or invalid data"
                    ),
                    @ApiResponse(
                            responseCode = "415",
                            description = "Unsupported Content-Type (use JSON, XML or YAML)"
                    )
            }
    )
    ResponseEntity<BookDTO> create(BookInsertDTO bookInsert);


    // PUT /book/{id}
    @Operation(
            summary = "Update a Book",
            description = "Here we are updating an existing book by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Book successfully updated",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = MediaType.APPLICATION_XML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    ),
                                    @Content(
                                            mediaType = CustomMediaType.APPLICATION_YAML_VALUE,
                                            schema = @Schema(implementation = BookDTO.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Malformed body, invalid data or ID that is not a number"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"
                    ),
                    @ApiResponse(
                            responseCode = "415",
                            description = "Unsupported Content-Type (use JSON, XML or YAML)"
                    )
            }
    )
    BookDTO update(Long id, BookUpdateDTO bookUpdate);


    // DELETE /book/{id}
    @Operation(
            summary = "Delete a Book",
            description = "Here we are deleting a book by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Book successfully deleted (no content returned)"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID (not a number)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Book not found"
                    )
            }
    )
    ResponseEntity<?> delete(Long id);

}