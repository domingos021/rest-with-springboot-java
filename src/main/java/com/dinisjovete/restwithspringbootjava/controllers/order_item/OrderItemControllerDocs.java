package com.dinisjovete.restwithspringbootjava.controllers.order_item;


import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.orderitem.OrderItemUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;

/*
 * ============================================================================
 * DOCUMENTAÇÃO SWAGGER - OrderItemControllerDocs
 * ============================================================================
 *
 * Interface que concentra TODA a documentação OpenAPI (Swagger) do
 * OrderItemController, mantendo a classe do Controller limpa.
 *
 * Regras:
 * - As assinaturas aqui precisam bater com as do Controller (@Override).
 * - Os mapeamentos HTTP (@GetMapping, @PostMapping...) continuam no Controller.
 * ============================================================================
 */
@Tag(name = "Order Item", description = "Itens de um pedido: adição, consulta, alteração de quantidade e remoção")
public interface OrderItemControllerDocs {


    @Operation(
            summary = "Busca um item de um pedido",
            description = "Retorna o item com os links HATEOAS. Um item de outro pedido não é encontrado.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderItemDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Order Or Item Not Found", responseCode = "404", content = @Content)
            }
    )
    OrderItemDTO findById(
            @Parameter(description = "ID do pedido") Long orderId,
            @Parameter(description = "ID do item") Long id
    );


    @Operation(
            summary = "Lista os itens de um pedido",
            description = "Retorna a coleção de itens do pedido com links HATEOAS. Pedido sem itens retorna coleção vazia.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200"),
                    @ApiResponse(description = "Order Not Found", responseCode = "404", content = @Content)
            }
    )
    CollectionModel<OrderItemDTO> findAll(@Parameter(description = "ID do pedido") Long orderId);


    @Operation(
            summary = "Adiciona um item ao pedido",
            description = "Adiciona um produto ao pedido. O preço é o preço atual do produto, congelado no item. "
                    + "Só é permitido em pedidos PENDING.",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = OrderItemDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Order Or Product Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Order Is Not PENDING", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Unsupported Media Type", responseCode = "415", content = @Content)
            }
    )
    ResponseEntity<OrderItemDTO> create(
            @Parameter(description = "ID do pedido") Long orderId,
            OrderItemInsertDTO itemInsert
    );


    @Operation(
            summary = "Atualiza a quantidade de um item",
            description = "Altera a quantidade do item e recalcula o subtotal do item e o total do pedido. "
                    + "Só é permitido em pedidos PENDING.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderItemDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Order Or Item Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Order Is Not PENDING", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Unsupported Media Type", responseCode = "415", content = @Content)
            }
    )
    OrderItemDTO update(
            @Parameter(description = "ID do pedido") Long orderId,
            @Parameter(description = "ID do item") Long id,
            OrderItemUpdateDTO itemUpdate
    );


    @Operation(
            summary = "Remove um item do pedido",
            description = "Remove o item e recalcula o total do pedido. Só é permitido em pedidos PENDING.",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Order Or Item Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Order Is Not PENDING", responseCode = "409", content = @Content)
            }
    )
    ResponseEntity<?> delete(
            @Parameter(description = "ID do pedido") Long orderId,
            @Parameter(description = "ID do item") Long id
    );
}
