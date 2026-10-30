package com.dinisjovete.restwithspringbootjava.repositories.product;
import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/*
 * public interface JpaRepository<T, ID>
 *
 * JpaRepository -> interface nativa do Spring Data JPA que recebe:
 *
 * T  -> a entidade que será gerenciada pelo repositório.
 *      Neste caso, Product.
 *
 * ID -> o tipo da chave primária da entidade.
 *      Neste caso, Long.
 *
 * Mesmo Product sendo uma classe abstrata, o JPA consegue trabalhar com ela
 * porque existe uma hierarquia de herança:
 *
 *              Product
 *                  |
 *        --------------------
 *        |                  |
 *      Book               Movie
 *
 * Na estratégia JOINED, cada classe da hierarquia tem a sua própria tabela
 * (products, books, movies...), ligadas pelo mesmo id.
 *
 * O Hibernate descobre qual classe concreta deve instanciar verificando, por
 * meio de JOINs, em qual tabela filha existe uma linha com aquele id.
 * (A coluna discriminadora é opcional na estratégia JOINED; ela é a base da
 * estratégia SINGLE_TABLE.)
 *
 * Esse repositório é útil para operações genéricas de produto, como:
 *
 * - buscar qualquer tipo de produto por id;
 * - listar todos os produtos;
 * - usar produtos dentro de outras entidades, como OrderItem.
 *
 * Exemplo:
 *
 * OrderItem
 *      |
 *      | N..1
 *      |
 *   ProductRepository
 *
 * O pedido não precisa saber se o produto é Book, Movie ou Game.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
     * O Spring Data JPA cria automaticamente a consulta:
     *
     * SELECT *
     * FROM products
     * WHERE title = ?
     *
     * O campo "title" pertence à entidade Product.
     *
     * Como Product é a classe pai da hierarquia:
     *
     *              Product
     *                  |
     *        --------------------
     *        |                  |
     *      Book               Movie
     *
     * O retorno pode ser qualquer implementação concreta:
     *
     * - Book
     * - Movie
     *
     * dependendo do registro encontrado.
     */
    Optional<Product> findByTitle(String title);

    /*
     * O Spring Data JPA cria automaticamente a consulta:
     *
     * SELECT *
     * FROM products
     * WHERE title = ?
     *
     * O campo title pertence à entidade Product e é herdado
     * pelas entidades filhas.
     */
    boolean existsByTitle(String title);

}
/*
Cliente adiciona um item ao pedido (POST /order/{orderId}/item)
        |
        v
OrderItemController
        |
        v
OrderItemService
        |
        |
        +---- ProductRepository
        |          |
        |          v
        |       Busca produto
        |
        v
Cria OrderItem (preço atual do produto congelado no item)
        |
        v
order.addItem(item)  -> valida PENDING e recalcula o total



exe:@Service
public class OrderItemService {


    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemMapper_MapStruct mapper;


    public OrderItemService(
            OrderItemMapper_MapStruct mapper,
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.mapper = mapper;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @Transactional
    public OrderItemDTO create(Long orderId, OrderItemInsertDTO dto) {


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found for this ID!"
                        )
                );


        // Busca qualquer tipo de produto
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found for this ID!"
                        )
                );


        // A entidade OrderItem não tem setters: o item nasce pelo construtor,
        // chamado dentro do mapper, com o preço ATUAL do produto (congelado no item).
        OrderItem item = mapper.mapDtoToEntity(dto, product, product.getPrice());


        // Regra do pedido: valida PENDING, vincula o item e recalcula o total.
        order.addItem(item);

        orderRepository.saveAndFlush(order);


        return mapper.mapEntityToDto(item);
    }
}

             Order
              |
              | 1
              |
              | 0..N
          OrderItem
              |
              | N
              |
              | 1
           Product
 */

/*
 * O ProductRepository representa o acesso aos dados da entidade base Product.
 *
 * Em um sistema de e-commerce, o pedido (Order) e seus itens (OrderItem)
 * não precisam conhecer qual é o tipo concreto do produto.
 *
 * Um OrderItem apenas guarda uma referência para um Product através do id.
 *
 * Exemplo:
 *
 * OrderItem
 *     |
 *     | product_id
 *     ↓
 * Product
 *     |
 *     ├── Book
 *     ├── Movie
 *     └── Game
 *
 *
 * Quando o sistema busca um produto pelo id, o Hibernate identifica
 * automaticamente qual classe concreta deve ser carregada devido ao
 * mecanismo de herança JPA JOINED.
 *
 * Exemplo:
 *
 * productRepository.findById(1L);
 *
 * Retorno:
 *
 * - Se o id pertence a um Book → retorna um objeto Book
 * - Se o id pertence a um Movie → retorna um objeto Movie
 *
 *
 * Dessa forma, a camada de pedido trabalha apenas com Product,
 * seguindo o princípio de polimorfismo, sem precisar criar regras
 * específicas para cada tipo de produto.
 */