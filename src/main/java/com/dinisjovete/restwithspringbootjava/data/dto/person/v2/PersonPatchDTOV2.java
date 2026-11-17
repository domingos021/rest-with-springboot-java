package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON PATCH INPUT CONTRACT V2
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para a atualização
// PARCIAL dos dados de uma pessoa (Verbo HTTP PATCH).
//
// DIFERENÇA DO UPDATE (PUT):
// No PUT, todos os campos editáveis são obrigatórios. No PATCH, todos os campos
// são OPCIONAIS. Se um campo vier como null, significa que ele não deve ser
// alterado no banco de dados.
//
// Por isso, NÃO utilizamos anotações como @NotBlank aqui, permitindo que
// o cliente envie apenas o campo que deseja modificar (ex: apenas o telefone).
// ============================================================================

@JsonPropertyOrder({"firstName", "lastName", "phone", "address", "gender"})
public class PersonPatchDTOV2 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 20, message = "O telefone deve possuir no máximo 20 caracteres")
    @Pattern(
            regexp = "^[0-9+()\\s-]+$",
            message = "O telefone possui formato inválido"
    )
    private String phone;

    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    @Size(min = 1, max = 20, message = "Gender must be between 1 and 20 characters")
    private String gender;

    // Default Constructor
    public PersonPatchDTOV2() {
    }

    // Parameterized Constructor
    public PersonPatchDTOV2(String firstName,
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
 EXEMPLO DE JSON VÁLIDO PARA O PATCH (Atualizando apenas o telefone)
 ============================================================================

 {
     "phone": "+55 61 98888-8888"
 }

 O Service deverá verificar se o campo recebido não é nulo antes de
 aplicá-lo na entidade existente.
 ============================================================================
*/