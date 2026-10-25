
/*
 * =============================================================================
 * WEB CONFIG - CONFIGURAÇÃO DO SPRING MVC (FORMATOS DE RESPOSTA)
 * =============================================================================
 *
 * OBJETIVO:
 *
 * Definir em quais formatos a API pode responder (JSON, XML e YAML) e como o
 * Spring escolhe o formato de cada resposta. É a única classe do projeto que
 * personaliza o comportamento do Spring MVC nesse ponto.
 *
 * -----------------------------------------------------------------------------
 * QUEM USA ESTA CLASSE:
 *
 * Nenhuma classe do projeto chama o WebConfig diretamente. Ele implementa
 * WebMvcConfigurer, então o próprio Spring o detecta (@Configuration) na
 * inicialização e chama seus métodos ao montar o Spring MVC.
 *
 * Quem sente o efeito dele são os endpoints que devolvem DTOs:
 *
 *      BookController          (/book)
 *      PersonController        (/person/v1)
 *      PersonControllerV2      (/person/v2)
 *
 * -----------------------------------------------------------------------------
 * PARA QUE SERVE CADA PARTE:
 *
 * 1) configureContentNegotiation()
 *    Define a regra de escolha do formato: o cliente pede pelo header Accept
 *    (application/json, application/xml ou application/yaml) e, se não pedir
 *    nada, a resposta sai em JSON (defaultContentType). Um Accept não
 *    suportado, como text/html, recebe 406 Not Acceptable.
 *
 * 2) extendMessageConverters()
 *    Registra o conversor de YAML (YamlJackson2HttpMessageConverter) no lugar
 *    do conversor de YAML automático do Spring. O nosso usa uma cópia do
 *    ObjectMapper do Spring Boot, então o YAML segue as mesmas regras do JSON
 *    (ex.: LocalDateTime sai como "1916-01-01T00:00:00" e não como lista de
 *    números).
 *
 * 3) Construtor com ObjectMapper
 *    Recebe o ObjectMapper configurado pelo Spring Boot, que é a base do
 *    conversor de YAML.
 *
 * -----------------------------------------------------------------------------
 * FLUXO DE UMA REQUISIÇÃO:
 *
 *      Cliente (Postman / Front-end)
 *          |
 *          | GET /book/1   Accept: application/yaml
 *          v
 *      Spring MVC  --> consulta as regras do WebConfig
 *          |
 *          | formato pedido = YAML
 *          v
 *      YamlJackson2HttpMessageConverter
 *          |
 *          v
 *      Resposta em YAML
 *
 * -----------------------------------------------------------------------------
 * DEPENDÊNCIAS E CUIDADOS:
 *
 * - Depende do jackson-dataformat-yaml e do jackson-dataformat-xml no pom.xml.
 * - Depende de o ObjectMapperConfig (config.somente_estudo) continuar
 *   desativado. Se ele for reativado com "new ObjectMapper()", o mapper
 *   injetado aqui perde o módulo de datas e o erro de LocalDateTime volta.
 * =============================================================================
 */




package com.dinisjovete.restwithspringbootjava.config.web_config;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.dinisjovete.restwithspringbootjava.serialization.converter.YamlJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.yaml.MappingJackson2YamlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
     * ObjectMapper já configurado pelo Spring Boot (módulo de datas, features, etc.).
     * É injetado por construtor e serve de base para o conversor de YAML, para que
     * JSON e YAML sigam as mesmas regras de serialização.
     *
     * ATENÇÃO: isto só funciona enquanto o ObjectMapperConfig (config.somente_estudo)
     * estiver desativado. Se ele for reativado com "new ObjectMapper()", o mapper
     * injetado aqui passa a ser um mapper "pelado", sem JavaTimeModule.
     */
    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    /*
     * REGISTRO DO CONVERSOR DE YAML
     *
     * Com o jackson-dataformat-yaml no pom.xml, o Spring MVC registra sozinho um
     * conversor de YAML próprio (MappingJackson2YamlHttpMessageConverter), que não
     * usa o ObjectMapper do Boot (a data saía como lista de números).
     *
     * Aqui removemos esse conversor automático e registramos o nosso, baseado em
     * uma cópia do ObjectMapper do Boot, para que ele seja o único responsável
     * por application/yaml.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.removeIf(c -> c instanceof MappingJackson2YamlHttpMessageConverter);
        converters.add(new YamlJackson2HttpMessageConverter(objectMapper));
    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        /*
         * ============================================================================
         * 2ª FORMA - CONTENT NEGOTIATION VIA HEADER (Accept)
         * ============================================================================
         *
         * Método HTTP:
         *      GET
         *
         * Endpoint:
         *      http://localhost:8080/person/v2/4
         *
         * No postman : como configurar
         * 1º- GET http://localhost:8080/person/v2/4
         * 2ª - Opção headers
         * Header HTTP: -> postman -> header(key: Accept, value: application/json ou xml
         *      Accept: application/json
         *
         * ou
         *
         *      Accept: application/xml
         *
         * ----------------------------------------------------------------------------
         * Exemplos de requisição:
         *
         * GET http://localhost:8080/person/v2/4
         * Accept: application/json
         *
         * GET http://localhost:8080/person/v2/4
         * Accept: application/xml
         *
         * ----------------------------------------------------------------------------
         * Nesta configuração, o Spring ignora os parâmetros da URL e utiliza
         * exclusivamente o cabeçalho HTTP "Accept" enviado pelo cliente para
         * determinar o formato da resposta.
         *
         * Se nenhum Header "Accept" for informado, o Spring retornará JSON,
         * pois ele foi definido como o tipo padrão através de:
         *
         *      .defaultContentType(MediaType.APPLICATION_JSON)
         *
         * ============================================================================
         */

        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("yaml", MediaType.parseMediaType(CustomMediaType.APPLICATION_YAML_VALUE));

        /*
        // =========================================================================
        // 1ª FORMA - CONTENT NEGOTIATION VIA QUERY PARAM
        // Exemplo:
        // GET http://localhost:8080/person/v2/4?mediaType=xml
        // GET http://localhost:8080/person/v2/4?mediaType=json
        // =========================================================================

        configurer
                .favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(true)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
        */
    }
}