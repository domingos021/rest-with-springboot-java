package com.dinisjovete.restwithspringbootjava.config.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Regra global de CORS da API.
 * Define quais origens podem fazer requisições e em quais condições.
 */
@Configuration // 1ª — indica ao Spring que esta classe contém configurações
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 2ª — o Spring lê esta propriedade do application.properties:
     *
     * app.cors.allowed-origins=${CORS_ORIGINS:http://localhost:3000,http://localhost:5173}
     * e coloca e define o atributo [] allowedOrigins que e um array com este valor
     * Se CORS_ORIGINS estiver definida no ambiente, usa o valor dela.
     * Caso contrário, usa as duas origens padrão.
     */
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * 3ª — o Spring MVC chama este método para registrar as regras globais de CORS.
     */
    @Override // Indica que este método sobrescreve um método de WebMvcConfigurer
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**") // 4ª — aplica as regras a todos os caminhos da API
                .allowedOrigins(allowedOrigins) // 5ª — permite requisições das origens configuradas
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                ) // 6ª — permite estes métodos HTTP
                .allowedHeaders("*") // 7ª — permite qualquer cabeçalho HTTP
                //.allowCredentials(true)
                .maxAge(3600); // 8ª — guarda o resultado do preflight por 1 hora
    }
}