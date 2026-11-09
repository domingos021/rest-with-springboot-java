# Guia Completo: Testes de Integração com Testcontainers

> **Autor:** Domingos Diniz José Jovete
>
> Guia prático e profissional: conceitos, arquitetura, passo a passo, configuração com Spring Boot e JUnit 5, boas práticas e troubleshooting.

## Sumário

1. [Introdução](#1-introdução)
2. [Conceitos fundamentais](#2-conceitos-fundamentais)
3. [Testcontainers vs Outras Abordagens (H2 / Bancos Compartilhados)](#3-testcontainers-vs-outras-abordagens-h2--bancos-compartilhados)
4. [Configuração do ambiente (Maven/Gradle)](#4-configuração-do-ambiente-mavengradle)
5. [Passo a passo: do zero ao primeiro container em testes](#5-passo-a-passo-do-zero-ao-primeiro-container-em-testes)
6. [Integração com Spring Boot (DynamicPropertySource)](#6-integração-com-spring-boot-dynamicpropertysource)
7. [Módulos populares (PostgreSQL, Redis, MySQL, LocalStack)](#7-módulos-populares-postgresql-redis-mysql-localstack)
8. [Reuse de Containers (Singleton Container Pattern)](#8-reuse-de-containers-singleton-container-pattern)
9. [Comandos e métodos essenciais](#9-comandos-e-métodos-essenciais)
10. [Boas Práticas de Arquitetura de Testes](#10-boas-práticas-de-arquitetura-de-testes)
11. [Troubleshooting](#11-troubleshooting)
12. [Quando usar e quando não usar Testcontainers](#12-quando-usar-e-quando-não-usar-testcontainers)
13. [Cheat sheet](#13-cheat-sheet)
14. [Checklist de boas práticas](#14-checklist-de-boas-práticas)
15. [Referências](#15-referências)

---

## 1. Introdução

O **Testcontainers** é uma biblioteca de código aberto em Java que permite provisionar instâncias leves e descartáveis de bancos de dados, brokers de mensagens, navegadores ou qualquer outro serviço conteinerizado via Docker durante a execução de testes automatizados.

O grande objetivo é garantir que os testes de integração rodem em um ambiente **idêntico ao de produção**, eliminando o problema de dependências externas não configuradas ou inconsistências de comportamento de motores de banco de dados (como testar com H2 e rodar em PostgreSQL em produção).

### Por que usar Testcontainers?

| Benefício | Descrição |
|---|---|
| **Fidelidade de Ambiente** | Roda a versão real do banco/serviço (ex: PostgreSQL 16) em vez de um emulador em memória. |
| **Isolamento Total** | Cada execução de teste ou suíte ganha um container limpo e descartável. |
| **Infraestrutura como Código de Teste** | Todo o ciclo de vida do container é gerenciado diretamente pelo código de teste (JUnit 5). |
| **Portabilidade** | Qualquer desenvolvedor ou pipeline CI/CD com Docker instalado consegue rodar os testes sem setups manuais complexos. |

---

## 2. Conceitos fundamentais

### 2.1 Como o Testcontainers Funciona

```
┌────────────────────────────────────────────────────────┐
│                        JUnit 5                         │
│  ┌──────────────────────────────────────────────────┐  │
│  │ @Container / GenericContainer / PostgreSQLContainer│  │
│  └────────────────────────┬─────────────────────────┘  │
└───────────────────────────┼────────────────────────────┘
                            │ Comunicação Docker Socket
                            ▼
┌────────────────────────────────────────────────────────┐
│                     Docker Daemon                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │   Container Efêmero (Postgres / Redis / Kafka)   │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

### 2.2 Glossário essencial

| Termo | O que é |
|---|---|
| **Container Lifecycle** | Gerenciamento automático de `start()` e `stop()` atrelado às anotações do JUnit. |
| **GenericContainer** | Classe base genérica para subir qualquer imagem Docker customizada. |
| **Módulos Específicos** | Wrappers otimizados para serviços comuns (`PostgreSQLContainer`, `KafkaContainer`, `GenericContainer`). |
| **DynamicPropertySource** | Recurso do Spring Framework para injetar dinamicamente portas e URLs geradas pelo container no contexto da aplicação. |
| **Ryuk** | Componente interno do Testcontainers responsável por garantir a remoção dos containers mesmo se a JVM sofrer crash abrupto. |

---

## 3. Testcontainers vs Outras Abordagens (H2 / Bancos Compartilhados)

| Critério | Testcontainers | Banco em Memória (H2 / Derby) | Banco Compartilhado (Dev/QA fixo) |
|---|---|---|---|
| **Fidelidade com Produção** | **Alta** (usa a mesma imagem Docker) | **Baixa** (dialetos SQL podem diferir) | **Alta** |
| **Isolamento** | **Perfeito** (container isolado) | **Perfeito** | **Ruim** (conflitos de concorrência e dados sujos) |
| **Facilidade de Setup** | Baixa complexidade (apenas Docker instalado) | Muito baixa | Alta complexidade de manutenção de infra |
| **Velocidade** | Moderada (tempo de pull/subida na primeira vez) | Altíssima | Moderada |

---

## 4. Configuração do ambiente (Maven/Gradle)

### 4.1 Maven (`pom.xml`)

Adicione a BOM (Bill of Materials) do Testcontainers e os módulos que vai utilizar (ex: JUnit 5 e PostgreSQL):

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>1.19.8</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Testcontainers JUnit 5 Support -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Módulo PostgreSQL do Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 4.2 Gradle (`build.gradle`)

```groovy
dependencies {
    testImplementation platform('org.testcontainers:testcontainers-bom:1.19.8')
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:postgresql'
}
```

---

## 5. Passo a passo: do zero ao primeiro container em testes

### Passo 1: Criar o teste com JUnit 5 e o container PostgreSQL

```java
package com.exemplo.integration;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresContainerBasicTest {

    // O Testcontainers inicia o container antes dos testes e desliga após
    @Container
    static PostgreSQL<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Test
    public void deveConectarAoPostgresContainerizado() throws SQLException {
        assertTrue(postgres.isRunning());

        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
            assertTrue(conn.isValid(2));
            var statement = conn.createStatement();
            var resultSet = statement.executeQuery("SELECT 1");
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }
}
```

---

## 6. Integração com Spring Boot (DynamicPropertySource)

No Spring Boot, as portas e URLs do container são geradas de forma dinâmica em tempo de execução. O recurso `@DynamicPropertySource` injeta essas propriedades no ambiente do Spring.

```java
package com.exemplo.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SpringBootIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("app_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    public void contextoDeveCarregarComSucesso() {
        assertTrue(postgres.isRunning());
    }
}
```

---

## 7. Módulos populares (PostgreSQL, Redis, MySQL, LocalStack)

O ecossistema do Testcontainers possui dezenas de módulos prontos para uso:

| Módulo | Classe Exemplo | Caso de Uso |
|---|---|---|
| **PostgreSQL / MySQL / MariaDB** | `PostgreSQLContainer` | Bancos relacionais |
| **Redis** | `GenericContainer` (imagem `redis:alpine`) | Cache e sessões |
| **Kafka / RabbitMQ** | `KafkaContainer` / `RabbitMQContainer` | Mensageria assíncrona |
| **LocalStack** | `LocalStackContainer` | Simulação de serviços AWS (S3, SQS, DynamoDB) |
| **MongoDB** | `MongoDBContainer` | Bancos NoSQL |

---

## 8. Reuse de Containers (Singleton Container Pattern)

Para evitar o custo de subir e derrubar um container a cada classe de teste, você pode usar o padrão Singleton, mantendo o container ativo durante toda a execução da suíte de testes.

```java
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("shared_db")
                .withUsername("user")
                .withPassword("pass")
                .withReuse(true); // Habilita reuso (requer configuração no arquivo .testcontainers.properties)
        
        POSTGRES_CONTAINER.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }
}
```

---

## 9. Comandos e métodos essenciais

> Legenda: ✅ **Quando usar** · ⚠️ **Quando evitar / cuidado**

| Método / Comando | Para que serve | ✅ Khi usar | ⚠️ Cuidados |
|---|---|---|---|
| `withReuse(true)` | Reutiliza o container entre execuções de build locais | Acelerar testes de desenvolvimento | Só funciona se configurado no arquivo global `.testcontainers.properties` |
| `withClasspathResourceMapping(...)` | Copia arquivos de configuração do projeto para dentro do container | Injetar scripts SQL ou configs customizadas | Caminhos relativos ao classpath |
| `withExposedPorts(8080)` | Expõe portas adicionais do container | Mapear portas de serviços não padronizados | Prefira métodos prontos dos módulos oficiais |
| `waitingFor(Wait.forHttp("/health"))` | Define estratégia de prontidão baseada em HTTP | Garantir que a aplicação interna subiu antes de prosseguir | Timeout muito curto pode causar falhas intermitentes |

---

## 10. Boas Práticas de Arquitetura de Testes

- **Use imagens fixas:** Nunca utilize tags como `:latest`. Defina versões específicas (ex: `postgres:16-alpine`, `redis:7.2-alpine`) para evitar que quebras inesperadas ocorram por atualizações de imagem.
- **Isole o estado do banco:** Utilize scripts de limpeza (`@Sql` do Spring ou flyway/liquibase) para garantir que cada teste comece com um estado limpo.
- **Aproveite o paralelismo com cautela:** O JUnit 5 suporta execução paralela, mas certifique-se de que containers paralelos não disputem portas fixas (deixe sempre que o Testcontainers aloque portas aleatórias).

---

## 11. Troubleshooting

| Sintoma | Causa provável | Solução |
|---|---|---|
| `Could not find a valid Docker environment` | Docker Desktop / Daemon não está rodando ou sem permissão | Inicie o Docker e valide se o comando `docker ps` funciona no terminal. |
| `Container startup failed` | Porta já em uso no host ou falta de memória RAM | Verifique o consumo de recursos e certifique-se de usar portas dinâmicas. |
| Testes lentos na primeira execução | Download da imagem Docker (Pull image) | Normal na primeira execução. Em CI/CD, garanta cache de camadas do Docker. |

---

## 12. Quando usar e quando não usar Testcontainers

### ✅ Quando usar
- Testes de integração que exigem persistência em bancos de dados relacionais ou NoSQL reais.
- Validação de integração com mensageria (Kafka, RabbitMQ) ou filas assíncronas.
- Ambientes de CI/CD que possuem suporte a Docker-in-Docker (DinD) ou socket Docker montado.

### ⚠️ Quando evitar
- Testes estritamente unitários que não saem da JVM.
- Ambientes restritos onde o Docker não pode ser instalado ou executado por políticas corporativas rígidas.

---

## 13. Cheat sheet

```java
// ----- DEFINIÇÃO BÁSICA DE CONTAINER -----
PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
    .withDatabaseName("db")
    .withUsername("user")
    .withPassword("pass");

// ----- INICIALIZAÇÃO MANUAL / AUTOMÁTICA -----
postgres.start();
String jdbcUrl = postgres.getJdbcUrl();
postgres.stop();

// ----- INJEÇÃO DINÂMICA NO SPRING BOOT -----
@DynamicPropertySource
static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}
```

---

## 14. Checklist de boas práticas

**Configuração e Ambiente**
- [ ] Docker daemon ativo e acessível na máquina de desenvolvimento e no CI/CD.
- [ ] Versões das imagens Docker estritamente fixadas (`-alpine` ou `-slim` para otimizar tamanho).
- [ ] BOM do Testcontainers configurada no gerenciador de dependências (Maven/Gradle).

**Escrita dos Testes**
- [ ] Uso correto de `@Testcontainers` e `@Container` com visibilidade `static` quando apropriado.
- [ ] Propriedades dinâmicas injetadas via `@DynamicPropertySource` no Spring Boot.
- [ ] Limpeza de dados executada entre os testes para evitar dependência de estado.

---

## 15. Referências

* Documentação oficial do Testcontainers: https://www.testcontainers.org/
* Testcontainers para Java: https://java.testcontainers.org/
* Guia de Integração com Spring Boot: https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1
* Repositório oficial no GitHub: https://github.com/testcontainers/testcontainers-java

*Documento de referência consolidado elaborado por Domingos Diniz José Jovete para estudo e uso profissional em arquiteturas Java modernas.*