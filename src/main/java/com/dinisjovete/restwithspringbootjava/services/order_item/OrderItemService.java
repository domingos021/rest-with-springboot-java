package com.dinisjovete.restwithspringbootjava.services.order_item;


import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.Order;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderItem;
import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.order_item.OrderItemMapper_MapStruct;
import com.dinisjovete.restwithspringbootjava.repositories.order.OrderRepository;
import com.dinisjovete.restwithspringbootjava.repositories.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class OrderItemService {


    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class.getName());

    /*
     * ============================================================================
     * O PEDIDO É A RAIZ DO AGREGADO
     * ============================================================================
     *
     * Um OrderItem existe somente dentro de um Order. Por isso este Service NÃO
     * possui OrderItemRepository: todo acesso passa pelo OrderRepository.
     *
     * Vantagens:
     * - Todo item é sempre buscado DENTRO de um pedido. Um item de outro pedido
     *   simplesmente não é encontrado (HTTP 404), sem risco de alterar o item errado.
     * - As regras do pedido (status PENDING, total recalculado) são aplicadas pelos
     *   métodos da entidade Order (addItem, updateItemQuantity, removeItem).
     * - O cascade ALL + orphanRemoval da entidade Order cuida de inserir e apagar
     *   os itens no banco.
     * ============================================================================
     */
    private final OrderRepository orderRepository;

    /*
     * O item possui um produto, que é uma entidade Product.
     *
     * Ao adicionar um item, o cliente envia apenas o productId, e é este repositório
     * que busca o Product correspondente no banco de dados.
     */
    private final ProductRepository productRepository;

    // Mapper ativo: MapStruct (OrderItemMapper_MapStruct)
    private final OrderItemMapper_MapStruct mapper;


    @Autowired   // injetando as dependências no construtor
    public OrderItemService(
            OrderItemMapper_MapStruct mapper,
            OrderRepository orderRepository,
            ProductRepository productRepository
    ) {
        this.mapper = mapper;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /*
     * ============================================================================
     * MÉTODOS AUXILIARES (privados)
     * ============================================================================
     */

    // Busca o pedido pelo ID. Se não encontrar, lança ResourceNotFoundException (HTTP 404).
    private Order findOrderOrThrow(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this ID!"));
    }

    /*
     * Procura o item DENTRO da lista do pedido.
     *
     * Se o item não existir ou pertencer a outro pedido, o resultado é o mesmo:
     * ResourceNotFoundException (HTTP 404).
     */
    private OrderItem findItemInOrder(Order order, Long itemId) {

        List<OrderItem> items = order.getItems();

        for (int i = 0; i < items.size(); i++) {

            OrderItem item = items.get(i);

            if (item.getId() != null && item.getId().equals(itemId)) {
                return item;
            }
        }

        throw new ResourceNotFoundException("Item not found in this order!");
    }

    /*
     * ============================================================================
     * SOBRE @Transactional
     * ============================================================================
     * Os itens e o produto são LAZY. A conversão para DTO acessa product.id, então
     * precisa ocorrer com a sessão aberta.
     *
     * Nas escritas, a transação garante "tudo ou nada": o item e o novo total do
     * pedido são gravados juntos, ou nenhum dos dois.
     * ============================================================================
     */

    //método que retorna a lista de itens de um pedido
    @Transactional(readOnly = true)
    public List<OrderItemDTO> findAll(Long orderId) {

        logger.info("Finding all items of one order!");

        Order order = findOrderOrThrow(orderId);

        return mapper.mapEntityListToDtoList(order.getItems());
    }

    @Transactional(readOnly = true)
    public OrderItemDTO findById(Long orderId, Long itemId) {

        logger.info("Finding one order item!");

        Order order = findOrderOrThrow(orderId);

        OrderItem item = findItemInOrder(order, itemId);

        return mapper.mapEntityToDto(item);
    }

    /*
     * ============================================================================
     * CREATE - ADIÇÃO DE UM ITEM AO PEDIDO
     * ============================================================================
     *
     * O cliente envia o ID do pedido (pela URL) e o OrderItemInsertDTO
     * (productId e quantity).
     *
     * O Service resolve o que NÃO vem do cliente:
     * - Busca o Order  (404 se não existir).
     * - Busca o Product (404 se não existir).
     * - Lê o preço ATUAL do Product. Esse preço é congelado no item (unitPrice).
     *   O cliente nunca informa o preço: isso evitaria adulteração de valores.
     *
     * Depois, order.addItem(item) aplica as regras do pedido:
     * - Só funciona se o pedido estiver PENDING (senão, IllegalStateException).
     * - Sincroniza os dois lados da relação (item.order e order.items).
     * - Recalcula o total do pedido.
     *
     * Por que saveAndFlush no pedido e não um save do item?
     * O item é salvo por cascade a partir do Order. O flush força o INSERT agora,
     * garantindo que o item já tenha id e createdAt quando for convertido em DTO.
     *
     * Fluxo:
     *
     * Cliente
     *    |
     *    ▼
     * Controller
     *    |
     *    ▼
     * OrderItemInsertDTO
     *    |
     *    ▼ (Busca Order e Product)
     * Order + Product
     *    |
     *    ▼
     * OrderItemMapper.mapDtoToEntity(dto, product, preço atual)
     *    |
     *    ▼
     * OrderItem (subtotal calculado)
     *    |
     *    ▼
     * order.addItem(item)  -> valida PENDING e recalcula o total
     *    |
     *    ▼
     * orderRepository.saveAndFlush(order)  (INSERT do item + UPDATE do total)
     *    |
     *    ▼
     * OrderItemMapper.mapEntityToDto()
     *    |
     *    ▼
     * OrderItemDTO
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     * ============================================================================
     */
    @Transactional
    public OrderItemDTO create(Long orderId, OrderItemInsertDTO dto) {

        logger.info("Adding a new item to an order!");

        Order order = findOrderOrThrow(orderId);

        // Busca o produto pelo productId informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for this ID!"));

        // Monta o item com o preço ATUAL do produto (que será congelado no item).
        OrderItem item = mapper.mapDtoToEntity(dto, product, product.getPrice());

        // Regra de negócio do pedido: valida PENDING, vincula o item e recalcula o total.
        order.addItem(item);

        orderRepository.saveAndFlush(order);

        return mapper.mapEntityToDto(item);
    }

    /*
     * ============================================================================
     * UPDATE - ATUALIZAÇÃO DA QUANTIDADE DE UM ITEM
     * ============================================================================
     *
     * O cliente envia o ID do pedido e o ID do item (pela URL) e a nova
     * quantidade (OrderItemUpdateDTO).
     *
     * Não há updateEntityFromDTO no mapper: a alteração passa por
     * Order.updateItemQuantity(), que:
     * - exige pedido PENDING;
     * - confere que o item pertence ao pedido;
     * - recalcula o subtotal do item e o total do pedido.
     *
     * Fluxo:
     *
     * Controller
     *    |
     *    ▼
     * OrderItemService.update()
     *    |
     *    ▼
     * findOrderOrThrow() + findItemInOrder()  -> 404 se não existir
     *    |
     *    ▼
     * order.updateItemQuantity(item, quantity)
     *    |
     *    ▼
     * orderRepository.save()  (UPDATE do item e do total)
     *    |
     *    ▼
     * OrderItemMapper.mapEntityToDto()
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     * ============================================================================
     */
    @Transactional
    public OrderItemDTO update(Long orderId, Long itemId, OrderItemUpdateDTO dto) {

        logger.info("Updating one order item!");

        Order order = findOrderOrThrow(orderId);

        OrderItem item = findItemInOrder(order, itemId);

        order.updateItemQuantity(item, dto.getQuantity());

        orderRepository.save(order);

        return mapper.mapEntityToDto(item);
    }

    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Remove um item do pedido.
     *
     * Order.removeItem() exige pedido PENDING, desvincula o item e recalcula o
     * total. Graças ao orphanRemoval, o item é apagado do banco.
     *
     * O produto NÃO é removido (ciclo de vida independente).
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * OrderItemService.delete()
     *      |
     *      ↓ (Verifica se o pedido e o item existem)
     * order.removeItem(item)
     *      |
     *      ↓
     * orderRepository.save()
     *      |
     *      ↓
     * Remove de order_items e atualiza o total em orders (DELETE + UPDATE)
     *
     * VOID -> não retorna conteúdo
     * ============================================================================
     */
    @Transactional
    public void delete(Long orderId, Long itemId) {

        logger.info("Deleting one order item!");

        Order order = findOrderOrThrow(orderId);

        OrderItem item = findItemInOrder(order, itemId);

        order.removeItem(item);

        orderRepository.save(order);

        // não retorna nada ao cliente
    }
}

    /*
    Controller
        |
        | recebe requisição HTTP
        | valida entrada básica (@Valid)
        |
        ▼
    Service
        |
        | regra de negócio (transação, relacionamentos)
        | coordena operações
        |
        ├── Entidade Order (raiz do agregado)
        |       |
        |       └── addItem / updateItemQuantity / removeItem
        |
        ├── Mapper
        |       |
        |       └── Entity -> DTO
        |
        └── Repository (Order e Product)
                |
                └── Banco de Dados
     */
