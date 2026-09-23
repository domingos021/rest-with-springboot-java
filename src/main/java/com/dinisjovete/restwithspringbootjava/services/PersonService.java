package com.dinisjovete.restwithspringbootjava.services;

import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class PersonService {
    // Controla IDs sequenciais de forma segura entre múltiplas threads (simulando um banco de dados)
    private final AtomicLong counter = new AtomicLong();
    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger = Logger.getLogger(PersonService.class.getName());

    //ME TODOS PARA REQUISIÇÕES
    public Person findById(String id) {
        logger.info("finding one Person");
        // ============================================================================
// MOCK
// ============================================================================
// Mock utilizado apenas para simular a inserção (set) de dados durante os testes.
// Neste momento os dados são criados manualmente na aplicação.
// Futuramente este código será substituído por uma chamada ao banco de dados.
// ============================================================================

        Person person = new Person(); // Instanciamos um novo objeto da classe Person.

        person.setId(counter.incrementAndGet());
        person.setFirstName("Dinis");
        person.setLastName("Jovete");
        person.setAddress("Luanda - Angola");
        person.setGender("Male");

// ------------------------- FIM DO MOCK -------------------------

        /*
         * CONCEITO
         * --------------------------------------------------------------------------
         * A variável "person" é uma referência (reference) para o objeto criado com
         * o operador "new".
         *
         * Em outras palavras:
         *
         *      Person person = new Person();
         *
         *      Person  -> tipo da referência.
         *      person  -> variável que armazena a referência (endereço lógico) do objeto.
         *      new Person() -> cria um novo objeto na memória (Heap).
         *
         * Sempre que utilizamos:
         *
         *      person.setFirstName(...);
         *
         * estamos acessando esse objeto por meio da referência armazenada na variável
         * "person".
         */

        return person;
    }
}







