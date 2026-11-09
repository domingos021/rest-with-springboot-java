package com.dinisjovete.restwithspringbootjava.integration_tests;

import com.dinisjovete.restwithspringbootjava.integration_tests.config.AbstractIntegrationTest;
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
 * Smoke test de infraestrutura: valida que a aplicação sobe com o
 * PostgreSQL do Testcontainers (via {@code @ServiceConnection}), o Flyway
 * e o JPA/Hibernate. Não testa regras de negócio.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ContextIntegrationTest extends AbstractIntegrationTest {

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

/*
Agora suba do zero:

powershell
docker compose build --no-cache
docker compose up -d
docker compose ps
docker compose logs -f app
 */