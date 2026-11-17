package com.dinisjovete.restwithspringbootjava.config.hateoas.person.v2;

import com.dinisjovete.restwithspringbootjava.controllers.person.v2.PersonControllerV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * ============================================================================
 * PERSON HATEOAS ASSEMBLER V2
 * ============================================================================
 *
 * Responsável por adicionar links HATEOAS ao recurso PersonDTOV2.
 *
 * O HATEOAS pertence à camada de representação HTTP. Por isso, esta lógica
 * fica no assembler, que enriquece o DTO com links navegáveis antes de ele
 * ser enviado na resposta REST.
 *
 * As camadas de serviço, mapeamento e persistência continuam responsáveis,
 * respectivamente, pelas regras de negócio, conversão de dados e acesso
 * aos dados.
 * ============================================================================
 */
@Component
public class PersonHateoasAssemblerV2 {

    /**
     * Recebe um PersonDTOV2 preenchido e adiciona a ele os links HATEOAS.
     *
     * Fluxo:
     * Service -> PersonDTOV2 -> HATEOAS adiciona links -> resposta HTTP
     */
    public PersonDTOV2 addLinks(PersonDTOV2 person) {

        Long id = person.getId();

        /*
         * ============================================================================
         * SELF
         * ============================================================================
         *
         * Link para consultar novamente este recurso.
         *
         * Exemplo:
         *     GET /person/v2/1
         *
         * Relação:
         *     self -> /person/v2/1
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
         * UPDATE
         * ============================================================================
         *
         * Link para a operação de atualização completa do recurso.
         *
         * Exemplo:
         *     PUT /person/v2/1
         *
         * O `null` representa o corpo da requisição usado para montar a chamada.
         * O método `update()` não é executado; o Spring HATEOAS usa a chamada
         * para descobrir e construir a URL do link.
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
         * PATCH
         * ============================================================================
         *
         * Link para a operação de atualização parcial do recurso.
         *
         * Exemplo:
         *     PATCH /person/v2/1
         *
         * O `null` representa o corpo da requisição usado para montar a chamada.
         * O método `patch()` não é executado; o Spring HATEOAS usa a chamada
         * para descobrir e construir a URL do link.
         * ============================================================================
         */
        person.add(
                linkTo(
                        methodOn(PersonControllerV2.class)
                                .patch(id, null)
                )
                        .withRel("patch")
        );

        /*
         * ============================================================================
         * DELETE
         * ============================================================================
         *
         * Link para remover este recurso.
         *
         * Exemplo:
         *     DELETE /person/v2/1
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
         * RETORNO
         * ============================================================================
         *
         * Retorna o próprio DTO, agora enriquecido com os links:
         *     self, update, patch e delete.
         * ============================================================================
         */
        return person;
    }
}