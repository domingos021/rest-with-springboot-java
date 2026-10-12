package com.dinisjovete.restwithspringbootjava.serialization.converter;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

/*
 * =============================================================================
 * FUNCIONAMENTO EM TEMPO DE EXECUÇÃO
 * =============================================================================
 *
 * Este converter não é chamado diretamente por nenhum código da aplicação.
 *
 * Nenhum Controller, Service ou Repository possui uma chamada explícita para:
 *
 *      new YamlJackson2HttpMessageConverter()
 *
 * ou para algum método desta classe.
 *
 * -----------------------------------------------------------------------------
 * O Spring MVC gerencia este componente internamente.
 *
 * Quando a aplicação inicia, o Spring registra os HttpMessageConverters
 * disponíveis no contexto da aplicação.
 *
 * Durante uma requisição HTTP, o Spring analisa o Header "Accept" enviado pelo
 * cliente e verifica qual converter é capaz de produzir aquele formato.
 *
 * Exemplo:
 *
 * Requisição:
 *
 *      GET http://localhost:8080/person/v1/4
 *
 *      Accept: application/yaml
 *
 *
 * Fluxo:
 *
 *      Controller
 *          |
 *          | retorna PersonDTO
 *          |
 *          v
 *      Spring MVC
 *          |
 *          | analisa o MediaType solicitado
 *          |
 *          v
 *      YamlJackson2HttpMessageConverter
 *          |
 *          | utiliza YAMLMapper
 *          |
 *          v
 *      Resposta YAML
 *
 *
 * -----------------------------------------------------------------------------
 * Portanto, este converter funciona através do mecanismo de infraestrutura do
 * Spring, sendo acionado automaticamente apenas quando a resposta solicitada
 * pelo cliente for compatível com "application/yaml".
 *
 * Para respostas JSON ou XML, outros HttpMessageConverters serão utilizados.
 *
 * =============================================================================
 */

public final class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlJackson2HttpMessageConverter() {

        super(new YAMLMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL),
                /*
                  * CustomMediaType.APPLICATION_YAML_VALUE é uma constante definida na classe CustomMediaType
                  * que representa o Media Type "application/yaml". Isso permite que o Spring
                  * reconheça este conversor como capaz de produzir respostas nesse formato.
                 */
                MediaType.parseMediaType(CustomMediaType.APPLICATION_YAML_VALUE)
        );


    }


}

/*
 * =============================================================================
 * YAML JACKSON HTTP MESSAGE CONVERTER
 * =============================================================================
 *
 * Esta classe cria um conversor personalizado de mensagens HTTP para o formato
 * YAML utilizando o YAMLMapper do Jackson.
 *
 * O objetivo desta classe é ensinar ao Spring MVC como converter objetos Java
 * (DTOs, Entities, etc.) para uma representação YAML na resposta HTTP da API.
 *
 * Por padrão, o Spring Boot já possui conversores para formatos como JSON e XML.
 * Este converter adiciona suporte para YAML.
 *
 * -----------------------------------------------------------------------------
 * CONCEITO:
 *
 * Um HttpMessageConverter é responsável por realizar a conversão entre:
 *
 *      Objeto Java
 *           |
 *           |
 *           v
 *      Representação HTTP
 *
 * Exemplo:
 *
 *      PersonDTO
 *
 *           |
 *           |
 *           v
 *
 *      JSON / XML / YAML
 *
 *
 * =============================================================================
 * FLUXO DA REQUISIÇÃO
 * =============================================================================
 *
 * Cliente (Postman / Front-end / Mobile)
 *
 *        |
 *        |
 *        | GET http://localhost:8080/person/v1/4
 *        |
 *        | Header:
 *        | Accept: application/yaml
 *        |
 *        v
 *
 * Controller
 *
 *        |
 *        |
 *        | Retorna um objeto PersonDTO
 *        |
 *        v
 *
 * HttpMessageConverter
 *
 *        |
 *        |
 *        | Identifica que o cliente solicitou YAML
 *        |
 *        v
 *
 * YamlJackson2HttpMessageConverter
 *
 *        |
 *        |
 *        | Utiliza o YAMLMapper do Jackson
 *        |
 *        v
 *
 * Resposta HTTP YAML
 *
 *        |
 *        |
 *        v
 *
 * Content-Type: application/yaml
 *
 * {
 *      id: 4,
 *      firstName: Nikola,
 *      lastName: Tesla
 * }
 *
 *
 * =============================================================================
 * FUNCIONAMENTO INTERNO
 * =============================================================================
 *
 * O Spring utiliza o Jackson para converter objetos Java em diferentes formatos.
 *
 * Conversor padrão:
 *
 *      PersonDTO
 *          |
 *          v
 *      ObjectMapper
 *          |
 *          v
 *      JSON
 *
 *
 * Este conversor personalizado:
 *
 *      PersonDTO
 *          |
 *          v
 *      YAMLMapper
 *          |
 *          v
 *      YAML
 *
 *
 * =============================================================================
 * YAMLMapper
 * =============================================================================
 *
 * O YAMLMapper é uma implementação do Jackson responsável por serializar e
 * desserializar objetos utilizando a estrutura YAML.
 *
 * Exemplo:
 *
 * Objeto Java:
 *
 * PersonDTO {
 *      id = 4,
 *      firstName = "Nikola",
 *      lastName = "Tesla"
 * }
 *
 *
 * Resultado YAML:
 *
 * id: 4
 * firstName: Nikola
 * lastName: Tesla
 *
 *
 * =============================================================================
 * JsonInclude.Include.NON_NULL
 * =============================================================================
 *
 * Configuração utilizada para remover propriedades que possuem valor null
 * durante a conversão.
 *
 * Exemplo:
 *
 * Sem NON_NULL:
 *
 * id: 4
 * firstName: Nikola
 * phone: null
 *
 *
 * Com NON_NULL:
 *
 * id: 4
 * firstName: Nikola
 *
 *
 * =============================================================================
 * MediaType
 * =============================================================================
 *
 * Define qual tipo de conteúdo HTTP este conversor suporta.
 *
 * Neste caso:
 *
 *      application/yaml
 *
 *
 * Exemplo de requisição:
 *
 * GET http://localhost:8080/person/v1/4
 *
 * Header:
 *
 *      Accept: application/yaml
 *
 *
 * Resposta:
 *
 *      HTTP/1.1 200 OK
 *
 *      Content-Type: application/yaml
 *
 *
 * =============================================================================
 * OBSERVAÇÃO
 * =============================================================================
 *
 * Criar esta classe não registra automaticamente o converter no Spring MVC.
 *
 * É necessário adicioná-lo através da configuração:
 *
 *      WebMvcConfigurer
 *
 * utilizando:
 *
 *      extendMessageConverters()
 *
 *
 * Assim o Spring passa a conhecer este novo formato de resposta.
 *
 * =============================================================================
 */
