package com.dinisjovete.restwithspringbootjava.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação da API (Swagger / OpenAPI).
 * Define as informações gerais exibidas na documentação e os grupos (People V1, People V2 e Book).
 *
 * Acessos com a aplicação rodando:
 *  - Interface visual:      http://localhost:8080/swagger-ui/index.html
 *  - JSON de todos:         http://localhost:8080/v3/api-docs
 *  - JSON só da V1:         http://localhost:8080/v3/api-docs/v1
 *  - JSON só da V2:         http://localhost:8080/v3/api-docs/v2
 *  - JSON só de Book:       http://localhost:8080/v3/api-docs/book
 *  - YAML de todos:         http://localhost:8080/v3/api-docs.yaml
 */
@Configuration // O Spring lê esta classe na inicialização
public class OpenApiConfig {

    /**
     * Bean é um objeto criado e gerenciado pelo Spring.
     * O SpringDoc usa este OpenAPI para montar o cabeçalho da documentação.
     * Os endpoints (controllers) são descobertos automaticamente.
     *
     * Este cabeçalho é compartilhado por todos os grupos; a versão de cada
     * grupo é ajustada nos customizers dos beans peopleV1(), peopleV2() e book().
     */
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java, Spring Boot, Kubernetes and Docker")
                        .version("v1") // Versão padrão; cada grupo sobrescreve abaixo
                        .description("Documentação da API REST")
                        .termsOfService("https://github.com/dinisjovete") // Link dos termos de uso
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0") // Texto da licença
                        )
                );
    }

    /*
     * ============================================================================
     * GRUPOS DO SWAGGER
     * ============================================================================
     * Cada GroupedOpenApi é um "grupo" de documentação com os seus próprios
     * endpoints. Com mais de um grupo, o Swagger UI mostra o seletor
     * "Select a definition" no topo e exibe só os endpoints do grupo escolhido.
     *
     * O filtro é feito por caminho (pathsToMatch): a versão na URL
     * (/person/v1, /person/v2) ou o recurso (/book) é o que separa os grupos.
     * ============================================================================
     */

    // Grupo da versão 1: só aparecem os endpoints /person/v1/**
    @Bean
    GroupedOpenApi peopleV1() {
        return GroupedOpenApi.builder()
                .group("v1")                    // nome interno (vai na URL /v3/api-docs/v1)
                .displayName("People V1")       // nome mostrado no seletor do Swagger UI
                .pathsToMatch("/person/v1/**")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setVersion("v1"))
                .build();
    }

    // Grupo da versão 2: só aparecem os endpoints /person/v2/**
    @Bean
    GroupedOpenApi peopleV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .displayName("People V2")
                .pathsToMatch("/person/v2/**")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setVersion("v2"))
                .build();
    }

    // Grupo de livros: só aparecem os endpoints /book/**
    @Bean
    GroupedOpenApi book() {
        return GroupedOpenApi.builder()
                .group("book")                  // nome interno (vai na URL /v3/api-docs/book)
                .displayName("Book")            // nome mostrado no seletor do Swagger UI
                .pathsToMatch("/book/**")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setVersion("v1"))
                .build();
    }
}