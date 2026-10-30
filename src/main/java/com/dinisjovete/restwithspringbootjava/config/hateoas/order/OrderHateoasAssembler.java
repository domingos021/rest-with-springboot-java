package com.dinisjovete.restwithspringbootjava.config.hateoas.order;

import com.dinisjovete.restwithspringbootjava.config.hateoas.order_item.OrderItemHateoasAssembler;
import com.dinisjovete.restwithspringbootjava.controllers.order.OrderController;
import com.dinisjovete.restwithspringbootjava.controllers.order_item.OrderItemController;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderDTO;


import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/*
 * ============================================================================
 * HATEOAS - OrderHateoasAssembler
 * ============================================================================
 *
 * Concentra a montagem dos links HATEOAS do OrderDTO num único lugar, para o
 * Controller não repetir esse código em cada método.
 *
 * Links adicionados ao pedido:
 *
 *      self    -> o próprio pedido           (GET /order/{id})
 *      orders  -> a coleção de pedidos       (GET /order)
 *      items   -> os itens deste pedido      (GET /order/{id}/item)
 *
 * Cada item da lista "items" também recebe os seus próprios links, montados
 * pelo OrderItemHateoasAssembler.
 *
 * Os links são construídos a partir dos métodos do Controller (methodOn), então
 * se uma URL mudar no Controller, os links acompanham automaticamente.
 * ============================================================================
 */
@Component
public class OrderHateoasAssembler {

    private final OrderItemHateoasAssembler itemAssembler;

    public OrderHateoasAssembler(OrderItemHateoasAssembler itemAssembler) {
        this.itemAssembler = itemAssembler;
    }

    /**
     * Adiciona os links HATEOAS ao pedido (e aos seus itens) e devolve o mesmo DTO.
     *
     * @param dto Pedido que receberá os links
     * @return O próprio DTO, já enriquecido com links
     */
    public OrderDTO addLinks(OrderDTO dto) {

        dto.add(linkTo(methodOn(OrderController.class).findById(dto.getId())).withSelfRel());

        dto.add(linkTo(methodOn(OrderController.class).findAll()).withRel("orders"));

        dto.add(linkTo(methodOn(OrderItemController.class).findAll(dto.getId())).withRel("items"));

        // Cada item do pedido também recebe os seus links.
        List<OrderItemDTO> items = dto.getItems();

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                itemAssembler.addLinks(items.get(i), dto.getId());
            }
        }

        return dto;
    }
}
