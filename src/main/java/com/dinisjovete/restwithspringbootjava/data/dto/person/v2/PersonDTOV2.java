package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.hateoas.RepresentationModel;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/*
 ============================================================================
 TÍTULO DA CLASSE: PersonDTOV2
 ----------------------------------------------------------------------------
 O QUE ELA FAZ:
 Representa o contrato oficial de dados (Data Transfer Object) da entidade
 Person na camada de API (Versão 2).

 Representa apenas o CADASTRO da pessoa. Não carrega livros, pedidos nem
 produtos: esses dados pertencem a outros contextos (Book, Order, Product)
 e são expostos pelos DTOs e endpoints próprios de cada um.

 Inclui novos campos adicionados na evolução do sistema, como birthDate e phone.
 ============================================================================
 */

// Campos com valor null não serão enviados no JSON de resposta.
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
        {
                "ID_usuário",
                "Nome",
                "Sobrenome",
                "Data_nascimento",
                "CPF",
                "Telefone",
                "Email",
                "Endereço",
                "Género",
                "Perfil"
        }
)
// RepresentationModel -> dá suporte ao HATEOAS para este DTO, a configuração é feita no controller
public class PersonDTOV2 extends RepresentationModel<PersonDTOV2> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("ID_usuário")
    private Long id;

    // @JsonProperty separa o nome usado no Java do nome exposto no JSON.
    // Exemplo: firstName (Java) -> "Nome" (JSON)
    @JsonProperty("Nome")
    private String firstName;

    @JsonProperty("Sobrenome")
    private String lastName;

    // Data formatada como "dd/MM/yyyy" no JSON (ex: 15/05/1990)
    @JsonProperty("Data_nascimento")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @JsonProperty("CPF")
    @JsonSerialize(using = CpfMaskSerializer.class)
    private String cpf; // Sai mascarado no JSON (ex: 123.***.***-01)

    @JsonProperty("Telefone")
    private String phone;

    @JsonProperty("Email")
    @JsonSerialize(using = EmailMaskSerializer.class)
    private String email; // Sai mascarado no JSON (ex: di***@gmail.com)

    @JsonProperty("Endereço")
    private String address;

    @JsonProperty("Género")
    private String gender;

    @JsonIgnore
    private PersonRole role;

    // ========================================================================
    // RELACIONAMENTOS (de propósito, não existem aqui)
    // ========================================================================
    //
    // Este DTO NÃO possui lista de livros, pedidos ou produtos.
    // Os papéis de negócio (autor, cliente, vendedor) nascem dos relacionamentos
    // e aparecem no DTO de quem tem a regra:
    //
    //   BookDTO.author     -> resumo da pessoa (id e nome)
    //   OrderDTO.customer  -> resumo da pessoa (id e nome)
    //
    // Para listar os livros de um autor, use um endpoint próprio
    // (ex.: GET /books?authorId={id}) consultando o BookRepository.
    //
    // ========================================================================

    // Default Constructor
    public PersonDTOV2() {
    }

    // Parameterized Constructor
    public PersonDTOV2(Long id,
                       String firstName,
                       String lastName,
                       LocalDate birthDate,
                       String cpf,
                       String phone,
                       String email,
                       String address,
                       String gender,
                       PersonRole role) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.cpf = cpf;
        this.phone = phone;
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
        if (!(o instanceof PersonDTOV2 personDTO)) return false;
        return Objects.equals(id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ========================================================================
    // SERIALIZADOR CUSTOMIZADO: MASCARAMENTO DE CPF
    // ========================================================================

    public static class CpfMaskSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {

            if (value == null || value.length() < 11) {
                gen.writeString(value);
            } else {
                String mascarado = value.substring(0, 3)
                        + ".***.***-"
                        + value.substring(9, 11);

                gen.writeString(mascarado);
            }
        }
    }

    // ========================================================================
    // SERIALIZADOR CUSTOMIZADO: MASCARAMENTO DE E-MAIL
    // ========================================================================

    public static class EmailMaskSerializer extends JsonSerializer<String> {

        @Override
        public void serialize(String value,
                              JsonGenerator gen,
                              SerializerProvider serializers) throws IOException {

            if (value == null || !value.contains("@")) {
                gen.writeString(value);
            } else {

                String[] partes = value.split("@");

                String nome = partes[0];
                String dominio = partes[1];

                String nomeMascarado;

                if (nome.length() <= 2) {
                    nomeMascarado = nome.charAt(0) + "***";
                } else {
                    nomeMascarado = nome.substring(0, 2) + "***";
                }

                gen.writeString(nomeMascarado + "@" + dominio);
            }
        }
    }

    // ========================================================================
    // TO STRING
    // ========================================================================
    //
    // Dados sensíveis como CPF e email são omitidos do toString()
    // para evitar exposição em logs ou mensagens de debug.
    //
    // ========================================================================

    @Override
    public String toString() {
        return "PersonDTOV2{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", phone='" + phone + '\'' +
                ", role=" + role +
                '}';
    }
}