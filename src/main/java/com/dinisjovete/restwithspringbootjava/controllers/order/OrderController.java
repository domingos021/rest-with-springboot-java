package com.dinisjovete.restwithspringbootjava.controllers.order;

import com.dinisjovete.restwithspringbootjava.config.hateoas.order.OrderHateoasAssembler;
import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderUpdateDTO;
import com.dinisjovete.restwithspringbootjava.services.order.OrderService;
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
 * CONTROLLER - OrderController
 * ============================================================================
 *
 * RESPONSABILIDADE
 * ----------------
 * Traduzir HTTP <-> Java. Recebe a requisição, valida o formato da entrada,
 * delega ao Service e devolve a resposta com o status HTTP correto e os links
 * HATEOAS.
 *
 * O Controller NÃO contém regra de negócio: ela mora no OrderService e na
 * entidade Order (status, total e itens).
 *
 * Os itens do pedido são gerenciados pelo OrderItemController
 * (/order/{orderId}/item).
 *
 * A documentação Swagger NÃO mora aqui: ela fica na interface
 * OrderControllerDocs, que esta classe implementa (inclusive o @Tag).
 * ============================================================================
 */
@RestController
/*
 * ============================================================================
 * @RequestMapping
 * ============================================================================
 * Define o caminho base (Base URL) deste Controller.
 *
 * Todas as requisições para este Controller começarão com:
 *
 *      http://localhost:8080/order
 *
 * Assim, todos os métodos desta classe herdam esse caminho base.
 * ============================================================================
 */
@RequestMapping("/order")
public class OrderController implements OrderControllerDocs { // documentação Swagger na interface

    // =========================================================================
    // Injeção de Dependência (Dependency Injection)
    // =========================================================================
    // 'final' garante que a referência não poderá ser alterada após a
    // construção do objeto, tornando a classe mais segura e imutável.
    // =========================================================================
    private final OrderService service;
    private final OrderHateoasAssembler assembler; // adiciona os links HATEOAS aos DTOs

    // =========================================================================
    // Construtor utilizado pelo Spring para realizar a Injeção de Dependência.
    //
    // A partir do Spring Boot 3.x, quando existe apenas um construtor,
    // a anotação @Autowired não é mais necessária.
    // =========================================================================
    public OrderController(OrderService orderService, OrderHateoasAssembler orderHateoasAssembler) {
        this.service = orderService;
        this.assembler = orderHateoasAssembler;
    }

    /*
     * ============================================================================
     * GET /order/{id}  ->  BUSCAR UM PEDIDO PELO ID
     * ============================================================================
     *
     * Retorna um pedido (com seus itens) a partir do ID, repassando o DTO
     * retornado pelo Service.
     *
     *      GET http://localhost:8080/order/1
     *
     * O formato da resposta é escolhido pelo cabeçalho Accept:
     *
     *      Accept: application/json
     *      Accept: application/xml
     *      Accept: application/yaml
     *
     * Respostas:
     *
     *      200 OK          pedido encontrado (corpo: OrderDTO com links)
     *      404 Not Found   ID inexistente
     *      400 Bad Request ID que não é número (ex.: /order/abc)
     * ============================================================================
     */
    // @Override: este método implementa findById(Long) de OrderControllerDocs.
    // A assinatura (nome + tipos dos parâmetros) precisa bater; senão o compilador acusa erro.
    @Override
    @GetMapping(
            value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    // application/yaml -> atendido pelo YamlJackson2HttpMessageConverter,
                    // que só é acionado quando o cliente pede YAML no cabeçalho Accept.
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public OrderDTO findById(@PathVariable("id") Long id) {
        /*
         * Fluxo da requisição:
         *
         * Controller -> Service -> Repository -> Banco de Dados
         *
         * O Service converte a Entity em OrderDTO (dentro de uma transação, por
         * causa dos relacionamentos LAZY) e o Controller devolve o DTO.
         */
        OrderDTO order = service.findById(id);

        // O DTO volta enriquecido com os links HATEOAS (self, orders, items).
        return assembler.addLinks(order);
    }


    /*
     * ============================================================================
     * GET /order  ->  LISTAR TODOS OS PEDIDOS (com HATEOAS)
     * ============================================================================
     *
     * Retorna todos os pedidos com suporte completo a HATEOAS, tanto para os
     * itens individuais quanto para a coleção como um todo.
     *
     * Respostas:
     *
     *      200 OK  sempre. Se não houver nenhum pedido, a coleção vem vazia (isso não é erro).
     * ============================================================================
     */
    // @Override: implementa findAll() de OrderControllerDocs.
    @Override
    @GetMapping(
            value = "",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    CustomMediaType.APPLICATION_YAML_VALUE
            }
    )
    public CollectionModel<OrderDTO> findAll() {

        // 1º Passo: busca os dados via Service (lista comum do Java).
        List<OrderDTO> orders = service.findAll();


        /*
         * 2º Passo: adiciona os links HATEOAS individuais em cada pedido da lista.
         *
         * forEach -> método da interface Iterable/List que percorre cada elemento
         * da coleção e executa uma ação definida por uma expressão lambda ou
         * referência de método.
         *
         * Aqui cada OrderDTO da lista recebe seus links HATEOAS através do assembler.
         */


        /*
         * Opção 1:
         * Usando Method Reference (referência de método).
         *
         * É uma forma reduzida da lambda:
         *
         * order -> assembler.addLinks(order)
         */
        orders.forEach(assembler::addLinks);


        /*
         * Opção 2:
         * Usando Lambda (alternativa à opção 1).
         *
         * orders.forEach(order -> assembler.addLinks(order));
         */


        /*
         * Opção 3:
         * Usando for-each tradicional (enhanced for).
         *
         * for (OrderDTO order : orders) {
         *     assembler.addLinks(order);
         * }
         */


        // 3º Passo: empacota a lista em um CollectionModel e adiciona
        // o link da própria coleção.
        return CollectionModel.of(
                orders,
                linkTo(
                        methodOn(OrderController.class)
                                .findAll()
                )
                        .withSelfRel() // link "self" da coleção
        );
    }


    /*
     * ============================================================================
     * POST /order  ->  CRIAR UM NOVO PEDIDO
     * ============================================================================
     *
     * Recebe um OrderInsertDTO no corpo da requisição (@RequestBody), valida
     * com @Valid e devolve o OrderDTO criado com o status 201 (Created).
     *
     * O pedido nasce PENDING e sem itens. Os itens são adicionados depois em
     * POST /order/{orderId}/item.
     *
     * Respostas:
     *
     *      201 Created     pedido criado (corpo: OrderDTO com links)
     *      400 Bad Request corpo malformado ou que viola as regras de validação
     *      404 Not Found   customer_id inexistente
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (POST http://localhost:8080/order)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "customer_id": 7
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (POST http://localhost:8080/order)
     * ----------------------------------------------------------------------------
     *
     *      <OrderInsertDTO>
     *        <customer_id>7</customer_id>
     *      </OrderInsertDTO>
     *
     * O DTO de entrada não possui "id", "status", "total_amount" nem datas:
     * tudo isso é definido pelo sistema.
     * ============================================================================
     */
    // @Override: implementa create(OrderInsertDTO) de OrderControllerDocs.
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
    public ResponseEntity<OrderDTO> create(@Valid @RequestBody OrderInsertDTO orderInsert) {
        OrderDTO createdOrder = service.create(orderInsert);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.addLinks(createdOrder)); // devolve o DTO criado, já com links HATEOAS
    }


    /*
     * ============================================================================
     * PUT /order/{id}  ->  ATUALIZAR O STATUS DE UM PEDIDO
     * ============================================================================
     *
     *      PUT http://localhost:8080/order/1
     *
     * O ID vem pela URL (@PathVariable) e o novo status vem pelo corpo da
     * requisição (@RequestBody), convertido em um OrderUpdateDTO e validado
     * com @Valid.
     *
     * Respostas:
     *
     *      200 OK          status atualizado (corpo: OrderDTO com links)
     *      400 Bad Request corpo malformado, status inexistente no enum ou ID que não é número
     *      404 Not Found   ID inexistente
     *      409 Conflict    transição de status não permitida
     *      415 Unsupported Media Type  Content-Type fora de JSON/XML/YAML
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE JSON PARA O POSTMAN (PUT http://localhost:8080/order/1)
     * ----------------------------------------------------------------------------
     *
     *      {
     *        "status": "PAID"
     *      }
     *
     * ----------------------------------------------------------------------------
     * EXEMPLO DE XML PARA O POSTMAN (PUT http://localhost:8080/order/1)
     * ----------------------------------------------------------------------------
     *
     *      <OrderUpdateDTO>
     *        <status>PAID</status>
     *      </OrderUpdateDTO>
     * ============================================================================
     */
    // @Override: implementa update(Long, OrderUpdateDTO) de OrderControllerDocs.
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
    public OrderDTO update(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderUpdateDTO orderUpdate
    ) {
        return assembler.addLinks(
                service.update(id, orderUpdate)
        );
    }


    /*
     * ============================================================================
     * DELETE /order/{id}  ->  REMOVER UM PEDIDO
     * ============================================================================
     *
     *      DELETE http://localhost:8080/order/{id}
     *
     * O ID do pedido a remover vem pela URL. Somente pedidos PENDING podem ser
     * removidos; os itens do pedido são removidos junto.
     *
     * Respostas:
     *
     *      204 No Content  removido com sucesso (sem corpo na resposta)
     *      404 Not Found   ID inexistente
     *      409 Conflict    pedido que não está PENDING
     *      400 Bad Request ID que não é número
     * ============================================================================
     */
    // @Override: implementa delete(Long) de OrderControllerDocs.
    @Override
    @DeleteMapping(
            value = "/{id}"
    )
    public ResponseEntity<?> delete(
            @PathVariable("id") Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204, sem conteúdo no corpo
    }

}

//Swagger UI: http://localhost:8080/swagger-ui/index.html
//YAML da documentação: http://localhost:8080/v3/api-docs.yaml
