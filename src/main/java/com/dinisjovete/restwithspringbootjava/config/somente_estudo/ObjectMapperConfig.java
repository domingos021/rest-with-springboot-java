package com.dinisjovete.restwithspringbootjava.config.somente_estudo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ============================================================================
// CONFIGURAÇÃO DE FILTRO DE SERIALIZAÇÃO JSON - APENAS ESTUDO
// ============================================================================
//
// DESATIVADA: o @Bean abaixo substituía o ObjectMapper padrão do Spring Boot
// por um "new ObjectMapper()" sem o JavaTimeModule, o que quebrava a
// serialização de LocalDateTime (ex.: BookDTO.launchDate).
//
// Para reativar o estudo do Jackson Filter, descomente @Configuration e @Bean
// e registre o módulo de datas (ou use Jackson2ObjectMapperBuilderCustomizer).
// ============================================================================

//@Configuration
public class ObjectMapperConfig {

    //    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        SimpleFilterProvider filtros = new SimpleFilterProvider();

        filtros.addFilter(
                "sensitiveDataFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(
                        "password",
                        "name"
                )
        );

        mapper.setFilterProvider(filtros);

        return mapper;
    }
}