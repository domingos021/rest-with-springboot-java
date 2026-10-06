package com.dinisjovete.restwithspringbootjava.data.dto.person.v1;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/*
 * ============================================================================
 * TÍTULO DA CLASSE: PersonDTO
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Representa o contrato oficial de dados (Data Transfer Object) da entidade
 * Person na camada de API. Utilizado para transportar dados de forma segura
 * entre o servidor e o cliente, omitindo informações sensíveis (como senhas)
 * e servindo como padrão unificado para todas as estratégias de mapeamento
 * (Manual, Dozer e MapStruct).
 * ============================================================================
 */
public class PersonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;
    private String cpf;
    private String email;
    private String address;
    private String gender;
    private PersonRole role;

    // Default Constructor
    public PersonDTO() {
    }

    // Parameterized Constructor
    public PersonDTO(Long id, String firstName, String lastName, String cpf, String email, String address, String gender, PersonRole role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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
    // EQUALS AND HASHCODE (Baseados no ID único)
    // ========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO personDTO)) return false;
        return Objects.equals(id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", role=" + role +
                '}';
    }
}