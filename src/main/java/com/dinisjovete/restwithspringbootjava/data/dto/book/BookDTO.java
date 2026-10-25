package com.dinisjovete.restwithspringbootjava.data.dto.book;

import com.dinisjovete.restwithspringbootjava.data_model.entities.book.dto.data.AuthorDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;


//DTO DE SAÍDA -> retorno de dados do banco para o cliente (API REST) após a requisição
//O livro não possui dados sensíveis, porém o autor possui dados próprios,
//por isso é utilizado um DTO específico (AuthorDTO) para controlar o que será exposto.

/*
 * ============================================================================
 * TÍTULO DA CLASSE: BookDTO
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 *
 * Representa o contrato oficial de dados (Data Transfer Object) da entidade
 * Book na camada de API.
 *
 * Utilizado para transportar dados entre o servidor e o cliente sem expor
 * diretamente a entidade do banco de dados.
 *
 * O DTO possui apenas informações necessárias para representar um livro.
 *
 * O autor não é representado pela entidade Person diretamente, mas sim por
 * AuthorDTO, evitando expor dados desnecessários da pessoa dentro do contexto
 * de livros.
 * ============================================================================
 */


// Campos com valor null não serão enviados no JSON de resposta.
// Mantém o contrato da API mais limpo evitando propriedades vazias.
@JsonInclude(JsonInclude.Include.NON_NULL)

// Garante que a ordem das propriedades no JSON seja consistente.
@JsonPropertyOrder(
        {
                "ID_livro",
                "Título",
                "Autor",
                "Data_lançamento",
                "Preço"
        }
)

// RepresentationModel -> dá suporte ao HATEOAS para este DTO.
public class BookDTO extends RepresentationModel<BookDTO> implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;


    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO JSON (@JsonProperty)
    // ========================================================================
    // Objetivo:
    // Mapeia o atributo Java (camelCase) para o nome utilizado no contrato JSON.
    // ========================================================================


    @JsonProperty("ID_livro")
    private Long id;


    @JsonProperty("Título")
    private String title;


    /*
     * O autor é representado por AuthorDTO e não diretamente por Person.
     *
     * Isso evita retornar todos os dados da entidade Person, como:
     *
     * - CPF
     * - Email
     * - Endereço
     * - Perfil
     *
     * O contexto do livro precisa apenas dos dados públicos do autor.
     */
    @JsonProperty("Autor")
    private AuthorDTO author;


    // LocalDateTime corresponde ao TIMESTAMP(6) da tabela books.
    @JsonProperty("Data_lançamento")
    private LocalDateTime launchDate;


    // BigDecimal corresponde ao DECIMAL(10,2) da tabela books.
    // Evita erros de arredondamento em valores monetários.
    @JsonProperty("Preço")
    private BigDecimal price;



    // Default Constructor
    public BookDTO() {
    }



    // Parameterized Constructor
    public BookDTO(
            Long id,
            String title,
            AuthorDTO author,
            LocalDateTime launchDate,
            BigDecimal price
    ) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.launchDate = launchDate;
        this.price = price;
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


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public AuthorDTO getAuthor() {
        return author;
    }


    public void setAuthor(AuthorDTO author) {
        this.author = author;
    }


    public LocalDateTime getLaunchDate() {
        return launchDate;
    }


    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }


    public BigDecimal getPrice() {
        return price;
    }


    public void setPrice(BigDecimal price) {
        this.price = price;
    }



    // ========================================================================
    // EQUALS AND HASHCODE (Baseados no ID único)
    // ========================================================================


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof BookDTO bookDTO)) return false;

        return Objects.equals(id, bookDTO.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



    @Override
    public String toString() {
        return "BookDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author=" + author +
                ", launchDate=" + launchDate +
                ", price=" + price +
                '}';
    }
}


/*
A anotação `@JsonPropertyOrder` é utilizada para definir explicitamente a ordem
em que as propriedades (campos) de uma classe Java serão serializadas para o JSON.

Por padrão, o Jackson serializa os campos na ordem em que aparecem na classe
ou conforme regras internas da JVM.

Com essa anotação, garantimos que o JSON gerado siga um padrão visual e um
contrato de API consistente.
*/