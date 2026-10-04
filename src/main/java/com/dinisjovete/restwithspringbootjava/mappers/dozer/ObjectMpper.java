package com.dinisjovete.restwithspringbootjava.mappers.dozer;

/*
 * ========================================================================
 * CLASSE UTILITÁRIA DE MAPEAMENTO (ObjectMapper)
 * ========================================================================
 * Objetivo: Centralizar e reutilizar a lógica de conversão de objetos
 * (Entidades para DTOs e vice-versa) em toda a aplicação. Por ser uma
 * classe utilitária sem estado, seus métodos são estáticos e genéricos,
 * permitindo mapear instâncias únicas ou listas de forma rápida e segura
 * sem a necessidade de instanciar a classe.
 */

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ObjectMpper {

    /*
     * Instância do Dozer (biblioteca externa, fora do Spring) responsável
     * por gerenciar o mapeamento e a conversão dos dados entre os objetos.
     */
    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    /*
     * TIPOS GENÉRICOS (Generics)
     *
     * Generics permitem criar classes, métodos e interfaces
     * que trabalham com diferentes tipos de dados de forma segura.
     *
     * Os tipos dentro de <> são chamados de parâmetros de tipo.
     *
     * OG = Object Generic (nome escolhido pelo programador)
     *      Representa o tipo genérico do objeto de origem (source).
     *      É o objeto que será convertido pelo mapper.
     *
     * DT = Destination Type (nome escolhido pelo programador)
     *      Representa o tipo genérico do objeto de destino.
     *      É a classe que receberá os dados convertidos.
     *
     * Fluxo:
     *
     * Objeto origem (OG)
     *          |
     *          ↓
     *    Dozer Mapper
     *          |
     *          ↓
     * Objeto destino (DT)
     *
     * Exemplo:
     *
     * Person (OG)
     *          |
     *          ↓
     * mapper.map()
     *          |
     *          ↓
     * PersonDTO (DT)
     *
     * O Java identifica automaticamente os tipos no momento
     * em que o método é chamado.
     */
    public static <OG, DT> DT parseObject(OG source, Class<DT> destination) {
        return mapper.map(source, destination);
    }

    public static <OG, DT> List<DT> parseListObject(List<OG> source, Class<DT> destination) {
        List<DT> destinationObject = new ArrayList<DT>();

        /*
         * AS DUAS VERSÕES ABAIXO FAZEM A MESMA COISA:
         *
         * - Recebem uma lista de objetos de origem (List<OG>).
         * - Percorrem cada objeto da lista.
         * - Utilizam o mapper para converter cada objeto OG em DT.
         * - Retornam uma nova lista contendo os objetos convertidos (List<DT>).
         *
         * Diferença:
         *
         * Versão 1:
         * Utiliza estrutura tradicional de repetição (for).
         * Deixa o passo a passo mais explícito.
         *
         * Versão 2:
         * Utiliza Stream API.
         * Representa o mesmo fluxo de transformação de forma mais declarativa.
         */
        //===================  versão 1ª  {FOR EACH} ==========================
        /*
         * Para cada objeto (obj) do tipo OG dentro da lista source,
         * pega esse objeto da origem e mapeia para o tipo de destino (DT)
         * usando o mapper.
         *
         * Após a conversão, o objeto transformado é adicionado na lista
         * destinationList, que armazenará todos os objetos convertidos.
         *
         * Em outras palavras:
         *
         * O mapper pega os dados da fonte (source - List<OG>),
         * transforma cada objeto para o tipo de destino (DT),
         * e adiciona o resultado na lista de destino (List<DT>).
         *
         * A transformação acontece aqui:
         * -> mapper.map(obj, destination)
         */
        for (OG obj : source) {
            destinationObject.add(mapper.map(obj, destination));
        }

        return destinationObject;
    }


    //===================  versão 2ª  {STREAM} ==========================
    public static <OG, DT> List<DT> parseListObjectStream(
            List<OG> source,
            Class<DT> destination
    ) {

        /*
         * O Stream executa o mesmo fluxo da versão com FOR:
         *
         * 1 - Cria um fluxo a partir da lista source.
         * 2 - Percorre cada objeto OG da lista.
         * 3 - Usa o mapper para converter cada objeto OG em DT.
         * 4 - Coleta os resultados convertidos em uma nova lista List<DT>.
         *
         * Em outras palavras:
         *
         * List<OG>
         *     |
         *     ↓
         * stream()
         *     |
         *     ↓
         * mapper.map(obj, destination)
         *     |
         *     ↓
         * List<DT>
         *
         * A transformação acontece aqui:
         * -> mapper.map(obj, destination)
         */
        return source.stream()
                .map(obj -> mapper.map(obj, destination))
                .toList();
    }

    // Método criado como alias (com 's') para atender chamadas de testes que utilizam parseListObjects
    public static <OG, DT> List<DT> parseListObjects(List<OG> source, Class<DT> destination) {
        return parseListObjectStream(source, destination);
    }

}

/*
 * FLUXO DE CONVERSÃO
 *
 * Entity (origem) -> recebe (OG) -> (do tipo person)
 *      |
 *      ↓
 * Mapper (responsável pela conversão dos dados)-> e converte para
 *      |
 *      ↓
 * DTO (destino)
 *
 * A Entity representa o objeto de domínio persistido no banco.
 * O Mapper transforma a Entity em um DTO, que será utilizado
 * para transportar os dados para outras camadas da aplicação,
 * como Controllers e APIs REST.
 */