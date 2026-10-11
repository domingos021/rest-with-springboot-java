package com.dinisjovete.restwithspringbootjava.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// ============================================================================
// CONFIGURAÇÃO DE FILTRO DE SERIALIZAÇÃO JSON - APENAS ESTUDO
// ============================================================================
//
// ATENÇÃO:
// Esta configuração NÃO faz parte da arquitetura principal da aplicação.
//
// Ela foi criada exclusivamente para fins de aprendizado sobre o funcionamento
// do Jackson Filter e como o Spring/Jackson controlam a serialização de objetos.
//
// Na arquitetura real deste projeto, o controle dos dados expostos pela API é
// realizado através de DTOs de saída (ex: PersonDTO e PersonDTOV2), utilizando
// mapeamento entre Entity -> DTO.
//
// Classes utilizadas neste estudo:
//
// 1) SensitiveData
//    Local:
//    apenas_estudo_filtro.jackson.filter.SensitiveData
//
//    Classe simulando um objeto com dados sensíveis que existem em memória,
//    mas que não devem aparecer na resposta JSON.
//
// 2) FilterController
//    Local:
//    apenas_estudo_filtro.jackson.controller.FilterController
//
//    Controller responsável apenas por retornar o objeto SensitiveData para
// demonstrar o funcionamento do filtro.
//
// ============================================================================
//
// Objetivo:
// Controlar dinamicamente quais propriedades de um objeto Java serão enviadas
// na resposta JSON.
//
// O filtro atua durante a serialização:
//
// Objeto Java  --->  JSON Response
//
// Permite ocultar campos sensíveis sem precisar criar outro DTO.
//
// Exemplo:
// Uma classe pode possuir senha, token ou informações internas,
// mas esses dados podem ser removidos antes da resposta ao cliente.
//
// Observação:
// Em APIs modernas com DTOs, normalmente o controle de exposição é feito
// através do próprio DTO. O Jackson Filter é mais comum quando precisamos
// de respostas dinâmicas ou quando trabalhamos com o mesmo objeto em vários
// contextos.
//
// Neste projeto, o uso do Jackson Filter serve apenas para entender o mecanismo
// interno de serialização do Jackson e comparar com a abordagem profissional
// baseada em DTOs.
// ============================================================================

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        SimpleFilterProvider filtros = new SimpleFilterProvider();

        filtros.addFilter(
                "sensitiveDataFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(
                         // esse filtro é aplicado à classe SensitiveData, que possui campos sensíveis como senha.
                        // Durante a serialização para JSON, o campo "password" será removido da resposta final.
                        // Campos adicionados aqui serão removidos do JSON final
                        // durante o processo de serialização.
                        "password",
                        "name"

                )
        );

        mapper.setFilterProvider(filtros);

        return mapper;
    }
}