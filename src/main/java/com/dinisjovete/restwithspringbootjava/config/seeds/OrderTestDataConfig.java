package com.dinisjovete.restwithspringbootjava.config.seeds;

import com.dinisjovete.restwithspringbootjava.data_model.entities.order.Order;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderItem;
import com.dinisjovete.restwithspringbootjava.data_model.entities.order.OrderStatus;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.product.Product;

import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.repositories.order.OrderRepository;
import com.dinisjovete.restwithspringbootjava.repositories.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.logging.Logger;


/*
 * Spring configuration class dedicated to the test and development environment setup
 * and database seeding for Order and OrderItem entities.
 * <p>
 * Responsibilities:
 * - Configures settings specific to the "test" and "dev" Spring profiles.
 * - Executes idempotent database seeding via {@link CommandLineRunner} at application startup,
 *   checking each scenario by customer to prevent duplicates and ID inflation.
 * - Links each order to a customer already seeded as a Person (found by id) and each item
 *   to a product already seeded as a Book (found by title).
 */
@Configuration
@Profile({"test", "dev"})
/*
 * ============================================================================
 * @Order(3) - ORDEM DE EXECUÇÃO DOS SEEDS
 * ============================================================================
 * Quando existem vários CommandLineRunner, o Spring os executa em uma ordem
 * que, sem @Order, não é garantida. Quanto MAIOR o número, mais tarde roda.
 *
 *      1º PersonTestDataConfig  (@Order(1))  -> cria as pessoas
 *      2º BookTestDataConfig    (@Order(2))  -> cria os livros ligados às pessoas
 *      3º OrderTestDataConfig   (@Order(3))  -> cria os pedidos (clientes + livros)
 *
 * ATENÇÃO - CONFLITO DE NOMES:
 * A anotação do Spring se chama Order e a nossa entidade também. Como os dois
 * nomes não podem ser importados juntos, a entidade Order é importada
 * normalmente e a anotação é escrita com o nome completo (pacote incluso).
 * ============================================================================
 */
@org.springframework.core.annotation.Order(3)
public class OrderTestDataConfig implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(OrderTestDataConfig.class.getName());

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;
    private final ProductRepository productRepository;

    public OrderTestDataConfig(
            OrderRepository orderRepository,
            PersonRepository personRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.personRepository = personRepository;
        this.productRepository = productRepository;
    }

    /*
     * Record auxiliar criado dentro da classe de seed.
     *
     * Um record é uma classe simplificada do Java usada para representar dados.
     * Por baixo dos panos, o Java cria automaticamente o construtor, os métodos
     * de acesso aos atributos, equals(), hashCode() e toString().
     *
     * Representa temporariamente uma linha de pedido durante a criação dos
     * dados iniciais.
     *
     * Não é uma entidade JPA e não possui tabela no banco.
     * Ele apenas guarda as informações necessárias para localizar o produto
     * e criar posteriormente um OrderItem real.
     *
     * Campos:
     * - productTitle: nome do produto que será buscado no banco.
     * - quantity: quantidade de unidades desse produto no pedido.
     *
     * Exemplo:
     * SeedItem("REST com Spring Boot", 1)
     * cria automaticamente um objeto contendo:
     * productTitle = "REST com Spring Boot"
     * quantity = 1
     */
    private record SeedItem(String productTitle, int quantity) {
    }

    // ========================================================================
    // STARTUP EXECUTION (CommandLineRunner)
    // ========================================================================
    /*
     * String... args usa o recurso varargs, permitindo receber vários
     * valores do tipo String.
     *
     * Exemplo:
     *
     * java MinhaClasse teste dev prod
     *
     * O Java transforma os valores recebidos em um array:
     *
     * args[0] = "teste";
     * args[1] = "dev";
     * args[2] = "prod";
     *
     * public static void main(String... args) {}
     */

    @Override
    public void run(String... args) throws Exception {

        logger.info("Checking order seed data...");

        // Seed individual e idempotente por cliente:
        // Cada cenário usa um cliente DIFERENTE. O pedido só é criado se esse cliente
        // ainda não tiver nenhum pedido, preservando a sequência de IDs do banco
        // (evita inflar os IDs a cada restart).
        //
        // Os clientes são buscados pelo ID da Person criada no seed de pessoas
        // (mesma ordem do PersonTestDataConfig em banco vazio):
        //
        //      1 Ayrton Senna | 2 Nikola Tesla | 3 Ada Lovelace
        //      4 Albert Einstein | 5 Marie Curie | 6 Domingos Jovete
        //
        // Os produtos são buscados pelo TÍTULO do livro (mais seguro que o ID, que é
        // gerado pelo banco). O preço de cada item é o preço atual do livro, congelado
        // no item, exatamente como o OrderItemService faz.
        //
        // ATENÇÃO: se o cliente ou algum livro não existir, o pedido inteiro é pulado
        // com um aviso no log.

        // ====================================================================
        // CENÁRIO 1: pedido PENDING com 2 itens (pode ser alterado)
        // ====================================================================
        // Total esperado: 99.90 * 1 + 59.90 * 2 = 219.70

        /*
         * instanciamos a class temporária que e um record
         * e alimentamos o construtor dela
         */
        SeedItem item01 = new SeedItem("REST com Spring Boot", 1);
        SeedItem item02 = new SeedItem("My Inventions", 2);

        seedOrder(
                /*
                 * customerId:
                 * ID da pessoa que realizou o pedido.
                 *
                 * status:
                 * Estado inicial do pedido conforme as regras do domínio.
                 *
                 * SeedItem:
                 * Produtos adicionados ao pedido e suas respectivas quantidades.
                 */
                6L, // Domingos Jovete
                OrderStatus.PENDING,

                // Envia os itens criados para o método seedOrder()
                item01,
                item02
        );

        // ====================================================================
        // CENÁRIO 2: pedido PAID (os itens NÃO podem mais ser alterados)
        // ====================================================================
        // Serve para testar as respostas 409 (Conflict): adicionar, alterar ou
        // remover itens, e excluir um pedido que não está PENDING.
        // Total esperado: 69.90 * 1 + 79.90 * 1 = 149.80
        seedOrder(
                3L, // Ada Lovelace
                OrderStatus.PAID,
                /*
                 * Aqui fazemos algo diferente:
                 * criamos diretamente novos objetos do tipo SeedItem
                 * dentro da chamada do método, sem armazenar esses objetos
                 * em variáveis de referência.
                 *
                 * O método seedOrder() recebe esses objetos diretamente
                 * pelo parâmetro SeedItem... items.
                 */
                new SeedItem("Notes on the Analytical Engine", 1),
                new SeedItem("Relativity: The Special and General Theory", 1)
        );

        // ====================================================================
        // CENÁRIO 3: pedido PENDING com 3 itens
        // ====================================================================
        // Total esperado: 89.90 * 1 + 64.90 * 2 + 54.90 * 1 = 274.60
        /*
         * seedOrder é o método responsável pela criação de um pedido.
         *
         * Para executar essa lógica, ele precisa receber dados de entrada,
         * que são os valores necessários para montar uma entidade Order.
         *
         * Dados informados:
         * - customerId: identifica o cliente que realizou o pedido.
         * - status: define o estado inicial do pedido.
         * - SeedItem: classe temporária que representa os itens recebidos pelo seed,
         *   contendo apenas os dados necessários para localizar o produto e criar
         *   posteriormente um OrderItem.
         *
         * SeedItem... items:-> O operador varargs (...) permite receber uma quantidade variável
         * indica que o método pode receber vários objetos SeedItem como parâmetro
         * (quantidade variável de itens do mesmo tipo).
         *
         * Exemplo:
         * seedOrder(
         *     6L,
         *     OrderStatus.PENDING,
         *     new SeedItem("REST com Spring Boot", 1),
         *     new SeedItem("My Inventions", 2)
         * );
         */
        seedOrder(
                4L, // Albert Einstein
                OrderStatus.PENDING,
                new SeedItem("Treatise on Radioactivity", 1),
                new SeedItem("The World as I See It", 2),
                new SeedItem("Pierre Curie", 1)
        );

        // ====================================================================
        // CENÁRIO 4: pedido PENDING SEM itens
        // ====================================================================
        // Serve para testar o pedido recém-criado: "items": [] e total 0.00.
        // Total esperado: 0.00
        seedOrder(
                1L, // Ayrton Senna
                OrderStatus.PENDING
        );

        // Tesla (2) e Marie Curie (5) ficam SEM pedidos de propósito: servem para
        // testar a exclusão de uma Person sem pedidos (permitida) em contraste com
        // as pessoas acima (bloqueada).

        logger.info("Order seeding verification completed successfully!");

        // ====================================================================
        // DATABASE SEEDING COMPLETE
        // ====================================================================
    }

    // ========================================================================
    // HELPER: cria o pedido apenas se o cliente ainda não tiver pedido,
    // se o cliente existir e se todos os produtos existirem
    // ========================================================================
    /*
     * O operador varargs (...) permite receber uma quantidade variável
     * de objetos do mesmo tipo.
     *
     * Internamente, o Java trata os argumentos recebidos como um array.
     *
     * Exemplo:
     *
     * seedItems(item1, item2, item3);
     *
     * é convertido internamente para:
     *
     * OrderItem[] items = {item1, item2, item3};
     *
     * Isso facilita a criação de métodos que precisam trabalhar com
     * vários itens sem exigir que o chamador crie uma lista manualmente.
     */
    private void seedOrder(Long customerId, OrderStatus finalStatus, SeedItem... items) {

        /*
         * Verifica se já existe um pedido associado a este cliente.
         *
         * Caso exista, o seed não cria outro pedido para evitar
         * duplicação de dados durante a inicialização da aplicação.
         */
        if (orderRepository.existsByCustomerId(customerId)) {
            return;
        }


        /*
         * Busca o cliente responsável pelo pedido.
         *
         * O método findById() já vem pronto do JpaRepository
         * e retorna um Optional<Person>, pois o cliente pode ou não existir no banco.
         *
         * O orElse(null) extrai o objeto Person quando encontrado;
         * caso contrário, retorna null.
         *
         * Sem um cliente válido, não é possível criar o pedido,
         * pois o relacionamento Order -> Person exige um cliente existente.
         */
        Person customer = personRepository.findById(customerId).orElse(null);

        if (customer == null) {
            logger.warning("Order not seeded (customer id not found: " + customerId + ")");
            return;
        }

        // O construtor da entidade define: status PENDING, data do pedido e total zero.
        Order order = new Order(customer);

        for (int i = 0; i < items.length; i++) {

            Product product = productRepository.findByTitle(items[i].productTitle()).orElse(null);

            if (product == null) {
                logger.warning("Order not seeded (product title not found: "
                        + items[i].productTitle() + ") for customer id: " + customerId);
                return;
            }

            // Preço atual do produto, congelado no item. O addItem vincula o item ao
            // pedido e recalcula o total (só funciona enquanto o pedido está PENDING).
            order.addItem(new OrderItem(product, items[i].quantity(), product.getPrice()));
        }

        // O status só muda DEPOIS dos itens: em pedido que não é PENDING o addItem seria bloqueado.
        // changeStatus valida a transição pela regra do enum OrderStatus.
        if (finalStatus != OrderStatus.PENDING) {
            order.changeStatus(finalStatus);
        }

        // O id fica null: quem gera é o banco (GenerationType.IDENTITY).
        // Os itens são salvos junto, por cascade a partir do pedido.
        orderRepository.save(order);
        logger.info("Seeded order for customer id " + customerId + " (status: " + finalStatus + ")");
    }
}
