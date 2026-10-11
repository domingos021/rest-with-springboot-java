package com.dinisjovete.restwithspringbootjava.apenas_estudo_filtro.jackson.controller;

import com.dinisjovete.restwithspringbootjava.apenas_estudo_filtro.jackson.filter.SensitiveData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


// ============================================================================
// CONTROLLER DE TESTE - JACKSON FILTER
// ============================================================================
//
// Objetivo:
// Simular uma resposta de API contendo dados sensíveis.
//
// O controller retorna o objeto completo com todos os campos.
// A filtragem acontece depois, durante a conversão:
//
// Java Object  --->  JSON Response
//
// Responsável pela filtragem:
// ObjectMapper + SimpleBeanPropertyFilter
//
//http://localhost:8080/teste/filter
// ============================================================================

@RestController
public class FilterController {

// o metodo retorna um objeto do tipo sensitiveData, que possui campos sensíveis como senha. A filtragem será aplicada posteriormente, durante a serialização para JSON.
    @GetMapping("/teste/filter")
    public SensitiveData testeFilter() {

        return new SensitiveData(
                1L,
                "Diniz",
                "diniz@email.com",
                "123456"
        );
    }
}