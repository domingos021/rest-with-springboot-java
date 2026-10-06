package com.dinisjovete.restwithspringbootjava.user_test.mapper;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: PersonMapperManualTest
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Classe de testes unitários (JUnit 5 + Mockito) para validar o comportamento
 * do `PersonMapper` (Mapper Manual). Testa a conversão de Entity para DTO (`toDTO`),
 * a conversão de DTO de inserção para Entity aplicando BCrypt e Roles (`toEntity`),
 * e a atualização segura de entidades existentes (`updateEntityFromDTO`).
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.mappers.mapper_manual.PersonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PersonMapperManualTest {

    private PersonMapper mapper;
    private PasswordEncoder passwordEncoderMock;

    @BeforeEach
    public void setUp() {
        // Mockamos o PasswordEncoder para isolar o teste unitário do Mapper
        passwordEncoderMock = Mockito.mock(PasswordEncoder.class);

        // Configuramos o mock para retornar uma hash simulada quando o encode for chamado
        when(passwordEncoderMock.encode(anyString())).thenReturn("$2a$10$encodedPasswordHashDummy");

        // Instancia o mapper manual injetando o mock
        mapper = new PersonMapper();
    }

    @Test
    public void testEntityToDTO() {
        // Cenário: Entidade preenchida vinda do banc
        Person entity = new Person();
        entity.setId(1L);
        entity.setFirstName("Diniz");
        entity.setLastName("Jovete");
        entity.setCpf("111.222.333-44");
        entity.setEmail("diniz@email.com");
        entity.setAddress("Brasília - DF");
        entity.setGender("Male");
        entity.setRole(PersonRole.ADMIN);

        // Ação
        PersonDTO dto = mapper.toDTO(entity);

        // Validações
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Diniz", dto.getFirstName());
        assertEquals("Jovete", dto.getLastName());
        assertEquals("111.222.333-44", dto.getCpf());
        assertEquals("diniz@email.com", dto.getEmail());
        assertEquals("Brasília - DF", dto.getAddress());
        assertEquals("Male", dto.getGender());
        assertEquals(PersonRole.ADMIN, dto.getRole());
    }

    @Test
    public void testEntityToDTO_WhenEntityIsNull() {
        PersonDTO dto = mapper.toDTO(null);
        assertNull(dto, "Deveria retornar null caso a entidade seja nula");
    }

    @Test
    public void testInsertDTOToEntity() {
        // Cenário: DTO de inserção vindo da requisição HTTP
        PersonInsertDTO insertDTO = new PersonInsertDTO();
        insertDTO.setFirstName("Domingos");
        insertDTO.setLastName("Jovete");
        insertDTO.setCpf("999.888.777-66");
        insertDTO.setEmail("domingos@email.com");
        insertDTO.setAddress("Taguatinga - DF");
        insertDTO.setGender("Male");
        insertDTO.setPassword("123456");
        insertDTO.setRole(PersonRole.CLIENT);

        // Ação
        Person entity = mapper.toEntity(insertDTO);

        // Validações
        assertNotNull(entity);
        assertEquals("Domingos", entity.getFirstName());
        assertEquals("Jovete", entity.getLastName());
        assertEquals("999.888.777-66", entity.getCpf());
        assertEquals("domingos@email.com", entity.getEmail());
        assertEquals("Taguatinga - DF", entity.getAddress());
        assertEquals("Male", entity.getGender());
        // Garante que a senha passou pelo encoder de segurança
        assertEquals("$2a$10$encodedPasswordHashDummy", entity.getPassword());
        assertEquals(PersonRole.CLIENT, entity.getRole());
    }

    @Test
    public void testInsertDTOToEntity_DefaultRoleWhenNull() {
        // Cenário: DTO sem a role informada para testar o fallback para CLIENT
        PersonInsertDTO insertDTO = new PersonInsertDTO();
        insertDTO.setFirstName("Test");
        insertDTO.setPassword("password");
        // Role fica null propositalmente

        Person entity = mapper.toEntity(insertDTO);

        assertNotNull(entity);
        assertEquals(PersonRole.CLIENT, entity.getRole(), "Deveria assumir CLIENT como role padrão");
    }

    @Test
    public void testUpdateEntityFromDTO() {
        // Cenário: Entidade existente no banco
        Person existingEntity = new Person();
        existingEntity.setId(10L);
        existingEntity.setFirstName("Nome Antigo");
        existingEntity.setLastName("Sobrenome Antigo");
        existingEntity.setCpf("000.000.000-00"); // Não deve mudar
        existingEntity.setEmail("antigo@email.com"); // Não deve mudar
        existingEntity.setAddress("Endereço Antigo");
        existingEntity.setGender("Female");

        // DTO com os novos dados de atualização
        PersonUpdateDTO updateDTO = new PersonUpdateDTO();
        updateDTO.setFirstName("Nome Novo");
        updateDTO.setLastName("Sobrenome Novo");
        updateDTO.setAddress("Endereço Novo");
        updateDTO.setGender("Male");

        // Ação
        mapper.updateEntityFromDTO(updateDTO, existingEntity);

        // Validações (Campos permitidos foram alterados)
        assertEquals("Nome Novo", existingEntity.getFirstName());
        assertEquals("Sobrenome Novo", existingEntity.getLastName());
        assertEquals("Endereço Novo", existingEntity.getAddress());
        assertEquals("Male", existingEntity.getGender());

        // Validações (Campos sensíveis/imutáveis permaneceram intactos)
        assertEquals(10L, existingEntity.getId());
        assertEquals("000.000.000-00", existingEntity.getCpf());
        assertEquals("antigo@email.com", existingEntity.getEmail());
    }
}
