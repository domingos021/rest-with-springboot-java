package com.dinisjovete.restwithspringbootjava.integration_tests.swagger;

import com.dinisjovete.restwithspringbootjava.integration_tests.config_manual.AbstractIntegrationTestManual;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Sobe a aplicação completa em porta aleatória, evitando conflito de portas.
// O banco (Testcontainers) vem da classe base AbstractIntegrationTestManual.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // carrega as configurações de application-test
@DisplayName("Documentação OpenAPI - Pessoas V2")
class OpenApiDocumentationIntegrationTest extends AbstractIntegrationTestManual {

    private static final String OPENAPI_URL = "/v3/api-docs"; // endpoint do springdoc
    private static final String PERSON_V2_TAG = "People V2";

    @LocalServerPort
    private int port; // porta aleatória em que o servidor subiu neste teste

    private JsonPath openApi;
    private Map<String, Object> paths;

    // Carrega o contrato antes de cada teste, para que todos consultem o mesmo documento
    @BeforeEach
    void loadOpenApiDocument() {
        String body =
                given()
                        .port(port)
                        .accept(ContentType.JSON)
                        .when()
                        .get(OPENAPI_URL) // endpoint do springdoc que expõe o contrato OpenAPI em JSON
                        .then()
                        .statusCode(200)
                        .extract()
                        .asString();

        openApi = JsonPath.from(body); // transforma o JSON (String) em objeto consultável
        paths = openApi.getMap("paths"); // extrai a seção "paths": rotas e seus verbos HTTP
    }

    @Test
    @DisplayName("Deve conter a seção de paths")
    void shouldContainPathsSection() {
        assertNotNull(paths, "A documentação deve conter a seção 'paths'");
    }

    @ParameterizedTest(name = "{0} {1} deve estar documentado")
    @CsvSource({
            "GET,    /person/v2",
            "POST,   /person/v2",
            "GET,    /person/v2/{id}",
            "PUT,    /person/v2/{id}",
            "DELETE, /person/v2/{id}"
    })
    @DisplayName("Deve documentar as operações de pessoas V2")
    void shouldDocumentPersonV2Operations(String httpMethod, String path) {
        assertNotNull(paths, "A documentação deve conter a seção 'paths'");
        assertTrue(paths.containsKey(path), "O path %s deve estar documentado".formatted(path));

        // Verbos HTTP documentados para a rota (ex.: "/person/v2" -> get, post)
        @SuppressWarnings("unchecked") // cast seguro: no OpenAPI, o valor de cada rota é sempre um objeto (mapa)
        Map<String, Object> operations = (Map<String, Object>) paths.get(path);

        assertTrue(
                operations.containsKey(httpMethod.toLowerCase()),
                "A operação %s %s deve estar documentada".formatted(httpMethod, path)
        );
    }

    @Test
    @DisplayName("Deve agrupar os endpoints sob a tag People V2")
    void shouldDeclarePersonV2Tag() {
        List<String> tagNames = openApi.getList("tags.name");

        assertNotNull(tagNames, "A documentação deve declarar tags");
        assertTrue(
                tagNames.contains(PERSON_V2_TAG),
                "A documentação deve incluir a tag '%s'".formatted(PERSON_V2_TAG)
        );
    }
}