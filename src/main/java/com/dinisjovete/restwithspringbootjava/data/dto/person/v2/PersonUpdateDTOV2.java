package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;


// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON UPDATE INPUT CONTRACT V2
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para atualização dos dados
// editáveis de uma pessoa.
//
// IMPORTANTE:
// Nem todos os atributos existentes na entidade Person devem estar disponíveis
// em uma operação de atualização.
//
// Um DTO de Update deve conter somente os campos que a regra de negócio permite
// alterar através de uma operação comum.
//
// Dados de identidade ou segurança, como:
// - id
// - cpf
// - birthDate
// - password
// - role
//
// Não pertencem a este DTO, pois possuem regras específicas e não devem sofrer
// alterações indevidas.
//
// ============================================================================


// Define apenas a ordem de apresentação dos campos no JSON de saída.
// Não altera banco de dados, entidade ou lógica de negócio.
// Serve apenas para organização visual do contrato JSON.
@JsonPropertyOrder({"firstName", "lastName", "phone", "address", "gender"})
public class PersonUpdateDTOV2 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    // ========================================================================
    // DADOS DE PERFIL EDITÁVEIS
    // ========================================================================
    //
    // Estes campos representam informações de perfil da pessoa.
    //
    // Podem ser alterados porque:
    // - podem sofrer correções;
    // - podem mudar ao longo da vida;
    // - não representam a identidade única da pessoa.
    //
    // ========================================================================


    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;


    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;


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


    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;


    @NotBlank(message = "Gender is required")
    @Size(min = 1, max = 20, message = "Gender must be between 1 and 20 characters")
    private String gender;



    // Default Constructor
    public PersonUpdateDTOV2() {
    }


    // Parameterized Constructor
    public PersonUpdateDTOV2(String firstName,
                             String lastName,
                             String phone,
                             String address,
                             String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}


/*
 ============================================================================
 EXEMPLO DE JSON ACEITO PELO PUT
 ============================================================================

 {
     "firstName": "Diniz",
     "lastName": "Jovete",
     "phone": "+55 61 99999-9999",
     "address": "Rua Principal, 123",
     "gender": "M"
 }


 OBSERVAÇÃO:

 Campos como:

 - cpf
 - birthDate
 - email
 - password
 - role

 não aparecem neste DTO porque não fazem parte de uma atualização comum.

 Caso exista necessidade de alterar algum deles, o ideal é criar um endpoint
 específico com regras próprias de segurança e validação.

 Exemplo:

 PUT /person/{id}/password
 PUT /person/{id}/email
 PUT /person/{id}/identity


 ============================================================================
*/