package com.dinisjovete.restwithspringbootjava.apenas_estudo_filtro.jackson.filter;


import com.fasterxml.jackson.annotation.JsonFilter;

// ============================================================================
// CLASSE DE ESTUDO - JACKSON FILTER
// ============================================================================
//
// Objetivo:
// Representar um objeto que possui informações sensíveis.
//
// Nesta classe vamos simular um cenário onde alguns dados existem no objeto,
// mas não devem aparecer no JSON final.
//
// ============================================================================
@JsonFilter("sensitiveDataFilter")
public class SensitiveData {

    private Long id;

    private String name;

    private String email;

    private String password;


    public SensitiveData() {
    }


    public SensitiveData(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }


    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }
}
