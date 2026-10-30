package com.dinisjovete.restwithspringbootjava.data.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;


// DTO DE ENTRADA -> o que chega no corpo da requisição para criar um pedido
// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - ORDER CREATION INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for creating orders
// (POST /order).
//
// Key Characteristics:
// - Excludes 'id': The ID does not exist prior to database insertion (auto-generated).
// - Receives 'customerId' instead of a full Person object: the client only informs
//   WHICH person is the customer. The Service layer loads the Person entity
//   from the database and links it to the Order.
// - Excludes every field controlled by the system:
//     * status       -> the Order is always born as PENDING (Order constructor).
//     * order_date   -> filled by the entity at creation time.
//     * total_amount -> calculated from the items (Order.calculateTotal()); accepting
//                       it from the client would allow a total that does not match the items.
//     * created_at / updated_at -> audit dates filled by @PrePersist / @PreUpdate.
//     * items        -> handled by the OrderItem endpoints, only while the order is PENDING.
// - Includes Validation Annotations: Guarantees that mandatory fields are provided
//   before reaching the Service layer.
// ============================================================================

/**
 * Data Transfer Object for receiving order creation data payloads (HTTP POST).
 */
@JsonPropertyOrder({"customer_id"})
public class OrderInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // CLIENTE DO PEDIDO
    // ========================================================================
    // Na entidade Order o cliente é um objeto Person (@ManyToOne). Na entrada da API
    // basta o identificador dessa pessoa, que vira a coluna customer_id no banco.
    // ========================================================================

    @NotNull(message = "Customer id is required")
    @Positive(message = "Customer id must be a positive number")
    @JsonProperty("customer_id")
    private Long customerId;


    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public OrderInsertDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param customerId Identifier of the person who is placing the order
     */
    public OrderInsertDTO(Long customerId) {
        this.customerId = customerId;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "OrderInsertDTO{" +
                "customerId=" + customerId +
                '}';
    }
}

/*
 ============================================================================
 EXEMPLO DE JSON ACEITO PELO POST
 ============================================================================

 {
     "customer_id": 7
 }

 ============================================================================
*/