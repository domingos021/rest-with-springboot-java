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
class PersonControllerV2IntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_PATH = "/person/v2";

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
    @DisplayName("Deve criar uma pessoa e retornar 201 com CPF e e-mail mascarados")
    void shouldCreatePerson() {
        Map<String, Object> body = Map.of(
                "firstName", "Domingos",
                "lastName", "Jovete",
                "birthDate", "28/06/1971",
                "cpf", "99988877766",
                "phone", "+1 512 99999-9999",
                "email", "domingos.teste@email.com",
                "password", "Ag123456@",
                "address", "Texas - EUA",
                "gender", "Male",
                "role", "CLIENT"
        );

        personId = given(specification)
                .body(body)
                .when().post()
                .then()
                .statusCode(201)
                .body("ID_usuário", notNullValue())
                .body("Nome", equalTo("Domingos"))
                .body("Sobrenome", equalTo("Jovete"))
                .body("CPF", equalTo("999.***.***-66"))
                .body("Email", equalTo("do***@email.com"))
                .body("password", nullValue())
                .extract().jsonPath().getLong("ID_usuário");
    }

    @Test
    @Order(2)
    @DisplayName("Deve buscar a pessoa pelo id")
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
    @DisplayName("Deve listar pessoas")
    void shouldFindAllPersons() {
        given(specification)
                .when().get()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(4)
    @DisplayName("Deve atualizar a pessoa")
    void shouldUpdatePerson() {
        Map<String, Object> body = Map.of(
                "firstName", "Ayrton",
                "lastName", "Senna da Silva",
                "address", "São Paulo - SP - Brasil",
                "gender", "Male",
                "role", "ADMIN"
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
    @DisplayName("Deve deletar a pessoa e retornar 204")
    void shouldDeletePerson() {
        given(specification)
                .pathParam("id", personId)
                .when().delete("/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    @DisplayName("Deve retornar 404 para pessoa deletada")
    void shouldReturnNotFoundAfterDelete() {
        given(specification)
                .pathParam("id", personId)
                .when().get("/{id}")
                .then()
                .statusCode(404);
    }
}