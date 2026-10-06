package com.dinisjovete.restwithspringbootjava.user_test.entities;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ============================================================================
 * TESTE UNITÁRIO DE ENTIDADE - PERSONTEST
 * ============================================================================
 *
 * Objetivo:
 *      Validar o comportamento da classe de Entidade (Person), testando:
 *          - Construtores (vazio e com argumentos)
 *          - Getters e Setters
 *          - Regras de igualdade (equals) e hash (hashCode) baseadas no ID
 *
 *      O teste NÃO utiliza:
 *          ❌ Banco de dados
 *          ❌ Spring Context
 *
 *      Trata-se de um teste unitário puro de POJO (Plain Old Java Object).
 * ============================================================================
 */
public class PersonTest {

    private Person person1;
    private Person person2;
    private Person person3;

    @BeforeEach
    void setUp() {
        // Inicializa objetos Person para os testes de equals e hashCode
        person1 = new Person(
                1L,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "password123",
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );

        person2 = new Person(
                1L, // Mesmo ID de person1
                "Ayrton Modificado",
                "Senna Modificado",
                "99988877766",
                "outro@email.com",
                "otherpass",
                "Outro Lugar",
                "Male",
                PersonRole.CLIENT
        );

        person3 = new Person(
                2L, // ID diferente
                "Nikola",
                "Tesla",
                "22233344455",
                "nikola.tesla@email.com",
                "password123",
                "Smiljan - Croácia",
                "Male",
                PersonRole.CLIENT
        );
    }

    // ========================================================================
    // 🟢 TESTES DE CONSTRUTORES, GETTERS E SETTERS
    // ========================================================================

    @Test
    public void testPersonConstructorAndGetters() {
        // Valida se o construtor com argumentos atribuiu corretamente os valores aos getters
        assertNotNull(person1);
        assertEquals(1L, person1.getId());
        assertEquals("Ayrton", person1.getFirstName());
        assertEquals("Senna", person1.getLastName());
        assertEquals("11122233344", person1.getCpf());
        assertEquals("ayrton.senna@email.com", person1.getEmail());
        assertEquals("password123", person1.getPassword());
        assertEquals("São Paulo - Brasil", person1.getAddress());
        assertEquals("Male", person1.getGender());
        assertEquals(PersonRole.ADMIN, person1.getRole());
    }

    @Test
    public void testPersonSetters() {
        // Testa o construtor vazio e a atribuição via Setters
        Person emptyPerson = new Person();
        emptyPerson.setId(10L);
        emptyPerson.setFirstName("Albert");
        emptyPerson.setLastName("Einstein");
        emptyPerson.setCpf("33344455666");
        emptyPerson.setEmail("albert@email.com");
        emptyPerson.setPassword("securePass");
        emptyPerson.setAddress("Ulm");
        emptyPerson.setGender("Male");
        emptyPerson.setRole(PersonRole.CLIENT);

        assertEquals(10L, emptyPerson.getId());
        assertEquals("Albert", emptyPerson.getFirstName());
        assertEquals("Einstein", emptyPerson.getLastName());
        assertEquals("33344455666", emptyPerson.getCpf());
        assertEquals("albert@email.com", emptyPerson.getEmail());
        assertEquals("securePass", emptyPerson.getPassword());
        assertEquals("Ulm", emptyPerson.getAddress());
        assertEquals("Male", emptyPerson.getGender());
        assertEquals(PersonRole.CLIENT, emptyPerson.getRole());
    }

    // ========================================================================
    // 🟢 TESTES DE EQUALS E HASHCODE
    // ========================================================================

    @Test
    public void testEqualsAndHashCode() {
        // 1. Comparação com ele mesmo (Reflexiva)
        assertEquals(person1, person1);

        // 2. Comparação de objetos com o MESMO ID (Devem ser considerados iguais pelo equals da entidade)
        assertEquals(person1, person2);
        assertEquals(person1.hashCode(), person2.hashCode());

        // 3. Comparação de objetos com IDs DIFERENTES (Devem ser diferentes)
        assertNotEquals(person1, person3);

        // 4. Comparação com null e com outra classe
        assertNotEquals(null, person1);
        assertNotEquals(person1, "Uma String qualquer");
    }

    @Test
    public void testEqualsWithNullId() {
        // Entidades sem ID (recém criadas via new sem persistir) não devem ser iguais por ID
        Person pNull1 = new Person();
        pNull1.setFirstName("Test");

        Person pNull2 = new Person();
        pNull2.setFirstName("Test");

        // Como o equals exige que id != null e id.equals(...) para retornar true, IDs nulos geram false
        assertNotEquals(pNull1, pNull2);
    }
}
