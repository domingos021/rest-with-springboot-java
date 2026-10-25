package com.dinisjovete.restwithspringbootjava.data.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


// DTO DE ENTRADA -> o que chega no corpo da requisição para criar/atualizar um livro
// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - BOOK CREATION/UPDATE INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for creating or updating books
// (POST /book, PUT /book/{id}).
//
// Key Characteristics:
// - Excludes 'id': The ID does not exist prior to database insertion (auto-generated).
// - Receives 'authorId' instead of a full Person object: the client only informs
//   WHICH person is the author. The Service layer loads the Person entity
//   from the database and links it to the Book.
// - Includes Validation Annotations: Guarantees that mandatory fields are provided
//   and respect size/formatting constraints before reaching the Service layer.
// ============================================================================

/**
 * Data Transfer Object for receiving book creation/update data payloads (HTTP POST / PUT).
 */
@JsonPropertyOrder({"title", "price", "launch_date", "author_id"})
public class BookInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO JSON (@JsonProperty)
    // ========================================================================
    // Mapeia o atributo Java (camelCase) para o nome do contrato JSON (snake_case).
    // ========================================================================

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;


    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 decimal places")
    private BigDecimal price;


    @NotNull(message = "Launch date is required")
    @JsonProperty("launch_date")
    private LocalDateTime launchDate;


    // ========================================================================
    // AUTOR DO LIVRO
    // ========================================================================
    // Na entidade Book o autor é um objeto Person (@ManyToOne). Na entrada da API
    // basta o identificador dessa pessoa, que vira a coluna author_id no banco.
    // ========================================================================

    @NotNull(message = "Author id is required")
    @Positive(message = "Author id must be a positive number")
    @JsonProperty("author_id")
    private Long authorId;


    // Default Constructor
    public BookInsertDTO() {
    }

    // Parameterized Constructor
    public BookInsertDTO(String title, BigDecimal price, LocalDateTime launchDate, Long authorId) {
        this.title = title;
        this.price = price;
        this.launchDate = launchDate;
        this.authorId = authorId;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDateTime launchDate) {
        this.launchDate = launchDate;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "BookInsertDTO{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", launchDate=" + launchDate +
                ", authorId=" + authorId +
                '}';
    }
}