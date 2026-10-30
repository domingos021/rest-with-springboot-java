package com.dinisjovete.restwithspringbootjava.controllers.order;



import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderUpdateDTO;
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
 * DOCUMENTAÇÃO SWAGGER - OrderControllerDocs
 * ============================================================================
 *
 * Interface que concentra TODA a documentação OpenAPI (Swagger) do
 * OrderController, mantendo a classe do Controller limpa.
 *
 * Regras:
 * - As assinaturas aqui precisam bater com as do Controller (@Override).
 * - Os mapeamentos HTTP (@GetMapping, @PostMapping...) continuam no Controller.
 * ============================================================================
 */
@Tag(name = "Order", description = "Pedidos: criação, consulta, atualização de status e exclusão")
public interface OrderControllerDocs {


    @Operation(
            summary = "Busca um pedido pelo ID",
            description = "Retorna o pedido com seus itens e os links HATEOAS.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content)
            }
    )
    OrderDTO findById(@Parameter(description = "ID do pedido") Long id);


    @Operation(
            summary = "Lista todos os pedidos",
            description = "Retorna a coleção de pedidos com links HATEOAS. Se não houver pedidos, a coleção vem vazia.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200")
            }
    )
    CollectionModel<OrderDTO> findAll();


    @Operation(
            summary = "Cria um novo pedido",
            description = "Cria um pedido PENDING, sem itens, para o cliente informado em customer_id.",
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Customer Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Unsupported Media Type", responseCode = "415", content = @Content)
            }
    )
    ResponseEntity<OrderDTO> create(OrderInsertDTO orderInsert);


    @Operation(
            summary = "Atualiza o status de um pedido",
            description = "Altera o status do pedido. A transição é validada pela regra do enum OrderStatus.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = OrderDTO.class))),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Invalid Status Transition", responseCode = "409", content = @Content),
                    @ApiResponse(description = "Unsupported Media Type", responseCode = "415", content = @Content)
            }
    )
    OrderDTO update(@Parameter(description = "ID do pedido") Long id, OrderUpdateDTO orderUpdate);


    @Operation(
            summary = "Remove um pedido",
            description = "Remove um pedido PENDING e seus itens. Pedidos em outros status não podem ser removidos.",
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Order Is Not PENDING", responseCode = "409", content = @Content)
            }
    )
    ResponseEntity<?> delete(@Parameter(description = "ID do pedido") Long id);
}
