package com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.order_item;


/*
 * =====================================================================
 * TÍTULO DA CLASSE: OrderItemMapper_MapStruct
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Interface de mapeamento baseada em MapStruct. Responsável por converter
 * objetos da entidade `OrderItem` nos DTOs de item de pedido
 * (`OrderItemDTO` e `OrderItemInsertDTO`), utilizando o container do Spring.
 *
 * Diferente do mapper de Book, aqui NÃO existe mapeamento automático de
 * DTO para entidade, nem update in-place automático. Motivo: OrderItem
 * protege suas regras (preço congelado, subtotal calculado, itens só mudam
 * em pedido PENDING) e não possui setters para quantity, unitPrice e discount.
 * Um mapeamento automático criaria um item inválido (sem produto e sem
 * subtotal) ou simplesmente ignoraria os campos sem avisar.
 * =====================================================================
 */


import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemInsertDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderItem;
import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)// Diz ao Spring para gerenciar esta interface como um Bean
public interface OrderItemMapper_MapStruct {

    // Com o componentModel = "spring", removemos o INSTANCE.
    // O Spring injetará este mapper automaticamente onde você precisar.

    /*
     * mapEntityToDto -> retorna um objeto do tipo OrderItemDTO
     * Mapeia uma instância da entidade OrderItem para o DTO OrderItemDTO.
     *
     * O produto é um objeto (Product) na entidade, mas o DTO expõe apenas o
     * identificador. Por isso o @Mapping lê "product.id" e grava em "productId"
     * (que no JSON vira "product_id").
     *
     * Os demais campos (id, quantity, unitPrice, discount, subtotal, createdAt)
     * possuem o mesmo nome na entidade e no DTO e são mapeados automaticamente.
     */
    // Mapeamento de Objeto Único: Entity ➔ DTO
    @Mapping(source = "product.id", target = "productId")
    OrderItemDTO mapEntityToDto(OrderItem orderItem);

    /*
     * mapEntityListToDtoList -> retorna uma lista de objetos do tipo OrderItemDTO
     * Converte uma coleção de entidades (List<OrderItem>) em uma lista de DTOs
     * (List<OrderItemDTO>), aplicando o mapeamento acima em cada elemento.
     * Também é usado pelo OrderMapper_MapStruct para preencher a lista "items"
     * do OrderDTO.
     */
    // Mapeamento de Listas: Entity List ➔ DTO List
    List<OrderItemDTO> mapEntityListToDtoList(List<OrderItem> orderItemList);

    /*
     * ============================================================================
     * CRIAÇÃO DE ENTIDADE A PARTIR DO DTO DE INSERT
     * ============================================================================
     */
    /*
     * mapDtoToEntity -> retorna uma entidade do tipo OrderItem
     *
     * O OrderItemInsertDTO traz apenas productId e quantity. Os outros dados
     * necessários para montar o item NÃO vêm do cliente e são resolvidos pelo
     * Service, que os entrega a este método:
     *
     * - product   -> entidade carregada do banco pelo productId (404 se não existir).
     * - unitPrice -> preço ATUAL do produto, congelado no item no momento da compra.
     *                Nunca é lido do corpo da requisição (evita adulteração de preço).
     *
     * Usa o construtor da entidade, que já calcula o subtotal. O vínculo com o
     * pedido NÃO é feito aqui: quem associa o item ao pedido (e recalcula o total)
     * é Order.addItem(item).
     *
     * Método default (implementado manualmente): o MapStruct não gera código
     * para ele, pois a entidade não possui setters para esses campos.
     */
    // Criação: DTO ➔ Entity
    default OrderItem mapDtoToEntity(OrderItemInsertDTO dto, Product product, BigDecimal unitPrice) {

        Objects.requireNonNull(dto, "O DTO do item não pode ser nulo.");
        Objects.requireNonNull(product, "O produto não pode ser nulo.");
        Objects.requireNonNull(unitPrice, "O preço unitário não pode ser nulo.");

        return new OrderItem(product, dto.getQuantity(), unitPrice);
    }

    /*
     * ATUALIZAÇÃO (OrderItemUpdateDTO)
     *
     * Propositalmente NÃO existe updateEntityFromDTO neste mapper.
     *
     * Alterar a quantidade de um item exige regras que pertencem ao pedido:
     * o pedido precisa estar PENDING, o item precisa pertencer a ele e o total
     * do pedido precisa ser recalculado. Por isso o Service chama o método de
     * negócio da entidade Order (ex.: order.updateItemQuantity(item, quantity)),
     * e não um mapeamento direto de campos.
     */
}
