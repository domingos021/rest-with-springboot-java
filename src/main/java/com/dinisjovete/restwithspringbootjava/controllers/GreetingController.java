package com.dinisjovete.restwithspringbootjava.controllers;

import com.dinisjovete.restwithspringbootjava.data_model.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    // Modelo da mensagem que será retornada ao cliente.
    // %s é um placeholder que será substituído pelo nome recebido.
    private static final String template = "Hello, %s!";

    // Gerador de IDs sequenciais (thread-safe).
    // Cada chamada de incrementAndGet() retorna um novo valor.
    private final AtomicLong counter = new AtomicLong();

    // Exemplo de acesso:
    // http://localhost:8080/greeting?name=Dionis
    //
    // Endpoint que responde à URL "/greeting".
    @RequestMapping("/greeting")
    public Greeting greeting(

            // Lê o parâmetro "name" enviado na URL.
            // Se o parâmetro não for informado, será utilizado "World".
            @RequestParam(value = "name", defaultValue = " Hello World") String name) {

        // Cria um objeto Greeting contendo:
        // 1. Um ID sequencial gerado pelo AtomicLong.
        // 2. A mensagem formatada com o nome recebido na requisição.
        return new Greeting(
                counter.incrementAndGet(),
                String.format(template, name)
        );
    }
}