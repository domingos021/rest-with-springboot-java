package com.dinisjovete.restwithspringbootjava.config.mediatypes;

/*
 * ============================================================================
 * CUSTOM MEDIA TYPES
 * ============================================================================
 *
 * Classe responsável por centralizar Media Types personalizados utilizados
 * pela aplicação.
 *
 * O Spring já possui constantes prontas para formatos comuns:
 *
 *      MediaType.APPLICATION_JSON_VALUE
 *      MediaType.APPLICATION_XML_VALUE
 *
 * Porém, formatos adicionais como YAML precisam ser definidos manualmente.
 *
 * Centralizar esses valores evita duplicação e possíveis erros de digitação.
 *
 * Exemplo:
 *
 *      "application/yaml"
 *
 * Caso no futuro o formato precise mudar para:
 *
 *      "application/x-yaml"
 *
 * basta alterar em um único lugar.
 *
 * ============================================================================
 */

public final class CustomMediaType {

    /*
     * Impede criação de objetos desta classe.
     *
     * Esta classe existe apenas para armazenar constantes.
     */
    private CustomMediaType() {
    }


    /*
     * Media Type utilizado para comunicação em formato YAML.
     */
    public static final String APPLICATION_YAML_VALUE = "application/yaml";

}

/*
Cliente
 |
 | Accept: application/yaml
 |
 v
Controller
 |
 | produces = application/yaml
 |
 v
Spring MVC
 |
 | procura converter compatível
 |
 v
YamlJackson2HttpMessageConverter
 |
 | YAMLMapper
 |
 v
Resposta YAML

 */