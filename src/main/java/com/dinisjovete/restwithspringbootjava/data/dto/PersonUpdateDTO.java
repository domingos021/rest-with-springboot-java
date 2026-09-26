package com.dinisjovete.restwithspringbootjava.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - PERSON UPDATE INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for updating existing persons (PUT /person/{id}).
//
// Key Differences from PersonInsertDTO (if applicable):
// - Excludes 'id': The target person ID is provided via URL path variable (@PathVariable).
// - Guarantees that mandatory fields are validated before updating the database record.
// ============================================================================

/**
 * Data Transfer Object for receiving person update data payloads (HTTP PUT).
 */
public class PersonUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * Parameterized Constructor.
     *
     * @param firstName Person First Name
     * @param lastName  Person Last Name
     * @param address   Person Address
     * @param gender    Person Gender
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