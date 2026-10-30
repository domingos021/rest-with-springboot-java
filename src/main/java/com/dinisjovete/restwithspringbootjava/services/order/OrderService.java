package com.dinisjovete.restwithspringbootjava.services.order;

import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.order.OrderUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.Order;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderStatus;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.order.OrderMapper_MapStruct;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.repositories.order.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class OrderService {


    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class.getName());

    // Injeção de Dependência do Repositório JPA para acesso ao Banco de Dados (tabela orders)
    private final OrderRepository repository;

    /*
     * O pedido possui um cliente, que é uma entidade Person.
     *
     * Por isso o OrderService também precisa do PersonRepository: ao criar um pedido,
     * o cliente envia apenas o customerId, e é este repositório que busca a Person
     * correspondente no banco de dados para ser vinculada ao pedido.
     */
    private final PersonRepository personRepository;

    /*
     * Mapper ativo: MapStruct (OrderMapper_MapStruct).
     *
     * Neste módulo existe apenas a abordagem MapStruct. Ela cuida da conversão
     * Order (Entidade) -> OrderDTO (incluindo a lista de itens). As conversões que
     * mudam o estado do pedido NÃO são feitas por mapeamento automático: passam
     * pelos métodos de negócio da entidade (new Order, changeStatus).
     */
    private final OrderMapper_MapStruct mapper;


    @Autowired   // injetando as dependências no construtor
    public OrderService(OrderMapper_MapStruct mapper, OrderRepository repository, PersonRepository personRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.personRepository = personRepository;
    }

    /*
     * ============================================================================
     * SOBRE @Transactional
     * ============================================================================
     *
     * Na entidade Order, o cliente (customer) e a lista de itens (items) são LAZY:
     * o Hibernate só os carrega quando alguém os acessa, e isso só é possível com a
     * sessão do banco ainda aberta.
     *
     * O Mapper acessa customer.id e items ao converter para OrderDTO. Se a conversão
     * ocorresse fora de uma transação, seria lançada LazyInitializationException.
     *
     * - readOnly = true  -> consultas: o Hibernate não monitora alterações (mais leve).
     * - sem readOnly     -> operações de escrita: tudo ou nada (se algo falhar,
     *                       o banco volta ao estado anterior).
     * ============================================================================
     */

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {

        logger.info("Finding one order!");

        /*
         * Fluxo de Camadas:
         * OrderController -> OrderService -> OrderRepository -> Banco de Dados (Tabela orders)
         *
         * Caso não encontre o registro, o .orElseThrow lança a nossa ResourceNotFoundException
         * (que é capturada e tratada globalmente pelo ControllerAdvice, retornando HTTP 404).
         */
        Order entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this ID!"));

        // Converte a entidade JPA recuperada para o DTO de resposta (cliente apenas pelo id).
        return mapper.mapEntityToDto(entity);
    }

    //método que retorna uma lista do tipo OrderDTO
    //como vai trabalhar com hateoas, o controller poderá envolver a lista em CollectionModel
    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {

        logger.info("Finding all orders!");

        /*
         * web user request -> GET http://localhost:8080/order -> controller -> service -> repository -> banco de dados
         *
         * 1. O Controller recebe a requisição HTTP GET para listar todos os pedidos
         * 2. O Controller chama o método findAll() do Service
         * 3. O Service chama o findAll() do Repository, que executa a consulta no banco
         * 4. O Repository retorna uma lista de entidades Order para o Service
         * 5. O Mapper converte cada entidade Order em OrderDTO
         */
        return mapper.mapEntityListToDtoList(repository.findAll());
    }

    /*
     * ============================================================================
     * CREATE - CRIAÇÃO DE UM NOVO PEDIDO
     * ============================================================================
     *
     * O cliente envia apenas o customerId (OrderInsertDTO).
     *
     * Diferente do Book, aqui NÃO há mapDtoToEntity: o pedido é criado pelo
     * construtor da entidade, que já define as regras de nascimento:
     * - status  = PENDING
     * - orderDate = agora
     * - total   = 0 (sem itens; eles entram depois pelo OrderItemService)
     *
     * O Service aplica a regra de relacionamento:
     * - Busca a Person cliente no banco pelo customerId.
     * - Lança ResourceNotFoundException (HTTP 404) caso o cliente não exista.
     *
     * Fluxo:
     *
     * Cliente
     *    |
     *    ▼
     * Controller
     *    |
     *    ▼
     * OrderInsertDTO
     *    |
     *    ▼ (Busca a Person pelo customerId)
     * Person (cliente)
     *    |
     *    ▼
     * new Order(customer)   -> PENDING, sem itens
     *    |
     *    ▼
     * Repository.save()
     *    |
     *    ▼
     * Banco de Dados (INSERT)
     *    |
     *    ▼
     * OrderMapper.mapEntityToDto()
     *    |
     *    ▼
     * OrderDTO
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     * ============================================================================
     */
    @Transactional
    public OrderDTO create(OrderInsertDTO dto) {

        logger.info("Creating a new order!");

        // Busca a pessoa cliente pelo customerId informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Person customer = personRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for this ID!"));

        // O construtor da entidade garante o estado inicial correto do pedido.
        Order order = new Order(customer);

        // Entidade Order -> INSERT no banco de dados.
        Order savedOrder = repository.save(order);

        // Converte a entidade salva em DTO de resposta.
        return mapper.mapEntityToDto(savedOrder);
    }

    /*
     * ============================================================================
     * UPDATE - ATUALIZAÇÃO DO STATUS DE UM PEDIDO
     * ============================================================================
     *
     * O cliente envia o ID do pedido (pela URL) e o novo status (OrderUpdateDTO).
     *
     * O Mapper (updateEntityFromDTO) chama Order.changeStatus(), que valida a
     * transição usando a regra do enum OrderStatus:
     * - Transição permitida   -> o status é alterado.
     * - Transição inválida    -> InvalidOrderStatusTransitionException, que o
     *                            ControllerAdvice deve traduzir para HTTP 409 (Conflict).
     *
     * O Service não decide se a transição é válida: essa regra pertence à entidade.
     *
     * Fluxo:
     *
     * Controller
     *    |
     *    ▼
     * OrderService.update()
     *    |
     *    ▼
     * repository.findById(id)  -> 404 se não existir
     *    |
     *    ▼
     * OrderMapper.updateEntityFromDTO() -> Order.changeStatus()
     *    |
     *    ▼
     * repository.save()  (UPDATE; @PreUpdate atualiza updatedAt)
     *    |
     *    ▼
     * OrderMapper.mapEntityToDto()
     *    |
     *    ▼
     * Resposta HTTP para o cliente
     * ============================================================================
     */
    @Transactional
    public OrderDTO update(Long id, OrderUpdateDTO dto) {

        logger.info("Updating one order!");

        // Busca o pedido existente pelo ID informado.
        // Se não encontrar, lança ResourceNotFoundException e retorna HTTP 404.
        Order entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this ID!"));

        // Aplica o novo status pela porta de entrada da entidade (valida a transição).
        mapper.updateEntityFromDTO(dto, entity);

        // Salva a entidade modificada (UPDATE no banco).
        Order updatedOrder = repository.save(entity);

        return mapper.mapEntityToDto(updatedOrder);
    }

    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Remove um pedido pelo ID.
     *
     * REGRA: somente pedidos PENDING podem ser excluídos.
     * Pedido já pago (ou em qualquer outro status) é registro financeiro: em vez
     * de apagá-lo, o caminho correto é mudar o status (ex.: cancelamento) pelo update.
     *
     * Os itens do pedido são removidos junto (cascade ALL na entidade Order).
     * O cliente (Person) NÃO é removido (ciclo de vida independente).
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * OrderService.delete()
     *      |
     *      ↓ (Verifica se existe e se está PENDING)
     * OrderRepository.delete()
     *      |
     *      ↓
     * Remove de orders e order_items no Banco de Dados (DELETE)
     *
     * VOID -> não retorna conteúdo
     * ============================================================================
     */
    @Transactional
    public void delete(Long id) {

        logger.info("Deleting one order!");

        // Garante que o registro existe antes de tentar deletar
        Order entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for this ID!"));

        // Protege o histórico: pedido fora de PENDING não pode ser apagado.
        if (entity.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING orders can be deleted. Current status: " + entity.getStatus());
        }

        repository.delete(entity);

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
        ├── Entidade Order
        |       |
        |       └── invariantes (status, total, itens)
        |
        ├── Mapper
        |       |
        |       └── Entity -> DTO
        |
        └── Repository
                |
                └── Banco de Dados
     */
