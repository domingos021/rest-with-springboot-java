package com.dinisjovete.restwithspringbootjava.config.web_config;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

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


/*
 * =============================================================================
 * CONFIGURAÇÃO WEB - CONTENT NEGOTIATION
 * =============================================================================
 *
 * Classe responsável por personalizar configurações relacionadas ao Spring MVC,
 * implementando a interface WebMvcConfigurer.
 *
 * O principal objetivo desta classe é configurar como a API irá negociar o
 * formato dos dados enviados na resposta para o cliente.
 *
 * -----------------------------------------------------------------------------
 * CONCEITO:
 *
 * Content Negotiation (negociação de conteúdo) é o mecanismo utilizado pelo
 * HTTP para permitir que cliente e servidor definam qual formato de dados será
 * utilizado na comunicação.
 *
 * O cliente informa através do Header HTTP "Accept" qual representação deseja
 * receber, e o Spring verifica se existe um conversor compatível para gerar
 * aquela resposta.
 *
 * -----------------------------------------------------------------------------
 *
 * EXEMPLO DO FLUXO:
 *
 * Cliente (Postman / Front-end / Mobile)
 *
 *        |
 *        |
 *        |  GET /person/v2/4
 *        |  Accept: application/json
 *        |
 *        v
 *
 * Spring Boot
 *
 *        |
 *        | Analisa o Header Accept
 *        |
 *        | Encontra o formato solicitado
 *        |
 *        v
 *
 * Resposta:
 *
 *        Content-Type: application/json
 *
 *        {
 *            "id": 4,
 *            "firstName": "Nikola",
 *            "lastName": "Tesla"
 *        }
 *
 *
 * =============================================================================
 * EXEMPLO 1 - SOLICITANDO JSON
 * =============================================================================
 *
 * Requisição HTTP:
 *
 * GET http://localhost:8080/person/v2/4 HTTP/1.1
 * Host: localhost:8080
 * Accept: application/json
 *
 *
 * Resposta HTTP:
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/json
 *
 * {
 *     "id": 4,
 *     "firstName": "Nikola",
 *     "lastName": "Tesla"
 * }
 *
 *
 * =============================================================================
 * EXEMPLO 2 - SOLICITANDO XML
 * =============================================================================
 *
 * Requisição HTTP:
 *
 * GET http://localhost:8080/person/v2/4 HTTP/1.1
 * Host: localhost:8080
 * Accept: application/xml
 *
 *
 * Resposta HTTP:
 *
 * HTTP/1.1 200 OK
 * Content-Type: application/xml
 *
 * <Person>
 *      <id>4</id>
 *      <firstName>Nikola</firstName>
 *      <lastName>Tesla</lastName>
 * </Person>
 *
 *
 * =============================================================================
 * CONFIGURAÇÃO NO POSTMAN
 * =============================================================================
 *
 * Método:
 *
 *      GET
 *
 * URL:
 *
 *      http://localhost:8080/person/v2/4
 *
 *
 * Aba Headers:
 *
 *      KEY              VALUE
 *
 *      Accept           application/json
 *
 * ou
 *
 *      Accept           application/xml
 *
 *
 * =============================================================================
 * FORMATO YAML (EXEMPLO DE REPRESENTAÇÃO)
 * =============================================================================
 *
 * Embora YAML não seja utilizado diretamente nesta configuração, ele é outro
 * formato de representação de dados muito usado em arquivos de configuração,
 * como Docker, Kubernetes e ferramentas DevOps.
 *
 * Exemplo:
 *
 * person:
 *   id: 4
 *   firstName: Nikola
 *   lastName: Tesla
 *
 *
 * =============================================================================
 * OBSERVAÇÃO:
 *
 * Caso o cliente não informe o Header Accept, o Spring utilizará o formato
 * definido como padrão através de:
 *
 *      .defaultContentType(MediaType.APPLICATION_JSON)
 *
 * Portanto, a API responderá em JSON por padrão.
 *
 * =============================================================================
 */