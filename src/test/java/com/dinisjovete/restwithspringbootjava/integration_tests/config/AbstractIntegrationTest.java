package com.dinisjovete.restwithspringbootjava.integration_tests.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

/*
 * Configuração moderna: o @ServiceConnection faz o Spring Boot
 * configurar sozinho URL, usuário, senha e driver.
 *
 * Fluxo:
 * static { start() } -> Container PostgreSQL sobe (uma vez por JVM)
 *      -> @ServiceConnection -> Spring Boot + Flyway + JPA
 */
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }
}