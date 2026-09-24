package com.dinisjovete.restwithspringbootjava.mock;

import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PersonMock {

    /*
     * ============================================================================
     * MOCK DE DADOS
     * ============================================================================
     * Classe responsável por simular uma fonte de dados temporária.
     *
     * Neste momento, os dados ficam armazenados em memória através de uma List.
     *
     * Futuramente será substituída por:
     *
     * PersonRepository -> Banco de Dados
     *
     * ============================================================================
     */


    private static final AtomicLong counter = new AtomicLong();


    /*
     * ============================================================================
     * BANCO DE DADOS SIMULADO
     * ============================================================================
     * Esta lista representa uma tabela temporária em memória.
     *
     * Os dados permanecem disponíveis enquanto a aplicação estiver executando.
     * ============================================================================
     */
    private static final List<Person> persons = new ArrayList<>();


    /*
     * ============================================================================
     * BLOCO DE INICIALIZAÇÃO
     * ============================================================================
     * Executado uma única vez quando a classe é carregada.
     *
     * Simula registros existentes no banco de dados.
     * ============================================================================
     */
    static {

        persons.add(createPerson(
                "Dinis",
                "Jovete",
                "Luanda - Angola",
                "Male"
        ));

        persons.add(createPerson(
                "João",
                "Silva",
                "Lisboa - Portugal",
                "Male"
        ));

        persons.add(createPerson(
                "Maria",
                "Santos",
                "São Paulo - Brasil",
                "Female"
        ));

        persons.add(createPerson(
                "Ana",
                "Costa",
                "Maputo - Moçambique",
                "Female"
        ));

        persons.add(createPerson(
                "Carlos",
                "Mendes",
                "Porto - Portugal",
                "Male"
        ));
    }





    /*
     * ============================================================================
     * FIND ALL
     * ============================================================================
     * Simula:
     *
     * SELECT * FROM person;
     *
     * Retorna todos os registros existentes.
     * ============================================================================
     */
    public static List<Person> findAll() {
        return persons;
    }


    /*
     * ============================================================================
     * FIND BY ID
     * ============================================================================
     * Simula:
     *
     * SELECT * FROM person WHERE id = ?
     *
     * Procura um registro dentro da lista simulada.
     * ============================================================================
     */
    public static Person findById(Long id) {

        return persons.stream()
                .filter(person -> person.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /*
     * ============================================================================
     * CREATE
     * ============================================================================
     * Simula:
     *
     * INSERT INTO person VALUES (...);
     *
     * Recebe uma nova Person, gera o ID e adiciona na lista em memória.
     * ============================================================================
     */
    public static Person create(Person person) {
        person.setId(counter.incrementAndGet());
        persons.add(person);
        return person;
    }

    /*
     * ============================================================================
     * UPDATE
     * ============================================================================
     * Simula:
     *
     * UPDATE person
     * SET campos = valores
     * WHERE id = ?
     *
     * Procura uma pessoa existente na lista em memória e atualiza seus dados.
     *
     * ============================================================================
     */
    public static Person update(Long id, Person personUpdate) {

        for (Person person : persons) {

            if (person.getId().equals(id)) {

                person.setFirstName(personUpdate.getFirstName());
                person.setLastName(personUpdate.getLastName());
                person.setAddress(personUpdate.getAddress());
                person.setGender(personUpdate.getGender());

                return person;
            }
        }

        return null;
    }



    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Simula:
     *
     * DELETE FROM person WHERE id = ?
     *
     * Remove uma pessoa da lista em memória através do ID informado.
     *
     * ============================================================================
     */
    public static void delete(Long id) {

        persons.removeIf(person -> person.getId().equals(id));

    }


    /*
     * ============================================================================
     * MÉTODO AUXILIAR
     * ============================================================================
     * Responsável apenas por criar um objeto Person.
     * ============================================================================
     */
    private static Person createPerson(String firstName,
                                       String lastName,
                                       String address,
                                       String gender) {

        Person person = new Person();

        person.setId(counter.incrementAndGet());
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setAddress(address);
        person.setGender(gender);

        return person;
    }
}

/*
 * ============================================================================
 * MAPA DE ORGANIZAÇÃO DA CLASSE: PersonMock
 * ============================================================================
 *
 * A classe está organizada seguindo uma ordem de responsabilidades:
 *
 *
 * 1. DECLARAÇÕES DA CLASSE
 *    |
 *    ├── AtomicLong counter
 *    |       -> Responsável por gerar IDs sequenciais simulando o banco.
 *    |
 *    └── List<Person> persons
 *            -> Representa uma tabela temporária em memória.
 *
 *
 * 2. INICIALIZAÇÃO DOS DADOS
 *    |
 *    └── static { ... }
 *            -> Executado uma única vez quando a classe é carregada.
 *            -> Simula registros já existentes no banco de dados.
 *
 *
 * 3. MÉTODOS PÚBLICOS (FUNCIONALIDADES)
 *    |
 *    ├── findAll()
 *    |       -> Simula:
 *    |          SELECT * FROM person;
 *    |
 *    └── findById()
 *            -> Simula:
 *               SELECT * FROM person WHERE id = ?;
 *
 *
 * 4. MÉTODOS AUXILIARES PRIVADOS
 *    |
 *    └── createPerson()
 *            -> Método interno responsável apenas por criar objetos Person.
 *            -> Evita repetição de código.
 *
 *
 * FLUXO GERAL:
 *
 *      PersonService
 *            |
 *            ↓
 *      PersonMock
 *            |
 *            ↓
 *      List<Person> (Banco simulado em memória)
 *
 *
 * FUTURO:
 *
 *      PersonService
 *            |
 *            ↓
 *      PersonRepository
 *            |
 *            ↓
 *      Banco de Dados
 *
 * ============================================================================
 */