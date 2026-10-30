package com.dinisjovete.restwithspringbootjava.config.hateoas.order_item;

import com.dinisjovete.restwithspringbootjava.controllers.order.OrderController;
import com.dinisjovete.restwithspringbootjava.controllers.order_item.OrderItemController;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/*
 * ============================================================================
 * HATEOAS - OrderItemHateoasAssembler
 * ============================================================================
 *
 * Concentra a montagem dos links HATEOAS do OrderItemDTO num único lugar.
 *
 * O OrderItemDTO não carrega o ID do pedido (ele já está na URL), mas os links
 * precisam dele. Por isso o ID do pedido é recebido como parâmetro.
 *
 * Links adicionados ao item:
 *
 *      self   -> o próprio item              (GET /order/{orderId}/item/{id})
 *      items  -> os itens do mesmo pedido    (GET /order/{orderId}/item)
 *      order  -> o pedido dono do item       (GET /order/{orderId})
 *
 * Também é usado pelo OrderHateoasAssembler para os itens aninhados no pedido.
 * ============================================================================
 */
@Component
public class OrderItemHateoasAssembler {

    /**
     * Adiciona os links HATEOAS ao item e devolve o mesmo DTO.
     *
     * @param dto     Item que receberá os links
     * @param orderId ID do pedido dono do item
     * @return O próprio DTO, já enriquecido com links
     */
    public OrderItemDTO addLinks(OrderItemDTO dto, Long orderId) {

        dto.add(linkTo(methodOn(OrderItemController.class).findById(orderId, dto.getId())).withSelfRel());

        dto.add(linkTo(methodOn(OrderItemController.class).findAll(orderId)).withRel("items"));

        dto.add(linkTo(methodOn(OrderController.class).findById(orderId)).withRel("order"));

        return dto;
    }
}
