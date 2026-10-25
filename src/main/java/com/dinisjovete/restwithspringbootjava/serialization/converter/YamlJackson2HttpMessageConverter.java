/*
 * =============================================================================
 * YAML JACKSON HTTP MESSAGE CONVERTER - CONVERSOR DE RESPOSTAS EM YAML
 * =============================================================================
 *
 * OBJETIVO:
 *
 * Ensinar o Spring MVC a converter objetos Java (DTOs) em YAML e a ler YAML
 * recebido, para que a API responda em application/yaml além de JSON e XML.
 *
 * -----------------------------------------------------------------------------
 * QUEM USA ESTA CLASSE:
 *
 * Apenas o WebConfig, que cria uma instância dela em extendMessageConverters()
 * e a registra no Spring MVC. Nenhum Controller, Service ou Repository a chama
 * diretamente.
 *
 * Quem sente o efeito dela são os endpoints que devolvem DTOs
 * (/book, /person/v1, /person/v2), sempre que o cliente enviar o header
 * "Accept: application/yaml".
 *
 * -----------------------------------------------------------------------------
 * COMO FUNCIONA:
 *
 * Em vez de criar um YAMLMapper do zero (que nasce sem os módulos de data e
 * sem as configurações do Spring Boot), ela recebe o ObjectMapper já
 * configurado pelo Boot e gera uma CÓPIA dele trocando apenas a fábrica de
 * formato para YAML (copyWith(new YAMLFactory())).
 *
 * Resultado: JSON e YAML seguem as mesmas regras de serialização.
 * Exemplo: LocalDateTime sai como "1916-01-01T00:00:00" nos dois formatos,
 * e não como lista de números.
 *
 * -----------------------------------------------------------------------------
 * FLUXO:
 *
 *      Cliente  --  GET /book/1   Accept: application/yaml
 *          |
 *          v
 *      Spring MVC (escolhe o conversor compatível com application/yaml)
 *          |
 *          v
 *      YamlJackson2HttpMessageConverter
 *          |  usa a cópia do ObjectMapper com YAMLFactory
 *          v
 *      Resposta em YAML
 *
 * -----------------------------------------------------------------------------
 * DETALHES IMPORTANTES:
 *
 * - NON_NULL: campos com valor null não aparecem no YAML. Como a configuração
 *   é feita na CÓPIA, o ObjectMapper do JSON não é afetado.
 * - MediaType: usa CustomMediaType.APPLICATION_YAML_VALUE ("application/yaml"),
 *   o que permite ao Spring reconhecer este conversor para esse formato.
 * - O registro é feito no WebConfig. Criar esta classe sozinha não basta.
 * - Depende de o ObjectMapperConfig (config.somente_estudo) continuar
 *   desativado: se ele voltar com "new ObjectMapper()", a cópia herdaria um
 *   mapper sem módulo de datas e o erro de LocalDateTime voltaria.
 * =============================================================================
 */


package com.dinisjovete.restwithspringbootjava.serialization.converter;

import com.dinisjovete.restwithspringbootjava.config.mediatypes.CustomMediaType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

/*
 * Conversor de mensagens HTTP para YAML (application/yaml).
 *
 * Em vez de criar um YAMLMapper do zero, ele recebe o ObjectMapper já
 * configurado pelo Spring Boot (módulos de data, features, etc.) e gera uma
 * CÓPIA dele trocando apenas o formato para YAML. Assim JSON e YAML seguem
 * as mesmas regras de serialização.
 *
 * O registro no Spring MVC é feito em WebConfig.extendMessageConverters().
 */
public final class YamlJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public YamlJackson2HttpMessageConverter(ObjectMapper baseMapper) {

        super(
                // copyWith: cópia independente do mapper do Boot, só com a fábrica YAML.
                // O NON_NULL vale apenas para o YAML e não altera o mapper do JSON.
                baseMapper.copyWith(new YAMLFactory())
                        .setSerializationInclusion(JsonInclude.Include.NON_NULL),

                MediaType.parseMediaType(CustomMediaType.APPLICATION_YAML_VALUE)
        );
    }
}