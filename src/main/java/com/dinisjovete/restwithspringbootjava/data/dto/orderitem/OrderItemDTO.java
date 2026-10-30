package com.dinisjovete.restwithspringbootjava.data.dto.orderitem;

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
//O item não expõe a entidade Product: o produto é representado apenas pelo seu identificador
//(product_id). Product é uma hierarquia (Book, Movie, Game); os dados completos do produto
//são obtidos pelo link HATEOAS ou pelo endpoint próprio de cada tipo.

/*
 * ============================================================================
 * TÍTULO DA CLASSE: OrderItemDTO
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 *
 * Representa o contrato oficial de dados (Data Transfer Object) da entidade
 * OrderItem (Item do Pedido) na camada de API.
 *
 * Utilizado dentro de OrderDTO (lista "items") e também como retorno dos
 * endpoints do próprio item.
 *
 * Pontos importantes do contrato:
 *
 * - unit_price é o preço CONGELADO no momento da compra. Ele não acompanha
 *   mudanças futuras no preço do produto.
 *
 * - subtotal é calculado pela entidade: (unit_price * quantity) - discount.
 *   Existe APENAS neste DTO de saída; o cliente nunca o envia.
 *
 * - O pedido dono do item não aparece no corpo (order_id): ele já está na URL
 *   (/order/{orderId}/item/{id}) e no link HATEOAS.
 * ============================================================================
 */


// Campos com valor null não serão enviados no JSON de resposta.
// Mantém o contrato da API mais limpo evitando propriedades vazias.
@JsonInclude(JsonInclude.Include.NON_NULL)

// Garante que a ordem das propriedades no JSON seja consistente.
@JsonPropertyOrder(
        {
                "id",
                "product_id",
                "quantity",
                "unit_price",
                "discount",
                "subtotal",
                "created_at"
        }
)

// RepresentationModel -> dá suporte ao HATEOAS para este DTO.
public class OrderItemDTO extends RepresentationModel<OrderItemDTO> implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;


    // ========================================================================
    // CONFIGURAÇÃO DE MAPEAMENTO JSON (@JsonProperty)
    // ========================================================================
    // Objetivo:
    // Mapeia o atributo Java (camelCase) para o nome utilizado no contrato JSON
    // (snake_case).
    // ========================================================================


    @JsonProperty("id")
    private Long id;


    // Somente o identificador do produto, nunca a entidade completa.
    @JsonProperty("product_id")
    private Long productId;


    @JsonProperty("quantity")
    private Integer quantity;


    // BigDecimal corresponde ao DECIMAL(15,2) da tabela order_items.
    // Preço unitário congelado no momento da compra.
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;


    // Desconto aplicado neste item (valor monetário, não percentual).
    @JsonProperty("discount")
    private BigDecimal discount;


    // (unit_price * quantity) - discount. Calculado pela entidade.
    @JsonProperty("subtotal")
    private BigDecimal subtotal;


    // Data de criação do registro, controlada pela entidade (@PrePersist).
    @JsonProperty("created_at")
    private LocalDateTime createdAt;



    // Default Constructor
    public OrderItemDTO() {
    }



    /**
     * Parameterized Constructor.
     *
     * @param id        Identificador do item
     * @param productId Identificador do produto comprado
     * @param quantity  Quantidade comprada
     * @param unitPrice Preço unitário congelado no momento da compra
     * @param discount  Desconto aplicado ao item
     * @param subtotal  Valor final do item
     * @param createdAt Data de criação do registro
     */
    public OrderItemDTO(
            Long id,
            Long productId,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal discount,
            BigDecimal subtotal,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discount = discount;
        this.subtotal = subtotal;
        this.createdAt = createdAt;
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


    public Long getProductId() {
        return productId;
    }


    public void setProductId(Long productId) {
        this.productId = productId;
    }


    public Integer getQuantity() {
        return quantity;
    }


    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }


    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }


    public BigDecimal getDiscount() {
        return discount;
    }


    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }


    public BigDecimal getSubtotal() {
        return subtotal;
    }


    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }



    // ========================================================================
    // EQUALS AND HASHCODE (Baseados no ID único)
    // ========================================================================


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof OrderItemDTO orderItemDTO)) return false;

        return Objects.equals(id, orderItemDTO.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", discount=" + discount +
                ", subtotal=" + subtotal +
                ", createdAt=" + createdAt +
                '}';
    }
}


/*
 ============================================================================
 EXEMPLO DE JSON RETORNADO
 ============================================================================

 {
     "id": 10,
     "product_id": 3,
     "quantity": 2,
     "unit_price": 39.90,
     "discount": 0.00,
     "subtotal": 79.80,
     "created_at": "2026-10-27T15:30:00",
     "_links": {
         "self": { "href": "http://localhost:8080/order/1/item/10" }
     }
 }

 ============================================================================
*/
