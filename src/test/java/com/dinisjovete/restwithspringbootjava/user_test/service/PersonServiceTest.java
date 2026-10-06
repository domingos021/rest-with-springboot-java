package com.dinisjovete.restwithspringbootjava.user_test.service;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapper_manual.PersonMapper;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.services.person.v1.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * ============================================================================
 * TESTE UNITÁRIO DE SERVICE - PERSONSERVICETEST
 * ============================================================================
 *
 * Objetivo:
 *      Testar de forma isolada a camada de Regra de Negócio (PersonService).
 *
 *      O teste NÃO utiliza:
 *          ❌ Banco de Dados real
 *          ❌ Spring Context completo (carregamento leve e rápido)
 *
 *      Ele utiliza:
 *          ✅ MockitoExtension -> Habilita os mocks para JUnit 5
 *          ✅ @Mock           -> Cria versões falsas do Repository e do Mapper
 *          ✅ @InjectMocks    -> Instancia o PersonService real e injeta os mocks nele
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    // Cria um dublê (mock) do repositório para simular as operações no banco de dados
    @Mock
    private PersonRepository repository;

    // Cria um dublê (mock) do mapper para simular as conversões e criptografias
    @Mock
    private PersonMapper mapper;

    // Instancia o Service real injetando os mocks de repository e mapper acima
    @InjectMocks
    private PersonService service;

    private Person person;
    private PersonDTO personDTO;
    private PersonInsertDTO personInsertDTO;
    private PersonUpdateDTO personUpdateDTO;

    // Executado antes de cada teste para inicializar objetos padrão
    @BeforeEach
    void setUp() {
        // Entidade de domínio (como vem do banco)
        person = new Person(
                1L,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "$2a$10$encodedPassword", // senha criptografada simulada
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );

        // DTO de resposta segura
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

        // DTO de inserção
        personInsertDTO = new PersonInsertDTO();
        personInsertDTO.setFirstName("Nikola");
        personInsertDTO.setLastName("Tesla");
        personInsertDTO.setCpf("22233344455");
        personInsertDTO.setEmail("nikola.tesla@email.com");
        personInsertDTO.setPassword("123456");
        personInsertDTO.setAddress("Smiljan - Croácia");
        personInsertDTO.setGender("Male");
        personInsertDTO.setRole(PersonRole.CLIENT);

        // DTO de atualização
        personUpdateDTO = new PersonUpdateDTO();
        personUpdateDTO.setFirstName("Ayrton (Updated)");
        personUpdateDTO.setLastName("Senna");
        personUpdateDTO.setAddress("Rio de Janeiro - Brasil");
        personUpdateDTO.setGender("Male");
    }

    // ========================================================================
    // 🟢 TESTES DE SUCESSO (HAPPY PATH)
    // ========================================================================

    @Test
    public void testFindByIdSuccess() {
        // Cenário: O repositório encontra a pessoa pelo ID
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        // O mapper converte a entidade para DTO
        when(mapper.toDTO(person)).thenReturn(personDTO);

        // Execução
        PersonDTO result = service.findById(1L);

        // Validações
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Ayrton", result.getFirstName());
        assertEquals("ayrton.senna@email.com", result.getEmail());

        // Verifica se os métodos dos mocks foram chamados de fato
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).toDTO(person);
    }

    @Test
    public void testFindAllSuccess() {
        // Cenário: O repositório retorna uma lista com uma pessoa
        when(repository.findAll()).thenReturn(List.of(person));
        when(mapper.toDTO(person)).thenReturn(personDTO);

        // Execução
        List<PersonDTO> list = service.findAll();

        // Validações
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Ayrton", list.get(0).getFirstName());

        verify(repository, times(1)).findAll();
        verify(mapper, times(1)).toDTO(person);
    }

    @Test
    public void testCreateSuccess() {
        // Cenário: Conversão de DTO para Entity, salvamento e retorno mapeado para DTO
        when(mapper.toEntity(personInsertDTO)).thenReturn(person);
        when(repository.save(any(Person.class))).thenReturn(person);
        when(mapper.toDTO(person)).thenReturn(personDTO);

        // Execução
        PersonDTO result = service.create(personInsertDTO);

        // Validações
        assertNotNull(result);
        assertEquals("Ayrton", result.getFirstName());

        verify(mapper, times(1)).toEntity(personInsertDTO);
        verify(repository, times(1)).save(any(Person.class));
        verify(mapper, times(1)).toDTO(person);
    }

    @Test
    public void testUpdateSuccess() {
        // Cenário: Busca a pessoa existente, atualiza e salva
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(any(Person.class))).thenReturn(person);
        when(mapper.toDTO(person)).thenReturn(personDTO);

        // Execução
        PersonDTO result = service.update(1L, personUpdateDTO);

        // Validações
        assertNotNull(result);
        verify(repository, times(1)).findById(1L);
        verify(mapper, times(1)).updateEntityFromDTO(personUpdateDTO, person);
        verify(repository, times(1)).save(person);
        verify(mapper, times(1)).toDTO(person);
    }

    @Test
    public void testDeleteSuccess() {
        // Cenário: Busca o registro existente antes de deletar
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        doNothing().when(repository).delete(person);

        // Execução
        service.delete(1L);

        // Validações
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).delete(person);
    }

    // ========================================================================
    // 🔴 TESTES DE ERRO / EXCEÇÃO (SAD PATH)
    // ========================================================================

    @Test
    public void testFindByIdNotFound() {
        // Cenário: O repositório não encontra o ID informado (retorna Optional.empty)
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Execução & Validação: Garante que lança a ResourceNotFoundException
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.findById(99L)
        );

        assertEquals("No record found for this ID!", exception.getMessage());
        verify(repository, times(1)).findById(99L);
        verifyNoInteractions(mapper); // Mapper não deve ser chamado se a entidade não existe
    }

    @Test
    public void testUpdateNotFound() {
        // Cenário: Tenta atualizar um ID que não existe
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.update(99L, personUpdateDTO)
        );

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Person.class));
    }

    @Test
    public void testDeleteNotFound() {
        // Cenário: Tenta deletar um ID que não existe
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.delete(99L)
        );

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).delete(any(Person.class));
    }
}


/*
 * ============================================================================
 * FLUXO DO TESTE UNITÁRIO DE SERVICE
 * ============================================================================
 *
 *  JUnit / Mockito
 *         |
 *         | Chama o método do Service (ex: service.findById(1L))
 *         ↓
 *  +--------------------------------+
 *  |         PersonService          |
 *  +--------------------------------+
 *         |
 *         | Controla o fluxo de negócio
 *         ├──> repository.findById(1L)  ---> (Simulado pelo Mock: Retorna Optional.of(person))
 *         └──> mapper.toDTO(person)     ---> (Simulado pelo Mock: Retorna personDTO)
 *         |
 *         ↓
 *  Retorna o resultado para o teste validar as asserções (assertEquals, assertNotNull)
 * ============================================================================
 */