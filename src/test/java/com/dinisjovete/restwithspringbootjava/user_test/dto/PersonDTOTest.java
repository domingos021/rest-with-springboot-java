package com.dinisjovete.restwithspringbootjava.user_test.dto;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ============================================================================
 * TESTE UNITÁRIO DE DTO - PERSONDTOTEST
 * ============================================================================
 *
 * Objetivo:
 *      Validar o comportamento do DTO de resposta (PersonDTO), testando:
 *          - Construtores (vazio e com argumentos)
 *          - Getters e Setters de todos os campos de saída da API
 *
 *      O teste é puramente unitário, sem necessidade de banco de dados ou Spring Context.
 * ============================================================================
 */
public class PersonDTOTest {

    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        // Inicializa o DTO usando o construtor com argumentos
        personDTO = new PersonDTO(
                1L,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );
    }

    // ========================================================================
    // 🟢 TESTES DE CONSTRUTOR E GETTERS
    // ========================================================================

    @Test
    public void testPersonDTOConstructorAndGetters() {
        assertNotNull(personDTO);
        assertEquals(1L, personDTO.getId());
        assertEquals("Ayrton", personDTO.getFirstName());
        assertEquals("Senna", personDTO.getLastName());
        assertEquals("11122233344", personDTO.getCpf());
        assertEquals("ayrton.senna@email.com", personDTO.getEmail());
        assertEquals("São Paulo - Brasil", personDTO.getAddress());
        assertEquals("Male", personDTO.getGender());
        assertEquals(PersonRole.ADMIN, personDTO.getRole());
    }

    // ========================================================================
    // 🟢 TESTES DE SETTERS
    // ========================================================================

    @Test
    public void testPersonDTOSetters() {
        PersonDTO emptyDto = new PersonDTO();

        emptyDto.setId(2L);
        emptyDto.setFirstName("Nikola");
        emptyDto.setLastName("Tesla");
        emptyDto.setCpf("22233344455");
        emptyDto.setEmail("nikola.tesla@email.com");
        emptyDto.setAddress("Smiljan - Croácia");
        emptyDto.setGender("Male");
        emptyDto.setRole(PersonRole.CLIENT);

        assertEquals(2L, emptyDto.getId());
        assertEquals("Nikola", emptyDto.getFirstName());
        assertEquals("Tesla", emptyDto.getLastName());
        assertEquals("22233344455", emptyDto.getCpf());
        assertEquals("nikola.tesla@email.com", emptyDto.getEmail());
        assertEquals("Smiljan - Croácia", emptyDto.getAddress());
        assertEquals("Male", emptyDto.getGender());
        assertEquals(PersonRole.CLIENT, emptyDto.getRole());
    }
}
