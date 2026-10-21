package com.dinisjovete.restwithspringbootjava.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação da API (Swagger / OpenAPI).
 * Define as informações gerais exibidas na documentação.
 *
 * Acessos com a aplicação rodando:
 *  - JSON da documentação: <a href="http://localhost:8080/v2/api-docs">...</a>
 *  - Interface visual:     <a href="http://localhost:8080/swagger-ui/index.html">...</a>
 */
@Configuration // O Spring lê esta classe na inicialização
public class OpenApiConfig {

    /**
     * Bean é um objeto criado e gerenciado pelo Spring.
     * O SpringDoc usa este OpenAPI para montar o cabeçalho da documentação.
     * Os endpoints (controllers) são descobertos automaticamente.
     */
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RESTful API with Java, Spring Boot, Kubernetes and Docker")
                        .version("v1") // Versão da API
                        .description("Documentação da API REST")
                        .termsOfService("https://github.com/dinisjovete") // Link dos termos de uso
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0") // Texto da licença
                        )
                );
    }
}