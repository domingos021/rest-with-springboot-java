package com.dinisjovete.restwithspringbootjava.data.dto.person.v1;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON UPDATE INPUT CONTRACT
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para atualização dos dados
// editáveis de uma pessoa (Versão 1).
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
// - email
// - password
// - role
//
// Não pertencem a este DTO, pois possuem regras específicas e não devem sofrer
// alterações indevidas.
//
// ============================================================================

/**
 Data Transfer Object for receiving person update data payloads (HTTP PUT).
 */
// Define apenas a ordem de apresentação dos campos no JSON de saída/entrada.
@JsonPropertyOrder({"first_name", "last_name", "address", "gender"})
public class PersonUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // DADOS DE PERFIL EDITÁVEIS
    // ========================================================================
    // Estes campos representam informações de perfil da pessoa que podem ser
    // corrigidas ou alteradas ao longo do tempo.
    // ========================================================================

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    @NotBlank(message = "Gender is required")
    @Size(min = 1, max = 20, message = "Gender must be between 1 and 20 characters")
    private String gender;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public PersonUpdateDTO() {
    }

    /**
     Parameterized Constructor.
     *
     @param firstName Person First Name
     @param lastName  Person Last Name
     @param address   Person Address
     @param gender    Person Gender
     */
    public PersonUpdateDTO(String firstName, String lastName, String address, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
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
     "address": "Rua Principal, 123",
     "gender": "M"
 }

 ============================================================================
*/