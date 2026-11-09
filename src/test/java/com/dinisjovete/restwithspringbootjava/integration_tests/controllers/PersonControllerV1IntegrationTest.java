package com.dinisjovete.restwithspringbootjava.integration_tests.controllers;

import com.dinisjovete.restwithspringbootjava.integration_tests.config.AbstractIntegrationTest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerV1IntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_PATH = "/person/v1";

    // Mesmo padrão da v2. Se o log do teste 1 mostrar outro nome, ajuste aqui.
    private static final String ID_FIELD = "ID_usuário";

    @LocalServerPort
    private int port;

    private RequestSpecification specification;

    private static Long personId;

    @BeforeEach
    void setUp() {
        specification = new RequestSpecBuilder()
                .setBasePath(BASE_PATH)
                .setPort(port)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("V1: deve criar uma pessoa e retornar 201")
    void shouldCreatePerson() {
        // CPF e e-mail únicos a cada execução, para não colidir com dados existentes
        String cpf = String.valueOf(10_000_000_000L + (long) (Math.random() * 89_999_999_999L));

        Map<String, Object> body = Map.of(
                "firstName", "Domingos",
                "lastName", "Jovete",
                "cpf", cpf,
                "email", "domingos.v1." + cpf + "@email.com",
                "password", "Ag123456@",
                "address", "Texas - EUA",
                "gender", "Male",
                "role", "CLIENT"
        );

        personId = given(specification)
                .body(body)
                .when().post()
                .then()
                .log().body()   // mostra o JSON real no console
                .statusCode(201)
                .body(ID_FIELD, notNullValue())
                .body("Nome", equalTo("Domingos"))
                .body("Sobrenome", equalTo("Jovete"))
                .extract().jsonPath().getLong(ID_FIELD);
    }

    @Test
    @Order(2)
    @DisplayName("V1: deve buscar a pessoa pelo id")
    void shouldFindPersonById() {
        given(specification)
                .pathParam("id", personId)
                .when().get("/{id}")
                .then()
                .statusCode(200)
                .body("Nome", equalTo("Domingos"))
                .body("Endereço", equalTo("Texas - EUA"));
    }

    @Test
    @Order(3)
    @DisplayName("V1: deve listar pessoas")
    void shouldFindAllPersons() {
        given(specification)
                .when().get()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    @DisplayName("V1: deve atualizar a pessoa")
    void shouldUpdatePerson() {
        Map<String, Object> body = Map.of(
                "firstName", "Ayrton",
                "lastName", "Senna da Silva",
                "address", "São Paulo - SP - Brasil",
                "gender", "Male"
        );

        given(specification)
                .pathParam("id", personId)
                .body(body)
                .when().put("/{id}")
                .then()
                .statusCode(200)
                .body("Nome", equalTo("Ayrton"))
                .body("Sobrenome", equalTo("Senna da Silva"));
    }

    @Test
    @Order(5)
    @DisplayName("V1: deve deletar a pessoa e retornar 204")
    void shouldDeletePerson() {
        given(specification)
                .pathParam("id", personId)
                .when().delete("/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    @DisplayName("V1: deve retornar 404 para pessoa deletada")
    void shouldReturnNotFoundAfterDelete() {
        given(specification)
                .pathParam("id", personId)
                .when().get("/{id}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("V1: deve retornar 400 quando o id não é numérico")
    void shouldReturnBadRequestForInvalidId() {
        given(specification)
                .when().get("/abc")
                .then()
                .statusCode(400);
    }
}