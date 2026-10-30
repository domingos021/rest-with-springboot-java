package com.dinisjovete.restwithspringbootjava.repositories.order;

import com.dinisjovete.restwithspringbootjava.data_model.entities.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/*
 * public interface JpaRepository<T, ID>
 * JpaRepository -> essa interface nativa espera receber como parâmetro
 * um tipo no caso(Order), e um Id do tipo Long, que é o tipo do id da entidade Order.
 * A interface JpaRepository fornece métodos prontos para realizar operações de CRUD (Create, Read, etc).
 *
 * Este é o ÚNICO repositório do agregado de pedidos: o OrderItem não possui
 * repositório próprio porque só existe dentro de um Order (cascade ALL + orphanRemoval).
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /*
     * ============================================================================
     * @EntityGraph -> CARREGA OS ITENS JUNTO COM O PEDIDO
     * ============================================================================
     * A lista items é LAZY: sem isso, o Hibernate faz uma consulta para os pedidos
     * e mais UMA consulta por pedido para buscar os itens (problema N+1).
     *
     * Com attributePaths = "items", os itens vêm na mesma consulta (JOIN FETCH).
     * O cliente (customer) e o produto (product) continuam LAZY: o mapper só lê
     * o id deles, que o Hibernate já conhece sem precisar ir ao banco.
     *
     * ATENÇÃO: ao adicionar paginação (Pageable) a findAll, NÃO combine com
     * este fetch de coleção: o Hibernate pagina em memória. Nesse caso, remova o
     * @EntityGraph e use hibernate.default_batch_fetch_size.
     * ============================================================================
     */
    @Override
    @EntityGraph(attributePaths = "items")
    Optional<Order> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "items")
    List<Order> findAll();

    // Usado para proteger a exclusão de uma pessoa (PersonService.delete):
    // quem já tem pedidos não pode ser apagado, senão a FK customer_id quebra o DELETE.
    // O Spring Data gera a consulta pelo nome do método (caminho "customer.id").
    boolean existsByCustomerId(Long customerId);

    // Usado para proteger a exclusão de um produto (BookService.delete e afins):
    // produto que já foi vendido não pode ser apagado, senão a FK product_id em
    // order_items quebra o DELETE. Caminho percorrido: items -> product -> id.
    boolean existsByItemsProductId(Long productId);

}
