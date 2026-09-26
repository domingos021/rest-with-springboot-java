package com.dinisjovete.restwithspringbootjava.data.dto;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

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
public class PersonInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    @NotBlank(message = "Gender is required")
    @Size(min = 1, max = 20, message = "Gender must be between 1 and 20 characters")
    private String gender;

    // Opcional no cadastro (pode assumir um padrão CLIENT se vier nulo)
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
}