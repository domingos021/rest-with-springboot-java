package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/*
 * ============================================================================
 * TÍTULO DA CLASSE: PersonDTOV2
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Representa o contrato oficial de dados (Data Transfer Object) da entidade
 * Person na camada de API (Versão 2). Utilizado para transportar dados de forma
 * segura entre o servidor e o cliente, omitindo informações sensíveis (como senhas)
 * e incluindo novos campos da evolução do sistema (como birthDate).
 * ============================================================================
 */
public class PersonDTOV2 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;

    // ========================================================================
    // @JsonFormat
    // ========================================================================
    // Garante que a data seja serializada e desserializada no formato padrão
    // ISO (yyyy-MM-dd) nas requisições e respostas JSON (ex: "1990-05-15").
    // ========================================================================
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String cpf;
    private String email;
    private String address;
    private String gender;
    private PersonRole role;

    // Default Constructor
    public PersonDTOV2() {
    }

    // Parameterized Constructor
    public PersonDTOV2(Long id, String firstName, String lastName, LocalDate birthDate, String cpf, String email, String address, String gender, PersonRole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.role = role;
    }


    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
    // EQUALS AND HASHCODE (Baseados no ID único)
    // ========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTOV2 personDTO)) return false;
        return Objects.equals(id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonDTOV2{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                '}';
    }
}