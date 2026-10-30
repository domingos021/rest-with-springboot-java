package com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.order;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: OrderMapper_MapStruct
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Interface de mapeamento baseada em MapStruct. Responsável por converter
 * objetos da entidade `Order` nos DTOs de pedido (`OrderDTO` e
 * `OrderUpdateDTO`), tanto para instâncias únicas quanto para listas,
 * utilizando o container do Spring.
 *
 * Diferente do mapper de Book, aqui NÃO existe mapeamento automático de
 * DTO para entidade. Motivo: Order protege suas regras (status só muda por
 * changeStatus(), total só muda por calculateTotal(), itens só por
 * addItem/removeItem) e não possui setters para esses campos. Um mapeamento
 * automático criaria um pedido inválido (sem cliente e sem status) ou
 * ignoraria os campos sem avisar.
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.Order;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.order_item.OrderItemMapper_MapStruct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

// uses -> o MapStruct reaproveita o OrderItemMapper_MapStruct para converter
// a lista de itens (List<OrderItem> ➔ List<OrderItemDTO>) dentro do pedido.
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = OrderItemMapper_MapStruct.class)// Diz ao Spring para gerenciar esta interface como um Bean
public interface OrderMapper_MapStruct {

    // Com o componentModel = "spring", removemos o INSTANCE.
    // O Spring injetará este mapper automaticamente onde você precisar.

    /*
     * mapEntityToDto -> retorna um objeto do tipo OrderDTO
     * Mapeia uma instância da entidade Order para o DTO OrderDTO.
     *
     * O cliente é um objeto (Person) na entidade, mas o DTO expõe apenas o
     * identificador. Por isso o @Mapping lê "customer.id" e grava em "customerId"
     * (que no JSON vira "customer_id"), sem expor dados da pessoa.
     *
     * A lista "items" é convertida pelo OrderItemMapper_MapStruct.
     *
     * ATENÇÃO: customer e items são LAZY. Este método deve ser chamado dentro
     * de uma transação (@Transactional no Service), senão ocorre
     * LazyInitializationException.
     */
    // Mapeamento de Objeto Único: Entity ➔ DTO
    @Mapping(source = "customer.id", target = "customerId")
    OrderDTO mapEntityToDto(Order order);

    /*
     * mapEntityListToDtoList -> retorna uma lista de objetos do tipo OrderDTO
     * Converte uma coleção de entidades (List<Order>) em uma lista de contratos
     * de transferência (List<OrderDTO>), aplicando o mapeamento acima em cada
     * elemento.
     */
    // Mapeamento de Listas: Entity List ➔ DTO List
    List<OrderDTO> mapEntityListToDtoList(List<Order> orderList);

    /*
     * CRIAÇÃO (OrderInsertDTO)
     *
     * Propositalmente NÃO existe mapDtoToEntity neste mapper.
     *
     * O OrderInsertDTO traz apenas customerId, e a entidade Order exige um
     * objeto Person. O Service carrega a Person pelo id (404 se não existir)
     * e cria o pedido pelo construtor da entidade:
     *
     *     Order order = new Order(customer);
     *
     * O construtor já define o status PENDING e a data do pedido.
     */

    /*
     * ============================================================================
     * ATUALIZAÇÃO DE ENTIDADE EXISTENTE (UPDATE IN-PLACE)
     * ============================================================================
     */
    /*
     * updateEntityFromDTO -> Atualiza uma entidade existente gerenciada pelo Hibernate
     *
     * Mantém o mesmo nome e a mesma assinatura do mapper de Book, mas NÃO usa
     * mapeamento automático de campos: a entidade Order não tem setStatus de
     * propósito, e o MapStruct ignoraria o campo em silêncio.
     *
     * A atualização passa por Order.changeStatus(), que valida a transição
     * usando a regra do enum OrderStatus e lança
     * InvalidOrderStatusTransitionException se ela for inválida (a camada de
     * exceções deve traduzir para HTTP 409 - Conflict).
     *
     * Atualiza somente o status; id, customer, orderDate, totalAmount e items
     * permanecem intactos por segurança.
     */
    default void updateEntityFromDTO(OrderUpdateDTO dto, @MappingTarget Order entity) {

        entity.changeStatus(dto.getStatus());
    }
}
