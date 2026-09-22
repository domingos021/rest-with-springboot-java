package com.dinisjovete.restwithspringbootjava.data_model.entities;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;

    public Person() {}

    public Person(Long id, String firstName, String lastName, String address, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        // Compara apenas pelo ID único do banco
        return id != null && id.equals(person.getId());
    }

    @Override
    public int hashCode() {
        // Retorna um valor fixo ou baseado no ID para evitar que mude se o objeto for alterado
        return getClass().hashCode();
    }
}

/*
 * ==========================================================================
 * CONCEITO: O QUE É O `Serializable`?
 * ==========================================================================
 *
 * 1. O QUE SIGNIFICA?
 *    `Serializable` é uma interface de marcação do Java (ela não possui nenhum
 *    método obrigatório para implementar). Quando uma classe implementa `Serializable`
 *    (ex: `public class MinhaClasse implements Serializable`), ela está dizendo ao Java:
 *    "Os objetos desta classe podem ser transformados em uma sequência de bytes".
 *
 * 2. PARA QUE SERVE? (Serialização e Desserialização)
 *    - **Serialização:** É o processo de converter o estado atual de um objeto em
 *      bytes. Isso é usado para:
 *        • Salvar o objeto em um arquivo no disco (persistência).
 *        • Enviar o objeto através de uma rede (ex: microsserviços conversando).
 *        • Guardar o objeto em uma sessão ou cache distribuído (como Redis).
 *    - **Desserialização:** É o processo inverso, onde pegamos essa sequência de
 *      bytes e reconstruímos o objeto original na memória.
 *
 * 3. NO CONTEXTO DO SPRING BOOT E EXCEÇÕES:
 *    Muitas vezes você verá exceptions personalizadas (como a sua
 *    `UnsupportedMathOperationException`) implementando `Serializable`.
 *    Isso acontece porque, se uma exceção precisar ser transportada pela rede
 *    (por exemplo, em arquiteturas distribuídas ou quando o servidor precisa
 *    serializar o erro para JSON/Binary), o Java precisa garantir que o objeto
 *    de erro possa ser convertido em bytes sem perder informações.
 * ==========================================================================
 */
