package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON CREATION/UPDATE INPUT CONTRACT
// ============================================================================
//
// OBJETIVO PRINCIPAL:
// Representa o corpo da requisição recebido pela API para criação ou atualização
// dos dados de uma pessoa (POST /person, PUT /person/{id}).
//
// CARACTERÍSTICAS:
// - Não possui o campo "id", pois ele é gerado automaticamente pelo banco.
// - Possui validações para garantir que os dados obrigatórios estejam corretos
//   antes de chegar à camada Service.
// ============================================================================

/**
 * Data Transfer Object responsável por receber os dados de criação e atualização
 * de uma pessoa através da API REST.
 */
@JsonPropertyOrder({
        "firstName",
        "lastName",
        "birthDate",
        "email",
        "password",
        "cpf",
        "phone",
        "gender",
        "address",
        "role"
})
public class PersonInsertDTOV2 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    // ========================================================================
    // CONFIGURAÇÃO DE VALIDAÇÃO DOS DADOS PESSOAIS
    // ========================================================================
    //
    // Objetivo:
    // Garante que os campos obrigatórios recebam valores válidos antes de
    // chegarem à camada de serviço.
    //
    // As anotações de validação são executadas automaticamente pelo Spring
    // através do @Valid utilizado no Controller.
    // ========================================================================

    @NotBlank(message = "O primeiro nome é obrigatório")
    @Size(min = 2, max = 50, message = "O primeiro nome deve ter entre 2 e 50 caracteres")
    private String firstName;


    @NotBlank(message = "O sobrenome é obrigatório")
    @Size(min = 2, max = 50, message = "O sobrenome deve ter entre 2 e 50 caracteres")
    private String lastName;


    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO E VALIDAÇÃO DE DATA (@JsonFormat)
    // ========================================================================
    //
    // Objetivo:
    // Define o formato esperado para conversão da data entre JSON e Java.
    //
    // Formato utilizado:
    // dd/MM/yyyy
    //
    // Exemplo:
    // 15/05/1990
    //
    // Caso a data esteja em formato incorreto ou não seja uma data passada,
    // a validação será rejeitada antes de chegar ao Service.
    // ========================================================================

    @NotNull(message = "A data de nascimento é obrigatória. Utilize o formato dd/MM/yyyy (exemplo: 15/05/1990)")
    @Past(message = "A data de nascimento deve ser uma data passada")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;


    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres")
    private String cpf;


    // ========================================================================
    // CONFIGURAÇÃO DO TELEFONE
    // ========================================================================
    //
    // O telefone não é obrigatório neste momento do desenvolvimento.
    //
    // Motivo:
    // - A coluna no banco permite valor NULL.
    // - O cadastro pode ocorrer sem telefone.
    // - Caso seja informado, deve respeitar o limite de caracteres.
    // ========================================================================

    /*
     * Regex utilizada para validação do telefone:
     *
     * [0-9]   → permite números de 0 a 9.
     * +       → permite o símbolo de código internacional.
     * ( )     → permite parênteses para formatação do DDD.
     * \s      → permite espaços entre os números.
     * -       → permite hífen para separação dos números.
     * + no final → significa que deve existir um ou mais caracteres válidos.
     *
     * Exemplos aceitos:
     * +55 61 99999-9999
     * (61) 99999-9999
     * 61-99999-9999
     */
    @Size(max = 20, message = "O telefone deve possuir no máximo 20 caracteres")
    @Pattern(
            regexp = "^[0-9+()\\s-]+$",
            message = "O telefone possui formato inválido"
    )
    private String phone;


    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
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


    @NotBlank(message = "O endereço é obrigatório")
    @Size(min = 5, max = 100, message = "O endereço deve possuir entre 5 e 100 caracteres")
    private String address;


    @NotBlank(message = "O gênero é obrigatório")
    @Size(min = 1, max = 20, message = "O gênero deve possuir entre 1 e 20 caracteres")
    private String gender;


    @NotNull(message = "O perfil de acesso é obrigatório")
    private PersonRole role;


    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    public PersonInsertDTOV2() {
    }


    public PersonInsertDTOV2(String firstName,
                             String lastName,
                             LocalDate birthDate,
                             String cpf,
                             String phone,
                             String email,
                             String password,
                             String address,
                             String gender,
                             PersonRole role) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.phone = phone;
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


    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }


    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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


    // ========================================================================
    // TO STRING
    // ========================================================================
    @Override
    public String toString() {
        return "PersonInsertDTOV2{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", cpf='" + cpf + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                '}';
    }
}