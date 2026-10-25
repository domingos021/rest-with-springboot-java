package com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;

/*
 * A classe corresponde a uma entidade do banco de dados, representando a tabela "person".
 * Cada instância de Person representa uma linha (registro) nessa tabela.
 * @Entity indica que a classe é uma entidade JPA.
 * @Table(name = "person") define o nome da tabela.
 * Serializable permite converter o objeto em bytes (persistência, rede, cache).
 * serialVersionUID garante compatibilidade na serialização/desserialização.
 */
@Entity
@Table(name = "person")
public class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /*
     * Cada atributo corresponde a uma coluna da tabela "person".
     * nullable = false -> não pode ser nulo
     * length -> tamanho máximo do campo
     * @Column() sem name -> a coluna tem o mesmo nome do atributo
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // incremento automático do id
    private Long id;

    @Column(name = "first_name", nullable = false, length = 70)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 70)
    private String lastName;

    /*
     * length = 14 (tamanho exato para o formato "000.000.000-00")
     */
    @Column(name = "cpf", unique = true, nullable = false, length = 14)
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
     * Papel de ACESSO ao sistema (ex.: ADMIN, USER), armazenado como String no banco.
     * Não confundir com os papéis de negócio (cliente, autor, vendedor), que são
     * definidos pelos relacionamentos de cada entidade.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonRole role;

    /*
     * RELACIONAMENTOS (de propósito, não existem aqui)
     *
     * Person representa apenas o cadastro da pessoa. Os papéis de negócio nascem dos
     * relacionamentos, e quem guarda a chave estrangeira é a entidade do lado "N":
     *
     *  - Book  N ---- 1 Person  -> Book.author   (coluna author_id)    -> papel: Autor
     *  - Order N ---- 1 Person  -> Order.person  (coluna person_id)    -> papel: Cliente
     *
     * Por isso Person NÃO possui listas de books, orders ou products.
     * Para consultar, use os repositórios, por exemplo:
     *   bookRepository.findByAuthorId(personId);
     */

    public Person() {
    }

    public Person(Long id,
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
        // Valor constante recomendado para entidades JPA com ID gerado pelo banco
        return 31;
    }

    // A senha fica de fora do toString de propósito: evita vazar o hash em logs.
    @Override
    public String toString() {
        return "Person{" +
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