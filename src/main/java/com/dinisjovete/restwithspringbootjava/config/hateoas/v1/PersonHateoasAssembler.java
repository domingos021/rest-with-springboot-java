package com.dinisjovete.restwithspringbootjava.config.hateoas.v1;


import com.dinisjovete.restwithspringbootjava.controllers.person.v1.PersonController;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/*
 * ============================================================================
 *
 * PERSON HATEOAS ASSEMBLER V1
 *
 * ============================================================================
 *
 * Responsável por adicionar links HATEOAS ao recurso PersonDTO.
 *
 * Este assembler trabalha com um recurso individual:
 *
 *      GET /person/v1/{id}
 *
 * Exemplo:
 *
 *      GET /person/v1/1
 *
 * A resposta será enriquecida com links que permitem ao cliente descobrir
 * ações disponíveis sobre este recurso.
 *
 * ============================================================================
 */
@Component
public class PersonHateoasAssembler {


    /*
     * ============================================================================
     *
     * ADD LINKS
     *
     * ============================================================================
     *
     * Recebe um PersonDTO já convertido pelo Service.
     *
     * Fluxo:
     *
     *      Repository
     *           |
     *           v
     *      Entity
     *           |
     *           v
     *      Mapper
     *           |
     *           v
     *      Service
     *           |
     *           v
     *      PersonDTO
     *           |
     *           v
     *      HATEOAS adiciona links
     *
     * ============================================================================
     */
    public PersonDTO addLinks(PersonDTO person) {


        Long id = person.getId();



        /*
         * ============================================================================
         *
         * SELF
         *
         * ============================================================================
         *
         * Link que aponta para o próprio recurso.
         *
         * Permite que o cliente saiba:
         *
         * "Qual é a URL deste objeto?"
         *
         * Exemplo:
         *
         *      GET /person/v1/1
         *
         * Resultado:
         *
         *      self -> /person/v1/1
         *
         * ============================================================================
         */
        person.add(
                linkTo(
                        methodOn(PersonController.class)
                                .findById(id)
                )
                        .withSelfRel()
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
         *      DELETE /person/v1/1
         *
         * Observação:
         *
         * O método não é executado.
         *
         * O Spring HATEOAS apenas utiliza a assinatura do método para gerar
         * a URL correta.
         *
         * ============================================================================
         */
        person.add(
                linkTo(
                        methodOn(PersonController.class)
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
         * Retorna o mesmo DTO agora enriquecido com links.
         *
         * Antes:
         *
         *      PersonDTO
         *
         * Depois:
         *
         *      PersonDTO
         *          |
         *          +-- self
         *          +-- delete
         *
         * ============================================================================
         */
        return person;
    }
}