package com.dinisjovete.restwithspringbootjava.user_test.dto;


import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/*
 * ============================================================================
 * TESTE UNITÁRIO DE DTO - PERSONINSERTDTOTEST
 * ============================================================================
 *
 * Objetivo:
 *      Validar o contrato de entrada de inserção (PersonInsertDTO), testando:
 *          - Construtores, getters e setters
 *          - Regras de validação do Bean Validation (@NotBlank, @Email, @Size)
 *
 *      Utiliza o Validator nativo do Bean Validation para testar se os campos
 *      inválidos geram violações de restrição (ConstraintViolations).
 * ============================================================================
 */
public class PersonInsertDTOTest {

    private static Validator validator;
    private PersonInsertDTO personInsertDTO;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        // Inicializa um DTO válido para servir de base nos testes de validação
        personInsertDTO = new PersonInsertDTO(
                "Nikola",
                "Tesla",
                "22233344455",
                "nikola.tesla@email.com",
                "123456",
                "Smiljan - Croácia",
                "Male",
                PersonRole.CLIENT
        );
    }

    // ========================================================================
    // 🟢 TESTES DE CONSTRUTOR, GETTERS E SETTERS
    // ========================================================================

    @Test
    public void testPersonInsertDTOConstructorAndGetters() {
        assertNotNull(personInsertDTO);
        assertEquals("Nikola", personInsertDTO.getFirstName());
        assertEquals("Tesla", personInsertDTO.getLastName());
        assertEquals("22233344455", personInsertDTO.getCpf());
        assertEquals("nikola.tesla@email.com", personInsertDTO.getEmail());
        assertEquals("123456", personInsertDTO.getPassword());
        assertEquals("Smiljan - Croácia", personInsertDTO.getAddress());
        assertEquals("Male", personInsertDTO.getGender());
        assertEquals(PersonRole.CLIENT, personInsertDTO.getRole());
    }

    @Test
    public void testPersonInsertDTOSetters() {
        PersonInsertDTO dto = new PersonInsertDTO();
        dto.setFirstName("Albert");
        dto.setLastName("Einstein");
        dto.setCpf("33344455666");
        dto.setEmail("albert@email.com");
        dto.setPassword("securePass");
        dto.setAddress("Ulm - Alemanha");
        dto.setGender("Male");
        dto.setRole(PersonRole.ADMIN);

        assertEquals("Albert", dto.getFirstName());
        assertEquals("Einstein", dto.getLastName());
        assertEquals("33344455666", dto.getCpf());
        assertEquals("albert@email.com", dto.getEmail());
        assertEquals("securePass", dto.getPassword());
        assertEquals("Ulm - Alemanha", dto.getAddress());
        assertEquals("Male", dto.getGender());
        assertEquals(PersonRole.ADMIN, dto.getRole());
    }

    // ========================================================================
    // 🟢 TESTES DE VALIDAÇÃO (BEAN VALIDATION / RESTRIÇÕES)
    // ========================================================================

    @Test
    public void testValidPersonInsertDTOShouldHaveNoViolations() {
        // Um objeto preenchido corretamente não deve gerar nenhuma violação de validação
        Set<ConstraintViolation<PersonInsertDTO>> violations = validator.validate(personInsertDTO);
        assertTrue(violations.isEmpty(), "Deveria ser um DTO válido sem violações");
    }

    @Test
    public void testInvalidEmailShouldFailValidation() {
        // Atribui um e-mail em formato inválido
        personInsertDTO.setEmail("email-invalido");

        Set<ConstraintViolation<PersonInsertDTO>> violations = validator.validate(personInsertDTO);

        assertFalse(violations.isEmpty());
        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
        assertTrue(hasEmailError, "Deveria conter erro de validação no campo email");
    }

    @Test
    public void testBlankFieldsShouldFailValidation() {
        // Deixa campos obrigatórios como vazios ("")
        personInsertDTO.setFirstName("");
        personInsertDTO.setLastName("");
        personInsertDTO.setEmail("");

        Set<ConstraintViolation<PersonInsertDTO>> violations = validator.validate(personInsertDTO);

        // Deve capturar violações para os campos anotados com @NotBlank
        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 3);
    }
}
