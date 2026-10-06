package com.dinisjovete.restwithspringbootjava.user_test.dto;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
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
 * TESTE UNITÁRIO DE DTO - PERSONUPDATEDTOTEST
 * ============================================================================
 *
 * Objetivo:
 *      Validar o contrato de entrada de atualização (PersonUpdateDTO), testando:
 *          - Construtores, getters e setters
 *          - Regras de validação do Bean Validation (@NotBlank, @Size)
 *
 *      Utiliza o Validator nativo do Bean Validation para garantir que campos
 *      obrigatórios de atualização acionem violações caso estejam vazios ou inválidos.
 * ============================================================================
 */
public class PersonUpdateDTOTest {

    private static Validator validator;
    private PersonUpdateDTO personUpdateDTO;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void setUp() {
        // Inicializa um DTO de atualização válido
        personUpdateDTO = new PersonUpdateDTO(
                "Ayrton",
                "Senna",
                "São Paulo - Brasil",
                "Male"
        );
    }

    // ========================================================================
    // 🟢 TESTES DE CONSTRUTOR, GETTERS E SETTERS
    // ========================================================================

    @Test
    public void testPersonUpdateDTOConstructorAndGetters() {
        assertNotNull(personUpdateDTO);
        assertEquals("Ayrton", personUpdateDTO.getFirstName());
        assertEquals("Senna", personUpdateDTO.getLastName());
        assertEquals("São Paulo - Brasil", personUpdateDTO.getAddress());
        assertEquals("Male", personUpdateDTO.getGender());
    }

    @Test
    public void testPersonUpdateDTOSetters() {
        PersonUpdateDTO dto = new PersonUpdateDTO();
        dto.setFirstName("Nikola");
        dto.setLastName("Tesla");
        dto.setAddress("Smiljan - Croácia");
        dto.setGender("Male");

        assertEquals("Nikola", dto.getFirstName());
        assertEquals("Tesla", dto.getLastName());
        assertEquals("Smiljan - Croácia", dto.getAddress());
        assertEquals("Male", dto.getGender());
    }

    // ========================================================================
    // 🟢 TESTES DE VALIDAÇÃO (BEAN VALIDATION / RESTRIÇÕES)
    // ========================================================================

    @Test
    public void testValidPersonUpdateDTOShouldHaveNoViolations() {
        // Um DTO de atualização preenchido corretamente não deve gerar nenhuma violação
        Set<ConstraintViolation<PersonUpdateDTO>> violations = validator.validate(personUpdateDTO);
        assertTrue(violations.isEmpty(), "Deveria ser um DTO de atualização válido sem violações");
    }

    @Test
    public void testBlankFieldsShouldFailValidation() {
        // Deixa os campos obrigatórios vazios ("") para testar as restrições @NotBlank
        personUpdateDTO.setFirstName("");
        personUpdateDTO.setAddress("");

        Set<ConstraintViolation<PersonUpdateDTO>> violations = validator.validate(personUpdateDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.size() >= 2);
    }

    @Test
    public void testSizeConstraintsShouldFailValidation() {
        // Atribui uma string menor do que o @Size(min = 2) permite
        personUpdateDTO.setFirstName("A");

        Set<ConstraintViolation<PersonUpdateDTO>> violations = validator.validate(personUpdateDTO);

        assertFalse(violations.isEmpty());
        boolean hasSizeError = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("firstName"));
        assertTrue(hasSizeError, "Deveria conter erro de tamanho mínimo no campo firstName");
    }
}
