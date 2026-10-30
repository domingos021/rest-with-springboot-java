package com.dinisjovete.restwithspringbootjava.data.dto.order;

import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;

// DTO DE ENTRADA -> o que chega no corpo da requisição para atualizar um pedido
// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - ORDER UPDATE INPUT CONTRACT
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para atualização dos dados
// editáveis de um pedido (PUT /order/{id}).
//
// IMPORTANTE:
// Um DTO de Update deve conter somente os campos que a regra de negócio permite
// alterar através de uma operação comum.
//
// No pedido, o único dado que muda por uma operação comum é o STATUS
// (ex.: PENDING -> PAID). A troca é validada pela própria entidade em
// Order.changeStatus(): se a transição não for permitida pelo enum OrderStatus,
// é lançada InvalidOrderStatusTransitionException (a camada de exceções deve
// traduzi-la para uma resposta HTTP 409 - Conflict).
//
// Dados de identidade, de relacionamento, calculados e de auditoria, como:
// - id            (vem pela URL, nunca pelo corpo)
// - customerId    (o cliente define o pedido; trocá-lo é uma regra específica)
// - orderDate     (data em que o pedido foi realizado; não é editável)
// - totalAmount   (calculado a partir dos itens; sem setter público na entidade)
// - items         (alterados pelos endpoints de OrderItem, só com pedido PENDING)
// - createdAt / updatedAt (auditoria automática)
//
// Não pertencem a este DTO.
//
// ============================================================================

/**
 * Data Transfer Object for receiving order update data payloads (HTTP PUT).
 */
@JsonPropertyOrder({"status"})
public class OrderUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // DADOS EDITÁVEIS DO PEDIDO
    // ========================================================================
    // Informações que podem ser alteradas ao longo do tempo.
    // Um valor que não exista no enum OrderStatus é rejeitado pelo Jackson na
    // desserialização (HTTP 400), antes mesmo de chegar à regra de negócio.
    // ========================================================================

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private OrderStatus status;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public OrderUpdateDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param status New status requested for the order
     */
    public OrderUpdateDTO(OrderStatus status) {
        this.status = status;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderUpdateDTO{" +
                "status=" + status +
                '}';
    }
}

/*
 ============================================================================
 EXEMPLO DE JSON ACEITO PELO PUT
 ============================================================================

 {
     "status": "PAID"
 }

 Os valores aceitos são os constantes do enum OrderStatus.

 ============================================================================
*/