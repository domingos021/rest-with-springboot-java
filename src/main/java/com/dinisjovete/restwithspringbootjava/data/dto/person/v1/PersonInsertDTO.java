package com.dinisjovete.restwithspringbootjava.data.dto.person.v1;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;


//DTO DE ENTRADA -> o que e salvo no banco apos criação de um novo objeto entidade
// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON CREATION/UPDATE INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for creating or updating persons (POST /person, PUT /person/{id}).
//
// Key Characteristics:
// - Excludes 'id': The ID does not exist prior to database insertion (auto-generated).
// - Includes Validation Annotations: Guarantees that mandatory fields are provided
//   and respect size/formatting constraints before reaching the Service layer.
// ============================================================================

/**
 * Data Transfer Object for receiving person creation/update data payloads (HTTP POST / PUT).
 */

@JsonPropertyOrder({"first_name", "last_name", "email", "password", "cpf", "gender", "address", "role"})
public class PersonInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO JSON (@JsonProperty)
    // ========================================================================
    // Objetivo: Mapeia o atributo Java (padrão camelCase) para um nome diferente
    // no contrato JSON (padrão snake_case), atendendo a padrões de API REST.
    // ========================================================================

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;


    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 14, message = "CPF must be between 11 and 14 characters")
    private String cpf;


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    // ========================================================================
// CONFIGURAÇÃO DE VALIDAÇÃO DA SENHA
// ========================================================================
//
// Regras da senha:
// - Deve possuir entre 8 e 100 caracteres.
// - Deve conter pelo menos:
//      * uma letra maiúscula;
//      * uma letra minúscula;
//      * um número;
//      * um caractere especial.
//
// Exemplos de senhas válidas:
//      Diniz123@
//      Java2026#
//      MinhaSenha1+
//
// Exemplos inválidos:
//      12345678       (somente números)
//      diniz123       (sem letra maiúscula e caractere especial)
//      DINIZ123@      (sem letra minúscula)
//
// ========================================================================

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, max = 100, message = "A senha deve possuir entre 8 e 100 caracteres")

            @Pattern(
                    regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*$",
                    message = "A senha deve conter pelo menos uma letra maiúscula, uma letra minúscula, um número e um caractere especial (exemplo: @, #, $, %, !, ?)"
            )

    private String password;


    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    // ========================================================================
    // CONFIGURAÇÃO DE VISIBILIDADE DO CAMPO GÊNERO
    // ========================================================================
    //
    // O gênero faz parte dos dados recebidos durante o cadastro da pessoa.
    //
    // WRITE_ONLY:
    // - Permite que o cliente envie o valor no JSON de entrada.
    // - Impede que esse valor seja retornado nas respostas JSON da API.
    //
    // Útil quando um campo:
    // - é necessário para criação ou atualização;
    // - não deve ser exposto no retorno por regra de negócio ou privacidade.
    //
    // Diferente de @JsonIgnore:
    // - @JsonIgnore remove completamente o campo do processo Jackson.
    // - WRITE_ONLY permite escrita, mas bloqueia somente a leitura.
    //
    // ========================================================================

    @NotBlank(message = "O género é obrigatório")
    @Size(min = 1, max = 20, message = "O género deve possuir entre 1 e 20 caracteres")
    private String gender;


    @NotNull(message = "O perfil de acesso é obrigatório")
    private PersonRole role;

    // Default Constructor
    public PersonInsertDTO() {
    }

    // Parameterized Constructor
    public PersonInsertDTO(String firstName, String lastName, String cpf, String email, String password, String address, String gender, PersonRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.address = address;
        this.gender = gender;
        this.role = role;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public PersonRole getRole() {
        return role;
    }

    public void setRole(PersonRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "PersonInsertDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                '}';
    }
}