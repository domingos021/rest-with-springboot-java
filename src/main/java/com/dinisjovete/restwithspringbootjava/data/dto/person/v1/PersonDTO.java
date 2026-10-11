package com.dinisjovete.restwithspringbootjava.data.dto.person.v1;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


//DTO DE SAÍDA -> retorno de dados do banco para o cliente (API REST) apos a requisição
//dados sensitives como senha não são retornados, apenas dados públicos, cpf e emails podem voltar mascarados
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

// Campos com valor null não serão enviados no JSON de resposta.
// Mantém o contrato da API mais limpo evitando propriedades vazias.
@JsonInclude(JsonInclude.Include.NON_NULL)
//garante que a ordem das propriedades no JSON seja consistente
@JsonPropertyOrder({"ID_usuário", "Nome", "Sobrenome", "Email", "CPF", "Género", "Endereço", "Perfil"})
public class PersonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("ID_usuário")
    private Long id;
    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO JSON (@JsonProperty)
    // ========================================================================
    // Objetivo: Mapeia o atributo Java (padrão camelCase) para um nome diferente
    // no contrato JSON (padrão snake_case), atendendo a padrões de API REST.
    // ========================================================================
    @JsonProperty("Nome")
    private String firstName;

    @JsonProperty("Sobrenome")
    private String lastName;

    @JsonProperty("CPF")
    @JsonSerialize(using = CpfMaskSerializer.class)
    private String cpf; // Sai mascarado no JSON (ex: 123.***.***-01)

    @JsonProperty("Email")
    @JsonSerialize(using = EmailMaskSerializer.class)
    private String email; // Sai mascarado no JSON (ex: di***@gmail.com)

    @JsonProperty("Endereço")
    private String address;


    @JsonProperty("Género")
    private String gender;


    // @JsonIgnore -> impede que o campo seja serializado no JSON de resposta.
    // Usar somente quando o dado não deve ser exposto pela API.
    @JsonProperty("Perfil")
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

    // ========================================================================
    // SERIALIZADOR CUSTOMIZADO: MASCARAMENTO DE CPF
    // ========================================================================
    // Objetivo: Intercepta a serialização da string do campo 'cpf' e aplica
    // uma máscara de segurança (ex: transformando "12345678901" em
    // "123.***.***-01") antes de enviá-lo na resposta JSON.
    // ========================================================================
    public static class CpfMaskSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null || value.length() < 11) {
                gen.writeString(value); // Retorna o valor original se for nulo ou inválido
            } else {
                // Transforma "12345678901" em "123.***.***-01"
                String mascarado = value.substring(0, 3) + ".***.***-" + value.substring(9, 11);
                gen.writeString(mascarado);
            }
        }
    }

    // ========================================================================
    // SERIALIZADOR CUSTOMIZADO: MASCARAMENTO DE E-MAIL
    // ========================================================================
    // Objetivo: Intercepta a serialização do campo 'email' e aplica uma máscara
    // de segurança preservando o domínio (ex: transformando "diniz@gmail.com"
    // em "di***@gmail.com") antes de enviá-lo na resposta JSON.
    // ========================================================================
    public static class EmailMaskSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null || !value.contains("@")) {
                gen.writeString(value); // Retorna o valor original se for nulo ou inválido
            } else {
                // Divide o e-mail em nome de usuário e domínio
                String[] partes = value.split("@");
                String nome = partes[0];
                String dominio = partes[1];

                // Mascara o nome do usuário mantendo os primeiros caracteres
                String nomeMascarado;
                if (nome.length() <= 2) {
                    nomeMascarado = nome.charAt(0) + "***";
                } else {
                    nomeMascarado = nome.substring(0, 2) + "***";
                }

                // Escreve o e-mail mascarado no JSON (ex: di***@gmail.com)
                gen.writeString(nomeMascarado + "@" + dominio);
            }
        }
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

/*
A anotação `@JsonPropertyOrder` é utilizada para definir explicitamente a **ordem**
 em que as propriedades (campos) de uma classe Java serão serializadas para o JSON.

 Por padrão, o Jackson serializa os campos na ordem em que eles aparecem na classe
 ou de forma imprevisível (dependendo da reflexão da JVM). Com essa anotação,
 você garante que o JSON gerado siga um padrão visual ou de contrato de API estrito.
 */