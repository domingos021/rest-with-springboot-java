package com.dinisjovete.restwithspringbootjava.services;

import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.mock.PersonMock;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service // Marca a classe como um Bean gerenciado pelo Spring (Injeção de Dependência)
public class PersonService {

    // Registry logs (mensagens de erro, avisos e informações) no console da aplicação
    private static final Logger logger =
            Logger.getLogger(PersonService.class.getName());


    // MÉTODOS PARA REQUISIÇÕES

    public Person findById(String id) {

        logger.info("finding one Person");


        // ============================================================================
        // MOCK
        // ============================================================================
        // Os dados de teste estão separados na classe PersonMock.
        //
        // O Service não conhece como os dados são armazenados.
        // Ele apenas solicita a informação ao Mock.
        //
        // Neste momento:
        //
        // PersonService -> PersonMock -> Lista em memória
        //
        // Futuramente:
        //
        // PersonService -> PersonRepository -> Banco de Dados
        //
        // ============================================================================


        return PersonMock.findById(Long.parseLong(id));


        /*
         * CONCEITO
         * --------------------------------------------------------------------------
         * O Service possui a responsabilidade de controlar o fluxo da aplicação.
         *
         * Ele não cria objetos Person e não manipula diretamente a fonte de dados.
         *
         * Responsabilidades:
         *
         *      Controller  -> recebe requisições HTTP
         *
         *      Service    -> regras de negócio
         *
         *      Mock       -> dados temporários para teste
         *
         *      Repository -> acesso ao banco de dados (futuro)
         *
         * --------------------------------------------------------------------------
         */
    }


    public List<Person> findAll() {

        logger.info("finding all Persons");


        // ============================================================================
        // MOCK
        // ============================================================================
        // Simula uma consulta:
        //
        // SELECT * FROM person;
        //
        // Futuramente será:
        //
        // personRepository.findAll();
        //
        // ============================================================================


        return PersonMock.findAll();
    }

    public Person create(Person person) {

        logger.info("creating a new Person");

        return PersonMock.create(person);
        //personRepository.save(person);

    }


    /*
     * ============================================================================
     * UPDATE
     * ============================================================================
     * Atualiza os dados de uma pessoa existente.
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * PersonService.update()
     *      |
     *      ↓
     * PersonMock.update()
     *      |
     *      ↓
     * Lista em memória
     *
     * Futuramente será substituído por:
     *
     * personRepository.save(person);
     *
     * ============================================================================
     */
    public Person update(String id, Person person) {

        logger.info("updating Person");

        return PersonMock.update(Long.parseLong(id), person);
    }



    /*
     * ============================================================================
     * DELETE
     * ============================================================================
     * Remove uma pessoa pelo ID.
     *
     * Fluxo:
     *
     * Controller
     *      |
     *      ↓
     * PersonService.delete()
     *      |
     *      ↓
     * PersonMock.delete()
     *      |
     *      ↓
     * Remove da lista em memória
     *
     * Futuramente será substituído por:
     *
     * personRepository.deleteById(id);
     *
     * VOID-> não retorna conteúdo
     * ============================================================================
     */
    public void delete(String id) {

        logger.info("deleting Person");

        PersonMock.delete(Long.parseLong(id));

    }
}