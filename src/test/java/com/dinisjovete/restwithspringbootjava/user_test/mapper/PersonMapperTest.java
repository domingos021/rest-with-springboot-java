package com.dinisjovete.restwithspringbootjava.user_test.mapper;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.mappers.mapper_manual.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
 * ============================================================================
 * TESTE UNITÁRIO DE MAPPER - PERSONMAPPERTEST
 * ============================================================================
 *
 * Objetivo:
 *      Testar de forma isolada a conversão de dados e regras de mapeamento da classe PersonMapper.
 *
 *      O teste NÃO utiliza:
 *          ❌ Banco de Dados
 *          ❌ Contexto do Spring Boot
 *
 *      Ele utiliza:
 *          ✅ MockitoExtension -> Habilita os mocks para JUnit 5
 *          ✅ @Mock           -> Simula o comportamento do PasswordEncoder (BCrypt)
 *          ✅ @InjectMocks    -> Instancia o PersonMapper real injetando o encoder simulado
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
public class PersonMapperTest {

    // Mock do PasswordEncoder para isolar a criptografia de senha durante o mapeamento para Entity
    @Mock
    private PasswordEncoder passwordEncoder;

    // Instancia o Mapper real injetando o mock do PasswordEncoder
    @InjectMocks
    private PersonMapper mapper;

    private Person person;
    private PersonInsertDTO personInsertDTO;
    private PersonUpdateDTO personUpdateDTO;

    @BeforeEach
    void setUp() {
        // Inicializa uma entidade padrão para os testes de Entity -> DTO
        person = new Person(
                1L,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "$2a$10$encodedPassword",
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );

        // Inicializa um DTO de inserção padrão
        personInsertDTO = new PersonInsertDTO();
        personInsertDTO.setFirstName("Nikola");
        personInsertDTO.setLastName("Tesla");
        personInsertDTO.setCpf("22233344455");
        personInsertDTO.setEmail("nikola.tesla@email.com");
        personInsertDTO.setPassword("123456");
        personInsertDTO.setAddress("Smiljan - Croácia");
        personInsertDTO.setGender("Male");
        personInsertDTO.setRole(PersonRole.CLIENT);

        // Inicializa um DTO de atualização padrão
        personUpdateDTO = new PersonUpdateDTO();
        personUpdateDTO.setFirstName("Ayrton (Updated)");
        personUpdateDTO.setLastName("Senna");
        personUpdateDTO.setAddress("Rio de Janeiro - Brasil");
        personUpdateDTO.setGender("Male");
    }

    // ========================================================================
    // 🟢 TESTES DE CONVERSÃO: ENTITY -> DTO
    // ========================================================================

    @Test
    public void testToDTO() {
        // Execução
        PersonDTO dto = mapper.toDTO(person);

        // Validações: Garante que todos os campos foram mapeados corretamente
        assertNotNull(dto);
        assertEquals(person.getId(), dto.getId());
        assertEquals(person.getFirstName(), dto.getFirstName());
        assertEquals(person.getLastName(), dto.getLastName());
        assertEquals(person.getCpf(), dto.getCpf());
        assertEquals(person.getEmail(), dto.getEmail());
        assertEquals(person.getAddress(), dto.getAddress());
        assertEquals(person.getGender(), dto.getGender());
        assertEquals(person.getRole(), dto.getRole());
    }

    @Test
    public void testToDTOReturnsNullWhenEntityIsNull() {
        // Cenário: Passando uma entidade nula para o mapper
        PersonDTO dto = mapper.toDTO(null);

        // Validação: Deve retornar null de forma segura via Optional
        assertNull(dto);
    }

    // ========================================================================
    // 🟢 TESTES DE CONVERSÃO: INSERT DTO -> ENTITY (COM BCRYPT E ROLE PADRÃO)
    // ========================================================================

    @Test
    public void testToEntityWithRoleProvided() {
        // Cenário: Simula a encriptação da senha pelo PasswordEncoder
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$hashedPassword123");

        // Execução
        Person entity = mapper.toEntity(personInsertDTO);

        // Validações
        assertNotNull(entity);
        assertNull(entity.getId()); // O ID deve ser nulo pois é um novo registro
        assertEquals(personInsertDTO.getFirstName(), entity.getFirstName());
        assertEquals(personInsertDTO.getLastName(), entity.getLastName());
        assertEquals(personInsertDTO.getCpf(), entity.getCpf());
        assertEquals(personInsertDTO.getEmail(), entity.getEmail());
        assertEquals(personInsertDTO.getAddress(), entity.getAddress());
        assertEquals(personInsertDTO.getGender(), entity.getGender());
        assertEquals("$2a$10$hashedPassword123", entity.getPassword()); // Verifica se a senha foi criptografada
        assertEquals(PersonRole.CLIENT, entity.getRole()); // Verifica se manteve a role informada

        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    public void testToEntityDefaultRoleWhenNull() {
        // Cenário: DTO sem Role informada para testar a atribuição padrão (CLIENT)
        personInsertDTO.setRole(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedPassword");

        // Execução
        Person entity = mapper.toEntity(personInsertDTO);

        // Validação: Garante que assumiu CLIENT como padrão
        assertNotNull(entity);
        assertEquals(PersonRole.CLIENT, entity.getRole());
    }

    @Test
    public void testToEntityReturnsNullWhenDtoIsNull() {
        // Cenário: Passando um InsertDTO nulo
        Person entity = mapper.toEntity(null);

        // Validação
        assertNull(entity);
        verifyNoInteractions(passwordEncoder);
    }

    // ========================================================================
    // 🟢 TESTES DE ATUALIZAÇÃO: UPDATE DTO -> ENTIDADE EXISTENTE
    // ========================================================================

    @Test
    public void testUpdateEntityFromDTO() {
        // Execução: Atualiza os campos permitidos na entidade existente
        mapper.updateEntityFromDTO(personUpdateDTO, person);

        // Validações: Confirma que os campos permitidos mudaram...
        assertEquals("Ayrton (Updated)", person.getFirstName());
        assertEquals("Rio de Janeiro - Brasil", person.getAddress());

        // ...e que campos sensíveis/fixos (como ID, E-mail, CPF e Senha) PERMANECERAM INTOCADOS
        assertEquals(1L, person.getId());
        assertEquals("11122233344", person.getCpf());
        assertEquals("ayrton.senna@email.com", person.getEmail());
        assertEquals("$2a$10$encodedPassword", person.getPassword());
        assertEquals(PersonRole.ADMIN, person.getRole());
    }

    @Test
    public void testUpdateEntityFromDTODoesNothingWhenParamsAreNull() {
        // Cenário: Passando parâmetros nulos para o método de update
        assertDoesNotThrow(() -> mapper.updateEntityFromDTO(null, person));
        assertDoesNotThrow(() -> mapper.updateEntityFromDTO(personUpdateDTO, null));
    }
}
