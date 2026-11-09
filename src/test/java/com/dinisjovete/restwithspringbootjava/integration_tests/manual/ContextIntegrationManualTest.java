package com.dinisjovete.restwithspringbootjava.integration_tests.manual;

import com.dinisjovete.restwithspringbootjava.integration_tests.config_manual.AbstractIntegrationTestManual;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test de infraestrutura com a configuração manual do Testcontainers.
 *
 * <p>Não testa regras de negócio. Valida apenas que a aplicação sobe
 * corretamente com o PostgreSQL do Testcontainers, o DataSource configurado
 * via {@code ApplicationContextInitializer}, o Flyway, o JPA/Hibernate e os
 * beans do Spring.
 *
 * <p>Ordem de execução:
 * <ol>
 *   <li>O container PostgreSQL sobe no bloco {@code static} da classe abstrata;</li>
 *   <li>O {@code Initializer} aplica as propriedades do banco no Environment;</li>
 *   <li>O Spring cria o DataSource e executa as migrations do Flyway;</li>
 *   <li>O Hibernate/JPA carrega as entidades e os beans são criados;</li>
 *   <li>Os testes são executados.</li>
 * </ol>
 *
 * <p>Se qualquer etapa anterior falhar, os testes falham antes de executar.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // ele aciona o arquivo properties src/test/resources/application-test.properties
class ContextIntegrationManualTest extends AbstractIntegrationTestManual {

    private static final int CONNECTION_TIMEOUT_SECONDS = 2;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void contextLoads() {
        assertNotNull(context, "O ApplicationContext não deveria ser nulo");
    }

    @Test
    @DisplayName("Deve conectar ao PostgreSQL do Testcontainers")
    void databaseIsReachable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(
                    connection.isValid(CONNECTION_TIMEOUT_SECONDS),
                    "A conexão com o banco deveria estar válida"
            );
        }
    }
}