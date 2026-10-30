package com.dinisjovete.restwithspringbootjava.controllers.order_item;

import com.dinisjovete.restwithspringbootjava.config.hateoas.order_item.OrderItemHateoasAssembler;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.controllers.order.OrderController;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.order_item.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/*
 * ============================================================================
 * CONTROLLER - OrderItemController
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java para os itens de um pedido. Recebe a requisição,
 * valida o formato da entrada, delega ao Service e devolve a resposta com o
 * status HTTP correto e os links HATEOAS.
 *
 * O item é um recurso ANINHADO no pedido: toda URL carrega o ID do pedido.
 * Isso reflete a regra do domínio (um OrderItem só existe dentro de um Order)
 * e impede acessar um item sem informar a qual pedido ele pertence.
 *
 * O Controller NÃO contém regra de negócio: ela mora no OrderItemService e na
 * entidade Order (itens só mudam em pedido PENDING).
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * OrderItemControllerDocs, que esta classe implementa (inclusive o @Tag).
 * ============================================================================
 */
@RestController
/*
 * ============================================================================
 * @RequestMapping
 * ============================================================================
 * Todas as requisições para este Controller começarão com:
 *
 *      http://localhost:8080/order/{orderId}/item
 *
 * O {orderId} é um Path Parameter presente em TODOS os métodos desta classe.
 * ============================================================================
 */

/*
 * /order
 * -> Recurso principal da API.
 * -> Representa a entidade Order (pedido).
 *
 * /1
 * -> Valor do Path Variable {orderId}.
 * -> É o ID do pedido que já existe no banco.
 * -> Exemplo: Order com id = 1.
 *
 * /item
 * -> Sub-recurso relacionado ao Order.
 * -> Representa os itens pertencentes ao pedido.
 * -> É a entidade OrderItem.
 *
 * Tradução da URL:
 *
 * "Acesse os itens (OrderItem) pertencentes ao pedido (Order)
 * de id 1."
 */
@RequestMapping("/order/{orderId}/item")
public class OrderItemController implements OrderItemControllerDocs { // documentação Swagger na interface

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    private final OrderItemService service;
    private final OrderItemHateoasAssembler assembler; // adiciona os links HATEOAS aos DTOs

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    // =========================================================================
    public OrderItemController(OrderItemService orderItemService, OrderItemHateoasAssembler orderItemHateoasAssembler) {
        this.service = orderItemService;
        this.assembler = orderItemHateoasAssembler;
    }

    /*
     * ============================================================================
     * GET /order/{orderId}/item/{id}  ->  BUSCAR UM ITEM DO PEDIDO
     * ============================================================================
     *
     *      GET http://localhost:8080/order/1/item/10
     *
     * Respostas:
     *
     *      200 OK          item encontrado (corpo: OrderItemDTO com links)
     *      404 Not Found   pedido inexistente, ou item que não pertence a este pedido
     *      400 Bad Request ID que não é número
     * ============================================================================
     */
    // @Override: implementa findById(Long, Long) de OrderItemControllerDocs.
    @Override
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public OrderItemDTO findById(
            @PathVariable("orderId") Long orderId,
            @PathVariable("id") Long id
    ) {
        OrderItemDTO item = service.findById(orderId, id);

        // O DTO volta enriquecido com os links HATEOAS (self, items, order).
        return assembler.addLinks(item, orderId);
    }


    /*
     * ============================================================================
     * GET /order/{orderId}/item  ->  LISTAR OS ITENS DE UM PEDIDO (com HATEOAS)
     * ============================================================================
     *
     * Respostas:
     *
     *      200 OK          sempre que o pedido existir (pedido sem itens -> coleção vazia)
     *      404 Not Found   pedido inexistente
     * ============================================================================
     */
    // @Override: implementa findAll(Long) de OrderItemControllerDocs.
    @Override
    @GetMapping(
            value = "",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public CollectionModel<OrderItemDTO> findAll(@PathVariable("orderId") Long orderId) {

        // 1º Passo: busca os itens do pedido via Service.
        List<OrderItemDTO> items = service.findAll(orderId);

        // 2º Passo: adiciona os links HATEOAS individuais em cada item.
        items.forEach(item -> assembler.addLinks(item, orderId));

        // 3º Passo: empacota em um CollectionModel com o link da coleção e o link do pedido dono.
        return CollectionModel.of(items,
                linkTo(
                        methodOn(OrderItemController.class)
                                .findAll(orderId)
                )
                        .withSelfRel(), // link "self" da coleção
                linkTo(
                        methodOn(OrderController.class)
                                .findById(orderId)
                )
                        .withRel("order") // link para o pedido dono dos itens
        );
    }


    /*
     * ============================================================================
     * POST /order/{orderId}/item  ->  ADICIONAR UM ITEM AO PEDIDO
     * ============================================================================
     *
     * Recebe um OrderItemInsertDTO no corpo da requisição, valida com @Valid e
     * devolve o OrderItemDTO criado com o status 201 (Created).
     *
     * O preço NÃO vem do cliente: o sistema usa o preço atual do produto e o
     * congela no item. O total do pedido é recalculado automaticamente.
     *
     * Respostas:
     *
     *      201 Created     item adicionado (corpo: OrderItemDTO com links)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      404 Not Found   pedido ou produto inexistente
     *      409 Conflict    pedido que não está PENDING
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/order/1/item)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "product_id": 3,
     *        "quantity": 2
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/order/1/item)
     * ----------------------------------------------------------------------------
     *
     *      <OrderItemInsertDTO>
     *        <product_id>3</product_id>
     *        <quantity>2</quantity>
     *      </OrderItemInsertDTO>
     * ============================================================================
     */
    // @Override: implementa create(Long, OrderItemInsertDTO) de OrderItemControllerDocs.
    @Override
    @PostMapping(
            value = "",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public ResponseEntity<OrderItemDTO> create(
            @PathVariable("orderId") Long orderId,
            @Valid @RequestBody OrderItemInsertDTO itemInsert
    ) {
        OrderItemDTO createdItem = service.create(orderId, itemInsert);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.addLinks(createdItem, orderId)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /order/{orderId}/item/{id}  ->  ATUALIZAR A QUANTIDADE DE UM ITEM
     * ============================================================================
     *
     *      PUT http://localhost:8080/order/1/item/10
     *
     * Respostas:
     *
     *      200 OK          quantidade atualizada (corpo: OrderItemDTO com links)
     *      400 Bad Request corpo malformado, inválido ou ID que não é número
     *      404 Not Found   pedido inexistente, ou item que não pertence a este pedido
     *      409 Conflict    pedido que não está PENDING
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/order/1/item/10)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "quantity": 5
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/order/1/item/10)
     * ----------------------------------------------------------------------------
     *
     *      <OrderItemUpdateDTO>
     *        <quantity>5</quantity>
     *      </OrderItemUpdateDTO>
     * ============================================================================
     */
    // @Override: implementa update(Long, Long, OrderItemUpdateDTO) de OrderItemControllerDocs.
    @Override
    @PutMapping(
            value = "/{id}",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public OrderItemDTO update(
            @PathVariable("orderId") Long orderId,
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderItemUpdateDTO itemUpdate
    ) {
        return assembler.addLinks(
                service.update(orderId, id, itemUpdate),
                orderId
        );
    }


    /*
     * ============================================================================
     * DELETE /order/{orderId}/item/{id}  ->  REMOVER UM ITEM DO PEDIDO
     * ============================================================================
     *
     *      DELETE http://localhost:8080/order/1/item/10
     *
     * O total do pedido é recalculado automaticamente.
     *
     * Respostas:
     *
     *      204 No Content  removido com sucesso (sem corpo na resposta)
     *      404 Not Found   pedido inexistente, ou item que não pertence a este pedido
     *      409 Conflict    pedido que não está PENDING
     *      400 Bad Request ID que não é número
     * ============================================================================
     */
    // @Override: implementa delete(Long, Long) de OrderItemControllerDocs.
    @Override
    @DeleteMapping(
            value = "/{id}"
    )
    public ResponseEntity<?> delete(
            @PathVariable("orderId") Long orderId,
            @PathVariable("id") Long id
    ) {
        service.delete(orderId, id);
        return ResponseEntity.noContent().build(); // 204, sem conteúdo no corpo
    }

}

//Swagger UI: http://localhost:8080/swagger-ui/index.html
//YAML da documentação: http://localhost:8080/v3/api-docs.yaml
