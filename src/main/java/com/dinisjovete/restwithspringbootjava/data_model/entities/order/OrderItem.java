package com.dinisjovete.restwithspringbootjava.data_model.entities.order;


import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Entidade OrderItem (Item do Pedido).
 *
 * Representa uma linha dentro de um pedido.
 *
 * Um OrderItem representa:
 *
 * - qual produto foi comprado;
 * - quantidade comprada;
 * - preço no momento da compra;
 * - desconto aplicado;
 * - subtotal calculado.
 *
 *
 * Responsabilidades:
 *
 * - armazenar o produto comprado;
 * - congelar o preço no momento da compra;
 * - calcular o subtotal do item;
 * - manter vínculo com Order;
 * - controlar alteração de quantidade.
 *
 *
 * Relacionamentos:
 *
 *
 * Order                 OrderItem
 * -----                 ---------
 * 1       ------------   0..*
 *
 *
 * Um pedido possui vários itens.
 * Um item pertence somente a um pedido.
 *
 *
 * Product               OrderItem
 * -------               ---------
 * 1       ------------   0..*
 *
 *
 * Um produto pode aparecer em vários itens de pedidos diferentes.
 * Cada item referencia um único produto.
 *
 */
@Entity
@Table(name = "order_items")
public class OrderItem implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    /*
     * Pedido ao qual este item pertence.
     *
     * A chave estrangeira fica na tabela order_items:
     *
     * order_items
     * ----------------
     * id
     * order_id  <---- FK
     *
     *
     * O Order é o dono da relação.
     * O OrderItem apenas mantém a referência
     * para o pedido ao qual pertence.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;



    /*
     * Produto comprado neste item.
     *
     * O OrderItem conhece apenas o tipo abstrato Product.
     *
     * Não importa se o produto concreto é:
     *
     * Product
     *    |
     *    +-- Book
     *    +-- Movie
     *    +-- Game
     *
     *
     * O pedido trabalha com Product,
     * não precisa conhecer a implementação concreta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;



    /*
     * Quantidade comprada deste produto.
     */
    @Column(nullable = false)
    private Integer quantity;



    /*
     * Preço congelado no momento da compra.
     *
     * Não utiliza novamente Product.price.
     *
     * Motivo:
     *
     * O preço do produto pode mudar futuramente,
     * mas a compra antiga precisa manter o valor original.
     *
     * Exemplo:
     *
     * Produto hoje:
     * R$ 150,00
     *
     * Compra realizada:
     * R$ 99,90
     *
     * O OrderItem mantém R$ 99,90.
     */
    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice;



    /*
     * Desconto aplicado especificamente neste item.
     *
     * Exemplo:
     *
     * Produto: R$100,00
     * Desconto: R$10,00
     *
     * Subtotal:
     * R$90,00
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private final BigDecimal discount = BigDecimal.ZERO;



    /*
     * Valor final deste item.
     *
     * Fórmula:
     *
     * subtotal =
     * (unitPrice * quantity) - discount
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;



    /*
     * Data de criação do item.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;



    /*
     * Construtor vazio exigido pelo JPA.
     *
     * O Hibernate utiliza este construtor
     * para criar a entidade internamente.
     */
    public OrderItem() {
    }



    /*
     * Cria um novo item de pedido.
     *
     * Recebe:
     *
     * - produto comprado;
     * - quantidade;
     * - preço congelado da compra.
     */
    public OrderItem(Product product, Integer quantity, BigDecimal unitPrice) {

        /*
         * O this indica que o atributo interno
         * recebe o valor recebido pelo parâmetro.
         */
        this.product = product;

        this.quantity = quantity;

        this.unitPrice = unitPrice;


        /*
         * Depois de preencher os dados necessários,
         * calcula o subtotal inicial do item.
         */
        this.calculateSubtotal();
    }



    /*
     * Executado automaticamente antes do INSERT no banco.
     */
    @PrePersist
    protected void onCreate() {

        this.createdAt = LocalDateTime.now();

        /*
         * Garante que o subtotal esteja calculado
         * antes de salvar.
         */
        this.calculateSubtotal();
    }



    /*
     * Executado automaticamente antes do UPDATE no banco.
     */
    @PreUpdate
    protected void onUpdate() {

        /*
         * Recalcula o subtotal caso algum valor
         * do item tenha sido alterado.
         */
        this.calculateSubtotal();
    }




    /*
     * Calcula o valor final deste item.
     *
     * O OrderItem é responsável pelo próprio subtotal.
     *
     * Fórmula:
     *
     * subtotal = (unitPrice * quantity) - discount
     */
    public void calculateSubtotal() {


        /*
         * BigDecimal é utilizado para representar valores monetários.
         *
         * Diferente do tipo double, ele evita problemas de precisão
         * em operações com dinheiro.
         *
         * Exemplo:
         *
         * double:
         * 0.1 + 0.2 pode gerar 0.30000000000000004
         *
         * BigDecimal:
         * 0.1 + 0.2 = 0.3
         *
         * Como BigDecimal é um objeto imutável, suas operações
         * não alteram o valor original.
         *
         * Por isso métodos como add(), subtract() e multiply()
         * retornam um novo objeto com o resultado.
         */


        /*
         * Calcula o valor bruto do item.
         *
         * Multiplica o preço unitário pela quantidade comprada.
         *
         * Exemplo:
         *
         * unitPrice = 59.90
         * quantity  = 2
         *
         * total = 59.90 x 2 = 119.80
         */
        BigDecimal total =
                this.unitPrice.multiply(
                        BigDecimal.valueOf(this.quantity)
                        // multiply -> método da classe nativa BigDecimal que realiza a multiplicação
                        // entre dois valores BigDecimal. É muito usado em cálculos monetários
                        // porque evita problemas de precisão comuns com tipos como double.
                );


        /*
         * O this.subtotal indica que o atributo interno
         * subtotal receberá o resultado final.
         *
         * Resultado:
         *
         * valor bruto - desconto
         */
        this.subtotal =
                total.subtract(this.discount);
                // subtract -> método da classe BigDecimal que subtrai um valor BigDecimal de outro.
    }



    /*
     * Altera a quantidade do item.
     *
     * Este método não é público de propósito.
     *
     * Apenas classes do mesmo pacote podem chamar.
     *
     * O objetivo é impedir alteração direta da quantidade
     * sem passar pelas regras do Order.
     */
    void changeQuantity(Integer quantity) {


        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }


        this.quantity = quantity;

        this.calculateSubtotal();
    }



    public Long getId() {
        return id;
    }



    public Order getOrder() {
        return order;
    }



    public void setOrder(Order order) {

        /*
         * Define o pedido ao qual este item pertence.
         */
        this.order = order;
    }



    public Product getProduct() {
        return product;
    }



    public Integer getQuantity() {
        return quantity;
    }



    public BigDecimal getUnitPrice() {
        return unitPrice;
    }



    public BigDecimal getDiscount() {
        return discount;
    }



    public BigDecimal getSubtotal() {
        return subtotal;
    }



    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof OrderItem)) return false;

        OrderItem other = (OrderItem) o;

        return id != null && id.equals(other.id);
    }



    @Override
    public int hashCode() {

        return getClass().hashCode();
    }
}