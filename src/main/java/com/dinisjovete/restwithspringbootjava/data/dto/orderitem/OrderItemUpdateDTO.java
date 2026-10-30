package com.dinisjovete.restwithspringbootjava.data.dto.orderitem;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;

// DTO DE ENTRADA -> o que chega no corpo da requisição para atualizar um item do pedido
// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - ORDER ITEM UPDATE INPUT CONTRACT
// ============================================================================
//
// Este DTO representa o contrato de entrada utilizado para atualização dos dados
// editáveis de um item do pedido (PUT /order/{orderId}/item/{id}).
//
// IMPORTANTE:
// Um DTO de Update deve conter somente os campos que a regra de negócio permite
// alterar através de uma operação comum.
//
// No item, o único dado que muda por uma operação comum é a QUANTIDADE.
//
// Dados de identidade, de relacionamento, congelados, calculados e de auditoria,
// como:
// - id          (vem pela URL, nunca pelo corpo)
// - orderId     (vem pela URL; um item não troca de pedido)
// - productId   (o produto define o item; para trocar de produto, remove-se o
//                item e adiciona-se outro)
// - unitPrice   (preço congelado no momento da compra; não é editável)
// - discount    (regra de negócio específica, não decidida pelo cliente)
// - subtotal    (calculado pela entidade)
// - createdAt   (auditoria automática)
//
// Não pertencem a este DTO.
//
// REGRAS NA CAMADA DE SERVIÇO:
// - O pedido precisa estar PENDING (depois de pago, o total é fixo).
// - Após alterar a quantidade, o subtotal do item E o total do pedido precisam
//   ser recalculados, senão o total do pedido fica desatualizado.
//
// ============================================================================

/**
 * Data Transfer Object for receiving order item update data payloads (HTTP PUT).
 */
@JsonPropertyOrder({"quantity"})
public class OrderItemUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ========================================================================
    // DADOS EDITÁVEIS DO ITEM
    // ========================================================================
    // Informações que podem ser alteradas ao longo do tempo.
    // O limite máximo é o mesmo do OrderItemInsertDTO (proteção contra estouro
    // do DECIMAL(15,2) do subtotal).
    // ========================================================================

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @Max(value = 9999, message = "Quantity must be at most 9999")
    @JsonProperty("quantity")
    private Integer quantity;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public OrderItemUpdateDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param quantity New quantity of the item
     */
    public OrderItemUpdateDTO(Integer quantity) {
        this.quantity = quantity;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItemUpdateDTO{" +
                "quantity=" + quantity +
                '}';
    }
}

/*
 ============================================================================
 EXEMPLO DE JSON ACEITO PELO PUT
 ============================================================================

 {
     "quantity": 5
 }

 ============================================================================
*/
