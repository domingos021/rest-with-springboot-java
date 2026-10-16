package com.dinisjovete.restwithspringbootjava.config.hateoas.v2;


import com.dinisjovete.restwithspringbootjava.controllers.person.v2.PersonControllerV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/*
 * ============================================================================
 *
 * PERSON HATEOAS ASSEMBLER V2
 *
 * ============================================================================
 *
 * Responsável por adicionar links HATEOAS ao recurso PersonDTOV2.
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
public class PersonHateoasAssemblerV2 {


    /*
     * ============================================================================
     *
     * ADD LINKS
     *
     * ============================================================================
     *
     * Recebe um PersonDTOV2 já preenchido pelo Service.
     *
     * Exemplo:
     *
     *      Service
     *          |
     *          v
     *      PersonDTOV2
     *          |
     *          v
     *      HATEOAS adiciona links
     *          |
     *          v
     *      Resposta HTTP
     *
     * ============================================================================
     */
    public PersonDTOV2 addLinks(PersonDTOV2 person) {


        Long id = person.getId();



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
         *      GET /person/v2/1
         *
         * Resultado:
         *
         *      self -> /person/v2/1
         *
         * ============================================================================
         */
        person.add(
                linkTo(
                        methodOn(PersonControllerV2.class)
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
         *      PUT /person/v2/1
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
        person.add(
                linkTo(
                        methodOn(PersonControllerV2.class)
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
         *      DELETE /person/v2/1
         *
         * ============================================================================
         */
        person.add(
                linkTo(
                        methodOn(PersonControllerV2.class)
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
         *      PersonDTOV2
         *
         * Depois:
         *
         *      PersonDTOV2
         *          |
         *          +-- self
         *          +-- update
         *          +-- delete
         *
         * ============================================================================
         */
        return person;
    }
}