package com.dinisjovete.restwithspringbootjava.data_model.entities.order;

import com.dinisjovete.restwithspringbootjava.exception.order.InvalidOrderStatusTransitionException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
private Long id;
private Person customer;
private LocalDateTime orderDate;
private OrderStatus status;
private BigDecimal totalAmount;
private List<OrderItem> items;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
 */

/**
 * Entidade Order (Pedido).
 * <p>
 * Camada: Model (persistência).
 * Responsabilidade: representar a tabela "orders" e proteger suas regras
 * internas (itens, total, status e datas de auditoria).
 * <p>
 * Observação: a tabela se chama "orders" porque ORDER é palavra reservada
 * do SQL (ORDER BY) e causaria erro de sintaxe em vários bancos.
 * <p>
 * Relacionamentos:
 * - N Orders -> 1 Person (customer)  : @ManyToOne
 * - 1 Order  -> N OrderItem (items)  : @OneToMany (Order é o dono da lista)
 * <p>
 * Invariantes protegidas por esta classe:
 * - totalAmount sempre é a soma dos subtotais dos itens (sem setter público).
 * - status só muda por changeStatus(), respeitando as transições do enum.
 * - itens só podem ser alterados enquanto o pedido está PENDING.
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos pedidos pertencem a uma pessoa. LAZY evita carregar a Person sem necessidade.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Person customer;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    // STRING grava o nome do enum ("PENDING"); ORDINAL gravaria a posição e
    // quebraria os dados se alguém reordenasse o enum.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    // BigDecimal para dinheiro (nunca double/float). 15 dígitos no total, 2 casas decimais.
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /*
     * mappedBy = "order": a FK fica na tabela de OrderItem (campo "order" lá).
     * cascade ALL: salvar/remover o pedido propaga para os itens.
     * orphanRemoval: item removido da lista é apagado do banco.
     * *
     * *
     * final protege a referência da lista, ou seja, impede que a variável
     * "items" aponte para outra lista depois de inicializada.
     *
     * Pode:
     * - adicionar itens: items.add(item);
     * - remover itens: items.remove(item);
     * - limpar a lista: items.clear();
     *
     * Não pode:
     * - substituir a lista inteira:
     *   items = new ArrayList<>();
     *
     * A alteração dos itens deve passar pelos métodos de negócio da entidade
     * (addItem/removeItem), garantindo que o relacionamento Order <-> OrderItem
     * e o cálculo do total permaneçam consistentes.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Construtor vazio: obrigatório para o JPA/Hibernate.
    public Order() {
    }

    public Order(Person customer) {

        /*
         * O this indica que o atributo dessa classe (customer) receberá
         * o valor da variável customer(externo) recebida como parâmetro
         * pelo construtor.
         */
        this.customer = customer;

        /*
         * Aqui o construtor não recebe nenhum valor externo para
         * orderDate e status.
         *
         * O this apenas indica que os atributos da classe
         * (orderDate e status) receberão os valores produzidos
         * pelas expressões à direita da atribuição.
         *
         * Neste caso:
         *
         * - LocalDateTime.now() cria/obtém a data e hora atuais.
         * - OrderStatus.PENDING é uma constante do enum OrderStatus.
         *
         * Esses valores não vieram por parâmetro; eles são definidos
         * pelo próprio construtor durante a criação do objeto.
         */
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // ------------------------------------------------------------------
    // Callbacks de ciclo de vida: preenchem as datas de auditoria sozinhos.
    // ------------------------------------------------------------------

    @PrePersist
    //  "Execute este método automaticamente antes de salvar esta entidade pela primeira vez no banco de dados
    protected void onCreate() {

        /*
         * Obtém a data e hora atuais uma única vez e armazena
         * o valor na variável local 'now'.
         *
         * Assim, todos os atributos recebem exatamente o mesmo
         * instante, evitando pequenas diferenças de milissegundos
         * caso LocalDateTime.now() fosse chamado várias vezes.
         */
        LocalDateTime now = LocalDateTime.now();

        /*
         * O this indica que os atributos da entidade receberão
         * o valor armazenado na variável local 'now'.
         */
        this.createdAt = now;
        this.updatedAt = now;

        /*
         * Caso a data do pedido ainda não tenha sido definida,
         * utiliza a mesma data e hora da criação da entidade.
         *
         * Se orderDate já possuir um valor, ele é preservado.
         */
        if (this.orderDate == null) {
            this.orderDate = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        /*
         * A annotation @PreUpdate informa ao JPA/Hibernate que este método
         * deve ser executado automaticamente antes de atualizar uma entidade
         * que já existe no banco de dados.
         *
         * Exemplo:
         *
         * Pedido já salvo:
         * Order id = 1
         * status = PENDING
         *
         * Alteração:
         * status = PAID
         *
         * Antes do UPDATE no banco, o Hibernate chama este método.
         */

        /*
         * O this indica que o atributo da própria entidade
         * (updatedAt) receberá o novo valor.
         *
         * O valor vem de:
         *
         * LocalDateTime.now()
         *
         * que cria a data e hora atual.
         */
        this.updatedAt = LocalDateTime.now();
    }

    // ------------------------------------------------------------------
    // Métodos de negócio
    // ------------------------------------------------------------------

    /**
     * Adiciona um item ao pedido, mantém os dois lados da relação
     * sincronizados e recalcula o total do pedido.
     * <p>
     * A inclusão de itens só é permitida enquanto o pedido estiver
     * no status PENDING.
     */
    public void addItem(OrderItem item) {

        /*
         * Garante que não seja adicionado um item inexistente.
         */
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }

        /*
         * Verifica se o pedido ainda permite alterações.
         *
         * O this indica que estamos chamando um método
         * do próprio objeto Order atual.
         *
         * Exemplo:
         *
         * Pedido id = 1
         * status = PAID
         *
         * this.assertItemsEditable();
         *
         * significa:
         * "Pedido id 1, verifique se seus itens ainda podem ser alterados."
         */
        this.assertItemsEditable();


        /*
         * Mantém os dois lados do relacionamento sincronizados.
         *
         * O Order possui uma lista de OrderItem:
         *
         * Order
         *   |
         *   | 1
         *   |
         *   | N
         * OrderItem
         *
         * Aqui o item passa a saber qual pedido pertence a ele.
         * *
         * insere o objeto pedido na classe OrderItem
         */
        item.setOrder(this); // o this é o objeto Order atual.


        /*
         * Adiciona o item na lista interna de itens deste pedido.
         *
         * O this.items representa a lista de OrderItem
         * pertencente a este objeto Order.
         *
         * Insere o OrderItem na lista de itens do pedido.
         */
        this.items.add(item);


        /*
         * Depois de adicionar um item, o pedido recalcula
         * seu valor total baseado nos itens atuais.
         */
        // AS DUAS FAZEM A MESMA COISA
        //this.calculateTotal(); // usando o for each
        this.calculateTotalComStream(); // usando o stream
    }

    /**
     * Remove um item do pedido e recalcula o total.
     * Graças ao orphanRemoval, o item também é apagado do banco.
     * Só é permitido enquanto o pedido está PENDING.
     */
    public void removeItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        this.assertItemsEditable();

        if (this.items.remove(item)) {
            item.setOrder(null);
            this.calculateTotalComForEach();
        }
    }
    //========================== usando o FOR EACH para iterar os items ========================

    /**
     * Recalcula totalAmount somando o subtotal de cada item.
     * Assume que OrderItem possui getSubtotal() retornando BigDecimal
     * (preço unitário x quantidade).
     */

    public void calculateTotalComForEach() {

        BigDecimal total = BigDecimal.ZERO;

        /*
         * Para cada OrderItem chamado item da lista de itens deste pedido,
         * adiciona o subtotal do item atual ao acumulador total.
         */
        for (OrderItem item : this.items) {
            total = total.add(item.getSubtotal());
        }

        /*
         * O this.totalAmount indica que o atributo da entidade
         * receberá o valor acumulado na variável total,
         * que corresponde à soma dos subtotais de todos os itens do pedido.
         */
        this.totalAmount = total;
    }

    // ================================= Usando o STREAM para fazer a mesma  coisa  ====================
    /*
     * Recalcula o valor total do pedido.
     *
     * Percorre todos os OrderItem pertencentes a este pedido,
     * obtém o subtotal de cada um e soma todos os valores.
     */
    public void calculateTotalComStream() {

        /*
         * O this.totalAmount indica que o atributo da entidade
         * receberá o resultado final da operação realizada pelo Stream.
         *
         * O Stream percorre todos os OrderItem, obtém o subtotal
         * de cada um e soma todos os valores.
         */
        this.totalAmount = this.items.stream()

                /*
                 * Para cada OrderItem da lista de itens deste pedido,
                 * obtém apenas o valor do seu subtotal.
                 */
                .map(OrderItem::getSubtotal)

                /*
                 * Soma todos os subtotais começando pelo valor zero.
                 *
                 * BigDecimal.ZERO -> valor inicial da soma.
                 * BigDecimal::add -> adiciona cada subtotal ao acumulador.
                 */
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        /*
         * O reduce() pertence à Stream API.
         *
         * Ele reduz todos os valores do Stream em um único resultado.
         *
         * Neste caso:
         *
         * - BigDecimal.ZERO -> define o valor inicial da soma.
         *
         * - BigDecimal::add -> é uma referência ao método add()
         *   da classe BigDecimal, responsável por somar dois
         *   valores do tipo BigDecimal.
         *
         * O método add() retorna um novo objeto BigDecimal
         * com o resultado da soma, pois BigDecimal é imutável.
         *
         * Exemplo:
         *
         * 0.00 + 99.90 + 119.80 = 219.70
         */

    }


    // =========================Usando LAMBDA ==============================
    /*
     * Calcula o total do pedido usando Stream e Lambda.
     *
     * Percorre os OrderItem, transforma cada item em seu subtotal
     * e soma todos os valores para atualizar o totalAmount.
     */
    public void calculateTotalComLambda() {

        this.totalAmount = this.items.stream()
                .map(item -> item.getSubtotal())
                .reduce(BigDecimal.ZERO, (total, subtotal) -> total.add(subtotal));

    }

    // =================================== COM MÉTODO REFERENCE  ============================
    /*
     * Calcula o total do pedido usando Stream API e Method Reference.
     *
     * Obtém o subtotal de cada OrderItem e soma todos os valores
     * para atualizar o totalAmount.
     */
    public void calculateTotalComMethodReference() {

        this.totalAmount = this.items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    /**
     * Única porta de entrada para mudar o status do pedido.
     * Valida a transição usando a regra centralizada no enum OrderStatus
     * e lança exceção de domínio se ela for inválida.
     */
    public void changeStatus(OrderStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("O novo status não pode ser nulo.");
        }
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidOrderStatusTransitionException(this.status, newStatus);
        }
        this.status = newStatus;
    }

    // Em Order
    public void updateItemQuantity(OrderItem item, Integer quantity) {
        if (item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        this.assertItemsEditable();

        if (!this.items.contains(item)) {
            throw new IllegalArgumentException("O item não pertence a este pedido.");
        }

        item.changeQuantity(quantity);
        this.calculateTotalComLambda();
    }

    // Regra: itens só mudam em pedido PENDING (depois de pago, o total é fixo).
    private void assertItemsEditable() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Os itens só podem ser alterados em pedidos PENDING. Status atual: " + this.status);
        }
    }

    // ------------------------------------------------------------------
    // Getters e Setters
    // ------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
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

    // Sem setStatus público de propósito: use changeStatus(), que valida a transição.

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    // Sem setTotalAmount público de propósito: o total só muda via calculateTotal(),
    // para ninguém gravar um valor que não bate com os itens.

    /**
     * Devolve uma visão SOMENTE LEITURA da lista de itens.
     * Para alterar, use addItem/removeItem (que mantêm o total e a relação consistentes).
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ------------------------------------------------------------------
    // equals e hashCode
    // ------------------------------------------------------------------

    // instanceof (e não getClass()) porque o Hibernate pode entregar um PROXY
    // desta classe em relações LAZY; com getClass() o mesmo pedido seria "diferente".
    // Entidade sem id (ainda não salva) só é igual a si mesma.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order other = (Order) o;
        return this.id != null && this.id.equals(other.getId());
    }

    // Constante de propósito: o id é nulo antes do save e muda depois dele.
    // Um hashCode baseado no id corromperia o objeto dentro de HashSet/HashMap.
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}