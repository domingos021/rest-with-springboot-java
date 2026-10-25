package com.dinisjovete.restwithspringbootjava.config.hateoas.book;


import com.dinisjovete.restwithspringbootjava.controllers.book.BookController;
import com.dinisjovete.restwithspringbootjava.data.dto.book.BookDTO;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/*
 * ============================================================================
 *
 * BOOK HATEOAS ASSEMBLER
 *
 * ============================================================================
 *
 * Responsável por adicionar links HATEOAS ao recurso BookDTO.
 *
 * Importante:
 *
 * O HATEOAS pertence à camada de representação HTTP.
 *
 * Ele NÃO deve ficar no:
 *
 *      Service
 *      Mapper
 *      Repository
 *
 * Porque essas camadas trabalham com:
 *
 *      regra de negócio
 *      conversão de dados
 *      persistência
 *
 * O Controller monta a resposta REST e utiliza este assembler para
 * enriquecer o DTO com links navegáveis.
 *
 * ============================================================================
 */
@Component
public class BookHateoasAssembler {


    /*
     * ============================================================================
     *
     * ADD LINKS
     *
     * ============================================================================
     *
     * Recebe um BookDTO já preenchido pelo Service.
     *
     * Exemplo:
     *
     *      Service
     *          |
     *          v
     *      BookDTO
     *          |
     *          v
     *      HATEOAS adiciona links
     *          |
     *          v
     *      Resposta HTTP
     *
     * ============================================================================
     */
    public BookDTO addLinks(BookDTO book) {


        Long id = book.getId();



        /*
         * ============================================================================
         *
         * SELF
         *
         * ============================================================================
         *
         * Representa o próprio recurso.
         *
         * Informa ao cliente:
         *
         * "Este é o endereço para consultar este recurso novamente."
         *
         * Exemplo:
         *
         *      GET /book/1
         *
         * Resultado:
         *
         *      self -> /book/1
         *
         * ============================================================================
         */
        book.add(
                linkTo(
                        methodOn(BookController.class)
                                .findById(id)
                )
                        .withSelfRel()
        );



        /*
         * ============================================================================
         *
         * UPDATE
         *
         * ============================================================================
         *
         * Informa que este recurso pode ser atualizado.
         *
         * O cliente descobre que existe uma operação PUT disponível.
         *
         * Exemplo:
         *
         *      PUT /book/1
         *
         * Observação:
         *
         * O null representa o corpo da requisição.
         *
         * Ele não executa o método update().
         *
         * O Spring HATEOAS utiliza o método apenas para construir a URL.
         *
         * ============================================================================
         */
        book.add(
                linkTo(
                        methodOn(BookController.class)
                                .update(id, null)
                )
                        .withRel("update")
        );



        /*
         * ============================================================================
         *
         * DELETE
         *
         * ============================================================================
         *
         * Informa que este recurso pode ser removido.
         *
         * Exemplo:
         *
         *      DELETE /book/1
         *
         * ============================================================================
         */
        book.add(
                linkTo(
                        methodOn(BookController.class)
                                .delete(id)
                )
                        .withRel("delete")
        );



        /*
         * ============================================================================
         *
         * RETORNO
         *
         * ============================================================================
         *
         * Retorna o próprio DTO agora enriquecido com links HATEOAS.
         *
         * Antes:
         *
         *      BookDTO
         *
         * Depois:
         *
         *      BookDTO
         *          |
         *          +-- self
         *          +-- update
         *          +-- delete
         *
         * ============================================================================
         */
        return book;
    }
}