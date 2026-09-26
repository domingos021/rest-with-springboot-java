package com.dinisjovete.restwithspringbootjava.data_model.entities;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

/*
 * A classe inteira corresponde a uma entidade do banco de dados, representando uma tabela chamada "person".
 * Cada instância da classe Person representa uma linha (registro) nessa tabela.
 * A anotação @Entity indica que a classe é uma entidade JPA (Java Persistence API).
 * A anotação @Table(name = "person") especifica o nome da tabela no banco de dados que essa entidade representa.
 * A interface Serializable permite que objetos dessa classe possam ser convertidos em uma sequência de bytes, o que é útil para persistência, envio pela rede ou armazenamento em cache.
 * O campo serialVersionUID é usado para garantir a compatibilidade durante a serialização e desserialização, evitando problemas quando a classe é modificada.
 */
@Entity
@Table(name = "person")
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /*
     * A cada atributo da classe corresponde a uma coluna na tabela "person" do banco de dados.
     * A anotação @Id indica que o atributo 'id' é a chave primária da tabela, garantindo que cada registro seja único.
     * Os demais atributos representam informações pessoais e são mapeados para colunas correspondentes na tabela.
     * A classe possui construtores, getters e setters para manipulação dos dados, além de métodos equals e hashCode para comparação e armazenamento eficiente em coleções.
     */
    /*
     *  nullable = false -> não pode ser nulo
     *  length = 70 -> tamanho máximo do campo
     *  @Column() -> quando não definimos o nome, significa que o nome da coluna será o mesmo do atributo
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // incremento automático do id
    private Long id;

    @Column(name = "first_name", nullable = false, length = 70)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 70)
    private String lastName;

    @Column(name = "cpf", unique = true, nullable = false, length = 100)
    private String cpf;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "gender", length = 10)
    private String gender;

    /*
     * Persistent PersonRole enum field stored as String in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonRole role;

    public Person() {}

    public Person(
            Long id,
            String firstName,
            String lastName,
            String cpf,
            String email,
            String password,
            String address,
            String gender,
            PersonRole role
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cpf = cpf;
        this.email = email;
        this.password = password;
        this.address = address;
        this.gender = gender;
        this.role = role;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        // Compara apenas pelo ID único do banco
        return id != null && id.equals(person.getId());
    }

    @Override
    public int hashCode() {
        // Retorna um valor fixo ou baseado no ID para evitar que mude se o objeto for alterado
        return getClass().hashCode();
    }
}