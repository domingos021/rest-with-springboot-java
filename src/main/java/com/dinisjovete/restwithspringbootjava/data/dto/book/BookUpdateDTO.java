package com.dinisjovete.restwithspringbootjava.data.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - BOOK UPDATE INPUT CONTRACT
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para atualização dos dados
// editáveis de um livro (PUT /book/{id}).
//
// IMPORTANTE:
// Um DTO de Update deve conter somente os campos que a regra de negócio permite
// alterar através de uma operação comum.
//
// Dados de identidade e de relacionamento, como:
// - id        (vem pela URL, nunca pelo corpo)
// - authorId  (o autor define a obra; trocá-lo é uma regra específica)
//
// Não pertencem a este DTO.
//
// ============================================================================

/**
 * Data Transfer Object for receiving book update data payloads (HTTP PUT).
 */
@JsonPropertyOrder({"title", "price", "launch_date"})
public class BookUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // DADOS EDITÁVEIS DO LIVRO
    // ========================================================================
    // Informações que podem ser corrigidas ou alteradas ao longo do tempo.
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

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public BookUpdateDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param title      Book title
     * @param price      Book price
     * @param launchDate Book launch date
     */
    public BookUpdateDTO(String title, BigDecimal price, LocalDateTime launchDate) {
        this.title = title;
        this.price = price;
        this.launchDate = launchDate;
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
}

/*
 ============================================================================
 EXEMPLO DE JSON ACEITO PELO PUT
 ============================================================================

 {
     "title": "Dom Casmurro",
     "price": 39.90,
     "launch_date": "1899-01-01T00:00:00"
 }

 ============================================================================
*/