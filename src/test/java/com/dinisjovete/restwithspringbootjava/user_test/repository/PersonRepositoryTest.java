package com.dinisjovete.restwithspringbootjava.user_test.repository;

import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Diz ao Spring para usar o banco real configurado
@ActiveProfiles("dev") // Garante que lê as configurações do application-dev.properties
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository repository;

    private Person person;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        repository.deleteAll();

        // Cria uma entidade de teste
        person = new Person(
                null,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "password123",
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );
    }

    @Test
    public void testSavePersonSuccess() {
        Person savedPerson = repository.save(person);

        assertNotNull(savedPerson);
        assertNotNull(savedPerson.getId());
        assertEquals("Ayrton", savedPerson.getFirstName());
        assertEquals("ayrton.senna@email.com", savedPerson.getEmail());
    }

    @Test
    public void testFindByEmailSuccess() {
        repository.save(person);

        Optional<Person> result = repository.findByEmail("ayrton.senna@email.com");

        assertTrue(result.isPresent());
        assertEquals("Ayrton", result.get().getFirstName());
        assertEquals("11122233344", result.get().getCpf());
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<Person> result = repository.findByEmail("inexistente@email.com");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeletePersonSuccess() {
        Person savedPerson = repository.save(person);
        Long id = savedPerson.getId();

        repository.delete(savedPerson);

        Optional<Person> result = repository.findById(id);
        assertTrue(result.isEmpty());
    }
}