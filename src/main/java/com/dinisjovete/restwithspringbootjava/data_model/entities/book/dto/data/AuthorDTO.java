package com.dinisjovete.restwithspringbootjava.data_model.entities.book.dto.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/*
 * ============================================================================
 * TÍTULO DA CLASSE: AuthorDTO
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 *
 * Representa os dados públicos de uma pessoa quando ela aparece como autor
 * de um livro.
 *
 * Este DTO existe para evitar retornar diretamente a entidade Person dentro
 * do BookDTO, mantendo o contrato da API desacoplado da estrutura interna
 * do banco de dados.
 *
 * Um autor pode possuir vários dados no sistema (CPF, email, endereço, perfil),
 * porém o contexto de um livro necessita apenas das informações básicas do
 * autor.
 * ============================================================================
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @JsonProperty("ID_autor")
    private Long id;


    @JsonProperty("Nome")
    private String firstName;


    @JsonProperty("Sobrenome")
    private String lastName;


    public AuthorDTO() {
    }


    public AuthorDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof AuthorDTO authorDTO)) return false;

        return Objects.equals(id, authorDTO.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}