package com.dinisjovete.restwithspringbootjava.data.dto.order;

import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


//DTO DE SAÍDA -> retorno de dados do banco para o cliente (API REST) após a requisição
//O pedido não expõe a entidade Person: o cliente é representado apenas pelo seu identificador
//(customer_id). Os dados completos da pessoa são obtidos pelo link HATEOAS ou pelo endpoint
//próprio de Person, evitando expor CPF, e-mail, endereço e outros dados sensíveis.

/*
 * ============================================================================
 * TÍTULO DA CLASSE: OrderDTO
 * ----------------------------------------------------------------------------
 * O QUE ELA FAZ:
 *
 * Representa o contrato oficial de dados (Data Transfer Object) da entidade
 * Order (Pedido) na camada de API.
 *
 * Utilizado para transportar dados entre o servidor e o cliente sem expor
 * diretamente a entidade do banco de dados.
 *
 * Campos calculados ou controlados pelo sistema (total_amount, status,
 * created_at, updated_at) existem APENAS neste DTO de saída. O cliente nunca
 * os envia: o total é sempre recalculado pela entidade a partir dos itens e o
 * status só muda por transição validada (Order.changeStatus()).
 *
 * Os itens do pedido são representados por OrderItemDTO (DTO de retorno da
 * entidade OrderItem).
 * ============================================================================
 */


// Campos com valor null não serão enviados no JSON de resposta.
// Mantém o contrato da API mais limpo evitando propriedades vazias.
@JsonInclude(JsonInclude.Include.NON_NULL)

// Garante que a ordem das propriedades no JSON seja consistente.
@JsonPropertyOrder(
        {
                "id",
                "customer_id",
                "order_date",
                "status",
                "total_amount",
                "items",
                "created_at",
                "updated_at"
        }
)

// RepresentationModel -> dá suporte ao HATEOAS para este DTO.
public class OrderDTO extends RepresentationModel<OrderDTO> implements Serializable {


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


    // Somente o identificador do cliente (Person), nunca a entidade completa.
    @JsonProperty("customer_id")
    private Long customerId;


    // LocalDateTime corresponde ao campo order_date da tabela orders.
    @JsonProperty("order_date")
    private LocalDateTime orderDate;


    // Enum serializado pelo nome (ex.: "PENDING"), igual ao que é gravado no banco.
    @JsonProperty("status")
    private OrderStatus status;


    // BigDecimal corresponde ao DECIMAL(15,2) da tabela orders.
    // Evita erros de arredondamento em valores monetários.
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;


    /*
     * Itens do pedido representados por OrderItemDTO e não pela entidade OrderItem.
     *
     * A lista nasce vazia (e não null) para o JSON sempre devolver "items": []
     * em um pedido sem itens, sem exigir verificação de null no cliente.
     */
    @JsonProperty("items")
    private List<OrderItemDTO> items = new ArrayList<>();


    // Datas de auditoria controladas pela entidade (@PrePersist / @PreUpdate).
    @JsonProperty("created_at")
    private LocalDateTime createdAt;


    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;



    // Default Constructor
    public OrderDTO() {
    }



    /**
     * Parameterized Constructor.
     *
     * @param id          Identificador do pedido
     * @param customerId  Identificador do cliente (Person)
     * @param orderDate   Data em que o pedido foi realizado
     * @param status      Status atual do pedido
     * @param totalAmount Total do pedido (soma dos subtotais dos itens)
     * @param items       Itens do pedido
     * @param createdAt   Data de criação do registro
     * @param updatedAt   Data da última atualização do registro
     */
    public OrderDTO(
            Long id,
            Long customerId,
            LocalDateTime orderDate,
            OrderStatus status,
            BigDecimal totalAmount,
            List<OrderItemDTO> items,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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


    public Long getCustomerId() {
        return customerId;
    }


    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }


    public OrderStatus getStatus() {
        return status;
    }


    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }


    public List<OrderItemDTO> getItems() {
        return items;
    }


    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }



    // ========================================================================
    // EQUALS AND HASHCODE (Baseados no ID único)
    // ========================================================================


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof OrderDTO orderDTO)) return false;

        return Objects.equals(id, orderDTO.id);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }



    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", items=" + items +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


/*
 ============================================================================
 EXEMPLO DE JSON RETORNADO (pedido recém-criado, ainda sem itens)
 ============================================================================

 {
     "id": 1,
     "customer_id": 7,
     "order_date": "2026-10-27T15:23:00",
     "status": "PENDING",
     "total_amount": 0.00,
     "items": [],
     "created_at": "2026-10-27T15:23:00",
     "updated_at": "2026-10-27T15:23:00",
     "_links": {
         "self": { "href": "http://localhost:8080/order/1" }
     }
 }

 ============================================================================
*/

/*
A anotação `@JsonPropertyOrder` é utilizada para definir explicitamente a ordem
em que as propriedades (campos) de uma classe Java serão serializadas para o JSON.

Por padrão, o Jackson serializa os campos na ordem em que aparecem na classe
ou conforme regras internas da JVM.

Com essa anotação, garantimos que o JSON gerado siga um padrão visual e um
contrato de API consistente.
*/
