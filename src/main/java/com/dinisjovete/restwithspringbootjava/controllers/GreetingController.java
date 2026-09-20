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
    //
    /*
     * @RequestMapping aplicado no método
     *
     * Define o caminho específico deste endpoint.
     * Diferente do @RequestMapping aplicado na classe,
     * ele pertence somente a este método.
     *
     * Exemplo:
     *
     * @RequestMapping("/greeting")
     *
     * URL final:
     * http://localhost:8080/greeting
     */
    @RequestMapping("/greeting")
    public Greeting greeting(

            // @RequestParam captura um parâmetro enviado pela URL.
            //
            // Exemplo:
            // http://localhost:8080/greeting?name=Dionis
            //
            // Neste caso:
            // name = "Dionis"
            //
            // Caso o parâmetro "name" não seja informado,
            // será utilizado o valor padrão "Hello World".
            @RequestParam(value = "name", defaultValue = "Hello World") String name) {

        // Cria um objeto Greeting contendo:
        //
        // 1. Um ID sequencial gerado pelo AtomicLong.
        // 2. A mensagem formatada utilizando o nome recebido na requisição.
        return new Greeting(
                counter.incrementAndGet(),
                String.format(template, name)
        );
    }
}