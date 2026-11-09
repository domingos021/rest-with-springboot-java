# Guia Completo: Testes de Integração com REST Assured

> **Autor:** Domingos Diniz José Jovete  
> Guia prático e profissional: conceitos, configuração, passo a passo, testes de APIs REST, boas práticas, padrões BDD e troubleshooting.

## Sumário

1. [Introdução](#1-introdução)
2. [Conceitos fundamentais](#2-conceitos-fundamentais)
3. [REST Assured vs Outras Abordagens](#3-rest-assured-vs-outras-abordagens)
4. [Configuração do ambiente (Maven/Gradle)](#4-configuração-do-ambiente-mavengradle)
5. [Passo a passo: do zero ao primeiro teste](#5-passo-a-passo-do-zero-ao-primeiro-teste)
6. [Sintaxe BDD (Given-When-Then)](#6-sintaxe-bdd-given-when-then)
7. [Comandos e métodos essenciais](#7-comandos-e-métodos-essenciais)
8. [Validações e Extração de Dados (JsonPath e Hamcrest)](#8-validações-e-extração-de-dados-jsonpath-e-hamcrest)
9. [Testando APIs com Spring Boot (Testcontainers / H2)](#9-testando-apis-com-spring-boot-testcontainers--h2)
10. [Autenticação e Segurança (Basic, Bearer Token, OAuth2)](#10-autenticação-e-segurança-basic-bearer-token-oauth2)
11. [Boas Práticas de Arquitetura de Testes](#11-boas-práticas-de-arquitetura-de-testes)
12. [Troubleshooting](#12-troubleshooting)
13. [Quando usar e quando não usar REST Assured](#13-quando-usar-e-quando-não-usar-rest-assured)
14. [Cheat sheet](#14-cheat-sheet)
15. [Checklist de boas práticas](#15-checklist-de-boas-práticas)
16. [Referências](#16-referências)

## 1. Introdução

Um **teste de integração** valida a comunicação entre diferentes camadas ou sistemas, garantindo que os componentes funcionem bem juntos (por exemplo, testando endpoints HTTP reais da API, integração com banco de dados e serialização de JSON).

O **REST Assured** é uma biblioteca Java que simplifica enormemente a testagem e validação de serviços REST. O objetivo é eliminar o código boilerplate HTTP do Java (como HttpURLConnection ou WebClient complexos apenas para teste) e permitir escrever testes expressivos inspirados em BDD.

### Por que usar REST Assured?

| Benefício | Descrição | 
| ----- | ----- | 
| **Sintaxe Fluida (BDD)** | Estrutura limpa baseada em `given()`, `when()` e `then()`. | 
| **Integração nativa com Hamcrest** | Poderosos matchers para asserções legíveis (`equalTo`, `hasItem`, etc.). | 
| **Suporte a JSONPath e XMLPath** | Facilidade para extrair e validar nós específicos de payloads complexos. | 
| **Flexibilidade de Autenticação** | Suporte integrado para Basic Auth, Digest, Form, OAuth1/2 e Tokens JWT. | 
| **Ecossistema Java** | Integra-se perfeitamente com JUnit 4/5, TestNG, Maven, Gradle e Spring Boot. | 

## 2. Conceitos fundamentais

### 2.1 Teste Unitário vs Teste de Integração (API)

```
Teste Unitário                          Teste de Integração (REST Assured)
┌─────────────────┐                     ┌─────────┐      HTTP       ┌──────────────────┐
│ Mock / Service  │                     │  Teste  │ ──────────────► │ Controller / App │
└─────────────────┘                     └─────────┘   (GET/POST)    └────────┬─────────┘
(Isolado, sem I/O real)                                                │ Banco / Infra
                                                                        ▼
                                                                [ Banco de Dados ]
```

| Critério | Teste Unitário | Teste de Integração (REST Assured) | 
| ----- | ----- | ----- | 
| Escopo | Uma única classe/método isolado | Fluxo completo HTTP, rotas, segurança e persistência | 
| Velocidade | Altíssima (milisegundos) | Mais lento (envolve subida de contexto/servidor) | 
| Dependências | Mockadas (Mocks/Stubs) | Reais ou simuladas via Testcontainers/H2 | 
| Confiabilidade de contrato | Baixa (não testa a rota real) | Alta (testa o comportamento real da API) | 

### 2.2 Glossário essencial

| Termo | O que é | 
| ----- | ----- | 
| **Specification (`Request/ResponseSpecification`)** | Objetos reutilizáveis para configurar parâmetros globais (base URI, headers, logs). | 
| **Given-When-Then** | Padrão estrutural: precondições (`given`), ação (`when`) e asserções (`then`). | 
| **Matcher** | Regra de asserção da biblioteca Hamcrest (`equalTo()`, `notNullValue()`, etc.). | 
| **JsonPath** | Linguagem de consulta para extrair dados de documentos JSON. | 
| **Deserialização** | Conversão automática de JSON de resposta em objetos Java (POJOs) via Jackson ou Gson. | 
| **Filtro (`Filter`)** | Interceptador de requisições/respostas do REST Assured útil para logs customizados ou métricas. | 

## 3. REST Assured vs Outras Abordagens

| Critério | REST Assured (Java) | Postman / Newman | Spring MockMvc | 
| ----- | ----- | ----- | ----- | 
| Linguagem | Java / Groovy | JavaScript (Scripts) | Java | 
| Execução em CI/CD | Excelente (Maven/Gradle) | Requer CLI (Newman) | Nativo no ecossistema Spring | 
| Complexidade de payloads dinâmicos | Baixa (poder total do Java) | Média (baseada em scripts JS) | Baixa | 
| Teste de API externa (Black-box) | Excelente | Excelente | Limitado (focado no contexto interno) | 

## 4. Configuração do ambiente (Maven/Gradle)

### 4.1 Maven (`pom.xml`)

Adicione as dependências de escopo de teste (exemplo com JUnit 5 e REST Assured):

```xml
<dependencies>
    <!-- REST Assured -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.0</version>
        <scope>test</scope>
    </dependency>

    <!-- JSON Path para extrações avançadas -->
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-path</artifactId>
        <version>5.5.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 4.2 Gradle (`build.gradle` / `build.gradle.kts`)

```groovy
dependencies {
    testImplementation 'io.rest-assured:rest-assured:5.5.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:5.11.0'
}

test {
    useJUnitPlatform()
}
```

## 5. Passo a passo: do zero ao primeiro teste

### Passo 1: Estruturar a classe de teste

Crie sua classe de teste em `src/test/java/com/exemplo/tests/UserApiIntegrationTest.java`.

### Passo 2: Configurar a BaseURI global (opcional)

No método anotado com `@BeforeAll`, defina a URL base da aplicação.

### Passo 3: Escrever o teste básico (GET)

```java
package com.exemplo.tests;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiIntegrationTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }

    @Test
    public void deveRetornarUsuarioComSucesso() {
        given()
            .pathParam("id", 1)
        .when()
            .get("/users/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("username", notNullValue());
    }
}
```

### Passo 4: Executar o teste

Execute via IDE ou terminal:

```bash
mvn test -Dtest=UserApiIntegrationTest
```

## 6. Sintaxe BDD (Given-When-Then)

O REST Assured modela os testes de API simulando uma narrativa legível:

```java
given() // 1. O que você envia? (Headers, parâmetros, corpo, autenticação)
    .header("Authorization", "Bearer token123")
    .contentType("application/json")
    .body(payloadDto)
.when() // 2. Qual a ação HTTP e o endpoint? (GET, POST, PUT, DELETE)
    .post("/api/v1/products")
.then() // 3. O que você espera receber? (Status code, contratos, dados no corpo)
    .statusCode(201)
    .body("id", notNullValue())
    .body("name", is("Notebook Gamer"));
```

## 7. Comandos e métodos essenciais

> Legenda: ✅ **Quando usar** · ⚠️ **Quando evitar / cuidado**

### 7.1 Métodos HTTP e Requisição (`given()`)

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado | 
| ----- | ----- | ----- | ----- | 
| `given().param("q", "java")` | Adiciona parâmetros de Query String (`?q=java`) | Buscas e filtros em GETs | n/a | 
| `given().pathParam("id", 10)` | Substitui placeholders na URL (`/users/{id}`) | Rotas RESTful parametrizadas | n/a | 
| `given().header("Authorization", ...)` | Insere cabeçalhos HTTP customizados | Passar tokens e metadados | n/a | 
| `given().contentType(ContentType.JSON)` | Define o header `Content-Type: application/json` | Envio de corpos JSON | n/a | 
| `given().body(objeto)` | Envia um objeto (POJO, String, Map) no corpo | POST, PUT, PATCH | Evite passar Strings gigantes mal formatadas; use objetos ou arquivos | 
| `given().log().all()` | Imprime toda a requisição no console | Depuração de testes falhos | Evite deixar em testes de CI em massa (polui o log) | 

### 7.2 Métodos de Validação (`then()`)

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado | 
| ----- | ----- | ----- | ----- | 
| `then().statusCode(200)` | Valida o código de status HTTP exato | Garantir contrato de resposta | n/a | 
| `then().statusLine(containsString("OK"))` | Valida a linha de status completa | Casos específicos de protocolo | Raramente necessário | 
| `then().body("campo", equalTo(valor))` | Valida um valor exato no JSON retornado | Checar propriedades específicas | n/a | 
| `then().body("items.size()", greaterThan(0))` | Valida tamanho de listas | Listas paginadas ou coleções | n/a | 
| `then().time(lessThan(2000L))` | Valida o tempo de resposta em milissegundos | Testes de performance básicos / SLA | Varia conforme o ambiente de CI | 
| `then().log().ifValidationFails()` | Mostra detalhes apenas se o teste falhar | **Melhor prática de log** em CI/CD | n/a | 

## 8. Validações e Extração de Dados (JsonPath e Hamcrest)

### 8.1 Matchers do Hamcrest mais úteis

* `equalTo(val)`: Igualdade estrita.
* `hasItem(val)`: Verifica se uma lista contém um item.
* `hasItems(v1, v2)`: Verifica múltiplos itens.
* `notNullValue()` / `nullValue()`: Validação de nulidade.
* `containsString(sub)`: Substring em textos.
* `greaterThan(num)` / `lessThan(num)`: Comparações numéricas.

### 8.2 Extraindo valores para uso posterior

Se você precisa criar um recurso (POST) e usar o `id` gerado no próximo passo (GET ou DELETE):

```java
int criadoId = 
    given()
        .contentType("application/json")
        .body("{\"name\": \"Produto Teste\"}")
    .when()
        .post("/products")
    .then()
        .statusCode(201)
        .extract()
        .path("id"); // Extrai o ID diretamente do JSON de resposta

// Usando o ID extraído
given()
    .pathParam("id", criadoId)
.when()
    .get("/products/{id}")
.then()
    .statusCode(200)
    .body("name", equalTo("Produto Teste"));
```

## 9. Testando APIs com Spring Boot (Testcontainers / H2)

Em projetos Spring Boot, é comum subir a aplicação em uma porta aleatória para testá-la de ponta a ponta com REST Assured.

```java
package com.exemplo.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port; // Configura o REST Assured para apontar para a porta aleatória do Spring
    }

    @Test
    public void deveCriarProdutoComSucesso() {
        given()
            .contentType("application/json")
            .body("{\"name\": \"Teclado Mecânico\", \"price\": 299.99}")
        .when()
            .post("/api/products")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("Teclado Mecânico"));
    }
}
```

## 10. Autenticação e Segurança (Basic, Bearer Token, OAuth2)

### 10.1 Basic Auth

```java
given()
    .auth().basic("usuario", "senha")
.when()
    .get("/admin/metrics")
.then()
    .statusCode(200);
```

### 10.2 Bearer Token (JWT)

```java
given()
    .header("Authorization", "Bearer eyJhbGciOiJIUzI1Ni...")
.when()
    .get("/api/profile")
.then()
    .statusCode(200);
```

## 11. Boas Práticas de Arquitetura de Testes

* **Use Request/Response Specifications:** Evite repetir URLs base, headers e configs em todo teste. Crie especificações reutilizáveis:
  ```java
  RequestSpecification requestSpec = new RequestSpecBuilder()
      .setBaseUri("https://api.exemplo.com")
      .setContentType(ContentType.JSON)
      .build();
  ```
* **Mantenha os testes independentes:** Cada teste deve preparar seus próprios dados (Seed/Setup) e limpá-los após a execução para evitar efeitos colaterais.
* **Evite lógica de negócio complexa nos testes:** Testes de integração devem ser declarativos e focados no comportamento HTTP e contratos.
* **Utilize DTOs para payloads:** Em vez de passar JSONs em formato de `String` bruta, utilize objetos Java serializados via Jackson.

## 12. Troubleshooting

| Sintoma | Causa provável | Solução | 
| ----- | ----- | ----- | 
| `Connection refused` | A API não está rodando na porta ou URI informada | Verifique se o servidor subiu e se `RestAssured.port` ou `baseURI` estão corretos. | 
| `java.lang.NoClassDefFoundError: org/hamcrest/Matcher` | Conflito de versão ou ausência da dependência Hamcrest | Garanta que as versões do REST Assured e Hamcrest no build tool estão alinhadas. | 
| Erro de deserialização JSON (Jackson) | Falta de construtor padrão ou getters/setters nos POJOs | Adicione `@NoArgsConstructor`, `@AllArgsConstructor` e `@Data` (Lombok) nas classes de modelo. | 
| Testes intermitentes (flaky tests) | Concorrência de dados no banco compartilhado | Garanta isolamento de transações ou limpe o banco entre os testes. | 

## 13. Quando usar e quando não usar REST Assured

### ✅ Quando usar
* Testes de integração de APIs REST em ecossistema Java.
* Validação profunda de payloads JSON complexos usando Hamcrest/JsonPath.
* Testes de contrato automatizados em pipelines de CI/CD.
* Integração robusta com frameworks como Spring Boot (`@SpringBootTest`).

### ⚠️ Когда evitar
* Testes end-to-end puramente de interface gráfica (frontend) — prefira Cypress, Playwright ou Selenium.
* Projetos desenvolvidos em outras linguagens (Python, Node.js, Go) — utilize bibliotecas nativas da respectiva linguagem (SuperTest, PyTest, etc.).
* Testes unitários puros onde nenhum protocolo HTTP/rede é acionado.

## 14. Cheat sheet

```java
// ----- SETUP BÁSICO -----
RestAssured.baseURI = "https://api.exemplo.com";
RestAssured.port = 8080;

// ----- GET SIMPLES COM PARÂMETROS -----
given()
    .pathParam("id", 42)
.when()
    .get("/users/{id}")
.then()
    .statusCode(200)
    .body("name", equalTo("Maria"));

// ----- POST COM CORPO JSON E EXTRAÇÃO -----
String token = given()
    .contentType(ContentType.JSON)
    .body("{\"email\":\"a@a.com\",\"password\":\"123\"}")
.when()
    .post("/auth/login")
.then()
    .statusCode(200)
    .extract()
    .path("access_token");

// ----- REQUEST SPECIFICATION REUTILIZÁVEL -----
RequestSpecBuilder builder = new RequestSpecBuilder();
builder.setBaseUri("https://api.exemplo.com")
       .addHeader("Authorization", "Bearer " + token);
RequestSpecification spec = builder.build();

given()
    .spec(spec)
.when()
    .get("/protected-resource")
.then()
    .statusCode(200);
```

## 15. Checklist de boas práticas

**Estrutura e Configuração**
- [ ] Dependências do REST Assured e Hamcrest configuradas no escopo de teste (`test`).
- [ ] Uso de `RequestSpecification` e `ResponseSpecification` para evitar código duplicado.
- [ ] URLs e credenciais sensíveis externas em arquivos de propriedades ou variáveis de ambiente.

**Escrita dos Testes**
- [ ] Padrão BDD (`given()`, `when()`, `then()`) rigorosamente seguido.
- [ ] Asserções claras utilizando matchers descritivos do Hamcrest.
- [ ] Logs configurados de forma inteligente (`.log().ifValidationFails()`) para evitar poluição no CI.

**Isolamento e Confiabilidade**
- [ ] Testes independentes (sem dependência da ordem de execução).
- [ ] Massa de dados isolada por teste (evitando conflitos de chave primária ou concorrência).
- [ ] Integração com banco de dados validada (seja via H2 em memória ou Testcontainers).

## 16. Referências

* Documentação oficial do REST Assured: https://rest-assured.io/
* Repositório oficial no GitHub: https://github.com/rest-assured/rest-assured
* Documentação do Hamcrest: https://hamcrest.org/
* Guia de Testes do Spring Boot: https://docs.spring.io/spring-boot/reference/testing/index.html

---
*Documento de referência consolidado elaborado por Domingos Diniz José Jovete para estudo e uso profissional em arquiteturas Java.*