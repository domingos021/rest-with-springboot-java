package com.dinisjovete.restwithspringbootjava.data_model;

import java.io.Serializable;

public record Greeting(Long id, String content) {}
/*
 * ========================================================================
 * CLASS NORMAL vs RECORD (Java)
 * ========================================================================
 *
 * 1) CLASS NORMAL:
 * ------------------------------------------------------------------------
 * - É uma classe tradicional do Java.
 * - Permite criar atributos, construtores, getters, setters e métodos
 *   manualmente.
 * - Dá mais controle sobre o comportamento e o estado do objeto.
 * - É normalmente usada para objetos que possuem identidade e regras de
 *   negócio, como entidades do sistema.
 *
 * Exemplo:
 *
 * public class User {
 *     private String name;
 *     private String email;
 *
 *     public User(String name, String email) {
 *         this.name = name;
 *         this.email = email;
 *     }
 *
 *     public String getName() {
 *         return name;
 *     }
 * }
 *
 *
 * 2) RECORD:
 * ------------------------------------------------------------------------
 * - É uma forma mais simples e concisa de criar classes que servem apenas
 *   para transportar dados (dados imutáveis).
 * - O Java gera automaticamente:
 *       • construtor
 *       • getters (chamados de accessors)
 *       • equals()
 *       • hashCode()
 *       • toString()
 *
 * - Os atributos de um record são automaticamente:
 *       • private
 *       • final
 *
 * - Depois de criado, o valor dos campos não pode ser alterado.
 *
 * Exemplo:
 *
 * public record UserDTO(String name, String email) {
 * }
 *
 *
 * DIFERENÇA PRINCIPAL:
 * ------------------------------------------------------------------------
 *
 * CLASS:
 *     "Eu tenho um objeto com comportamento e regras."
 *
 *     Exemplo:
 *     User, Product, Order
 *
 *
 * RECORD:
 *     "Eu tenho apenas uma estrutura para transportar informações."
 *
 *     Exemplo:
 *     UserDTO, LoginResponseDTO, OrderDTO
 *
 *
 * No Spring Boot, é comum usar:
 *
 * Entity  -> class normal
 * DTO     -> record
 *
 * Porque a Entity representa um objeto do domínio, enquanto o DTO apenas
 * carrega dados entre camadas da aplicação.
 *
 * ========================================================================
 */