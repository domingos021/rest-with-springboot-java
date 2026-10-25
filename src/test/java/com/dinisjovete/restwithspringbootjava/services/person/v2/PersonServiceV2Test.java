package com.dinisjovete.restwithspringbootjava.services.person.v2;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.v2.PersonMapper_MapStructV2;
import com.dinisjovete.restwithspringbootjava.mocks.v2.MockPersonV2;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/*
 * ============================================================================
 * TESTE UNITÁRIO DA CAMADA DE SERVIÇO - PersonServiceV2
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * Apenas a classe PersonServiceV2, isolada de tudo que está ao redor dela:
 *
 *      Controller            <- fora do teste (não participa)
 *          |
 *      PersonServiceV2       <- UNIDADE TESTADA (código real, executado de verdade)
 *          |
 *     +----+-----------+----------------+
 *     |                |                |
 *  Repository        Mapper       PasswordEncoder
 *    (Mock)          (Mock)           (Mock)
 *
 * Nada de banco, nada de Spring, nada de BCrypt real. O Mockito cria "dublês"
 * das dependências e nós controlamos o que eles devolvem. Por isso o teste é
 * rápido (milissegundos) e, se falhar, o culpado só pode estar no Service.
 *
 *
 * V1 x V2 NESTE PROJETO
 * ---------------------
 * A V2 usa os DTOs próprios (PersonDTOV2, PersonInsertDTOV2, PersonUpdateDTOV2)
 * e o mapper PersonMapper_MapStructV2, mas continua trabalhando com a MESMA
 * entidade Person e o MESMO PersonRepository da V1. Por isso a entidade é
 * importada do pacote v1 e as regras de negócio testadas aqui são as mesmas.
 *
 *
 * O QUE UM TESTE DE SERVICE DEVE PROVAR
 * -------------------------------------
 * Boa parte dos métodos só delega (repository -> mapper). O valor do teste está
 * em proteger o que o Service REALMENTE decide:
 *
 *   - create():  criptografar a senha, aplicar a Role padrão CLIENT.
 *   - findById / update / delete: lançar ResourceNotFoundException quando o ID
 *     não existe e NÃO executar nenhuma escrita nesse caso.
 *
 * Por isso cada método tem o caminho feliz E o caminho de erro/borda.
 *
 *
 * PADRÃO DE ESCRITA: ARRANGE / ACT / ASSERT (+ VERIFY)
 * ----------------------------------------------------
 *   ARRANGE : monta os dados e ensina os mocks o que responder.
 *   ACT     : chama o método real do Service (uma única chamada).
 *   ASSERT  : confere o RESULTADO retornado.
 *   VERIFY  : confere as INTERAÇÕES (quem foi chamado, em que ordem, quem NÃO foi).
 *
 *
 * NOMES DOS TESTES
 * ----------------
 * Formato: metodo_quandoCondicao_resultadoEsperado
 * Quando um teste falha, o nome já diz qual regra quebrou, sem abrir o código.
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
        /*
         * @ExtendWith(MockitoExtension.class)
         *
         * Liga o Mockito ao JUnit 5. Ele cria os @Mock, injeta no @InjectMocks e
         * descarta tudo ao final de cada teste.
         *
         * Detalhe importante: essa extensão usa STRICT STUBS. Se você ensinar um mock
         * com when(...) e o Service nunca chamar aquilo, o teste FALHA com
         * UnnecessaryStubbingException. Isso é bom: impede stubs "sobrando" que
         * enganam quem lê o teste.
         *
         * Observação sobre o ciclo de vida: NÃO usamos @TestInstance(PER_CLASS).
         * O padrão do JUnit é criar uma instância nova da classe para cada @Test, o que
         * isola completamente os testes (nenhum campo "vaza" de um para outro). PER_CLASS
         * só vale a pena quando precisamos de @BeforeAll não-estático, o que não é o caso.
         */
class PersonServiceV2Test {

    /*
     * Fábrica de dados fictícios (Test Data Builder). NÃO é um mock do Mockito:
     * é uma classe nossa que devolve objetos reais, preenchidos com valores
     * previsíveis. mockEntity(1) gera uma Person de id 1, mockDTO(1) um PersonDTOV2
     * de id 1, e assim por diante. O número é o "índice" usado nos valores.
     */
    private MockPersonV2 input;

    /*
     * @InjectMocks: o Mockito instancia o PersonServiceV2 REAL chamando o
     * construtor e entrega os @Mock abaixo. A injeção é por TIPO, então o nome
     * dos campos não precisa coincidir com o nome usado dentro do Service.
     */
    @InjectMocks
    private PersonServiceV2 service;

    /* @Mock: dublês das dependências. Não executam código real; devolvem o que mandarmos. */
    @Mock
    private PersonRepository repository;

    @Mock
    private PersonMapper_MapStructV2 mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    /*
     * Roda antes de CADA @Test. Como os mocks são recriados pelo Mockito a cada
     * teste, só precisamos recriar a fábrica de dados.
     */
    @BeforeEach
    void setUp() {
        input = new MockPersonV2();
    }

    // ========================================================================
    // findById
    // ========================================================================

    @Test
    @DisplayName("findById: quando a pessoa existe, devolve o DTO produzido pelo mapper")
    void findById_whenPersonExists_returnsMappedDto() {

        // ARRANGE
        // O repository (mock) "encontra" a entidade; o mapper (mock) a converte em DTO.
        Person entity = input.mockEntity(1);
        PersonDTOV2 dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.mapEntityToDto(entity)).thenReturn(dto);

        // ACT
        PersonDTOV2 result = service.findById(1L);

        // ASSERT
        // assertSame compara a REFERÊNCIA: prova que o Service devolveu exatamente
        // o objeto que o mapper produziu, sem trocar nem montar outro por conta própria.
        assertSame(dto, result);

        // VERIFY
        verify(repository).findById(1L);
        verify(mapper).mapEntityToDto(entity);
        verifyNoMoreInteractions(repository, mapper); // nenhuma chamada extra e inesperada
    }

    @Test
    @DisplayName("findById: quando o ID não existe, lança ResourceNotFoundException")
    void findById_whenPersonDoesNotExist_throwsResourceNotFoundException() {

        // ARRANGE
        // Optional.empty() é o que o JPA devolve quando não há registro com o ID.
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        // assertThrows executa o lambda e passa se (e somente se) a exceção
        // esperada for lançada. Aqui a ação e a verificação acontecem juntas.
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));

        // VERIFY
        // Sem entidade, não há o que converter: o mapper não pode ter sido acionado.
        verifyNoInteractions(mapper);
    }

    // ========================================================================
    // findAll
    // ========================================================================

    @Test
    @DisplayName("findAll: converte cada entidade em DTO e preserva a ordem")
    void findAll_whenThereArePeople_returnsAllMappedDtosInOrder() {

        // ARRANGE
        Person person1 = input.mockEntity(1);
        Person person2 = input.mockEntity(2);
        PersonDTOV2 dto1 = input.mockDTO(1);
        PersonDTOV2 dto2 = input.mockDTO(2);

        when(repository.findAll()).thenReturn(List.of(person1, person2));
        when(mapper.mapEntityToDto(person1)).thenReturn(dto1);
        when(mapper.mapEntityToDto(person2)).thenReturn(dto2);

        // ACT
        List<PersonDTOV2> result = service.findAll();

        // ASSERT
        // A lista inteira de uma vez: confere tamanho, conteúdo e ORDEM.
        assertEquals(List.of(dto1, dto2), result);

        // VERIFY
        verify(repository).findAll();
        verify(mapper).mapEntityToDto(person1);
        verify(mapper).mapEntityToDto(person2);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("findAll: quando não há ninguém, devolve lista vazia sem acionar o mapper")
    void findAll_whenThereIsNobody_returnsEmptyList() {

        // ARRANGE
        when(repository.findAll()).thenReturn(List.of());

        // ACT
        List<PersonDTOV2> result = service.findAll();

        // ASSERT
        // Lista vazia (e não null) é o contrato esperado pelo Controller.
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // VERIFY
        verifyNoInteractions(mapper);
    }

    // ========================================================================
    // create
    // ========================================================================

    @Test
    @DisplayName("create: criptografa a senha ANTES de salvar e devolve o DTO do mapper")
    void create_whenPasswordIsProvided_savesEntityWithEncodedPassword() {

        // ARRANGE
        // Fluxo do Service:
        //   InsertDTOV2 --mapper--> Entity --encode(senha)--> save() --mapper--> DTOV2
        Person entity = input.mockEntity(1);
        PersonInsertDTOV2 insertDto = input.mockInsertDTO(1);
        PersonDTOV2 responseDto = input.mockDTO(1);

        when(mapper.mapDtoToEntity(insertDto)).thenReturn(entity);
        when(passwordEncoder.encode(insertDto.getPassword())).thenReturn("senha_hash_teste");
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(entity)).thenReturn(responseDto);

        // ACT
        PersonDTOV2 result = service.create(insertDto);

        // ASSERT
        assertSame(responseDto, result);

        // ArgumentCaptor "captura" o objeto que o Service passou ao repository.save().
        // Sem ele, só saberíamos que encode() foi chamado, mas NÃO que o hash
        // chegou na entidade salva. Se alguém apagasse o person.setPassword(...)
        // do Service, um teste só com verify(encode) continuaria verde. Este não.
        ArgumentCaptor<Person> savedCaptor = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(savedCaptor.capture());
        assertEquals("senha_hash_teste", savedCaptor.getValue().getPassword());

        // VERIFY - ORDEM DAS OPERAÇÕES
        // Regra de segurança: a senha precisa ser criptografada antes de persistir.
        // InOrder falha se save() acontecer antes de encode().
        InOrder order = inOrder(mapper, passwordEncoder, repository);
        order.verify(mapper).mapDtoToEntity(insertDto);
        order.verify(passwordEncoder).encode(insertDto.getPassword());
        order.verify(repository).save(entity);
        order.verify(mapper).mapEntityToDto(entity);
    }

    @Test
    @DisplayName("create: quando a Role vem nula, aplica a Role padrão CLIENT")
    void create_whenRoleIsNull_setsDefaultRoleClient() {

        // ARRANGE
        // Forçamos role = null na entidade devolvida pelo mapper para exercitar o
        // "if (person.getRole() == null)" do Service, que o caminho feliz pode nem tocar.
        Person entity = input.mockEntity(1);
        entity.setRole(null);
        PersonInsertDTOV2 insertDto = input.mockInsertDTO(1);

        when(mapper.mapDtoToEntity(insertDto)).thenReturn(entity);
        when(passwordEncoder.encode(insertDto.getPassword())).thenReturn("hash");
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(entity)).thenReturn(input.mockDTO(1));

        // ACT
        service.create(insertDto);

        // ASSERT
        // Capturamos a entidade que foi salva e conferimos a Role atribuída.
        ArgumentCaptor<Person> savedCaptor = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(savedCaptor.capture());
        assertEquals(PersonRole.CLIENT, savedCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("create: quando a Role já vem preenchida, NÃO sobrescreve com CLIENT")
    void create_whenRoleIsProvided_keepsOriginalRole() {

        // ARRANGE
        // Contraparte do teste anterior: o outro lado do mesmo "if".
        // Sem este teste, um Service que sempre forçasse CLIENT passaria despercebido
        // (e um usuário ADMIN viraria CLIENT).
        Person entity = input.mockEntity(1);
        entity.setRole(PersonRole.ADMIN);
        PersonInsertDTOV2 insertDto = input.mockInsertDTO(1);

        when(mapper.mapDtoToEntity(insertDto)).thenReturn(entity);
        when(passwordEncoder.encode(insertDto.getPassword())).thenReturn("hash");
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(entity)).thenReturn(input.mockDTO(1));

        // ACT
        service.create(insertDto);

        // ASSERT
        ArgumentCaptor<Person> savedCaptor = ArgumentCaptor.forClass(Person.class);
        verify(repository).save(savedCaptor.capture());
        assertEquals(PersonRole.ADMIN, savedCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("create: quando a senha é nula, não chama o PasswordEncoder")
    void create_whenPasswordIsNull_doesNotCallPasswordEncoder() {

        // ARRANGE
        // Exercita o "if (dto.getPassword() != null)" do Service.
        // Guardamos a senha original da entidade para provar que ela não foi alterada.
        Person entity = input.mockEntity(1);
        String passwordBefore = entity.getPassword();
        PersonInsertDTOV2 insertDto = input.mockInsertDTO(1);
        insertDto.setPassword(null);

        when(mapper.mapDtoToEntity(insertDto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(entity)).thenReturn(input.mockDTO(1));

        // ACT
        service.create(insertDto);

        // ASSERT / VERIFY
        // never() garante que a chamada NÃO aconteceu nenhuma vez.
        verify(passwordEncoder, never()).encode(any());
        assertEquals(passwordBefore, entity.getPassword());
    }

    // ========================================================================
    // update
    // ========================================================================

    @Test
    @DisplayName("update: busca, aplica o DTO na entidade existente, salva e devolve o DTO")
    void update_whenPersonExists_updatesSavesAndReturnsDto() {

        // ARRANGE
        // updateEntityFromDTO é um método void: um mock de método void já "não faz
        // nada" por padrão, então não precisamos de doNothing().
        Person entity = input.mockEntity(1);
        PersonUpdateDTOV2 updateDto = input.mockUpdateDTO(1);
        PersonDTOV2 responseDto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.mapEntityToDto(entity)).thenReturn(responseDto);

        // ACT
        PersonDTOV2 result = service.update(1L, updateDto);

        // ASSERT
        assertSame(responseDto, result);

        // VERIFY - ORDEM
        // O mapper precisa atualizar a entidade ANTES do save, senão salvaríamos
        // os dados antigos.
        InOrder order = inOrder(repository, mapper);
        order.verify(repository).findById(1L);
        order.verify(mapper).updateEntityFromDTO(updateDto, entity);
        order.verify(repository).save(entity);
        order.verify(mapper).mapEntityToDto(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    @DisplayName("update: quando o ID não existe, lança exceção e não salva nada")
    void update_whenPersonDoesNotExist_throwsAndDoesNotSave() {

        // ARRANGE
        PersonUpdateDTOV2 updateDto = input.mockUpdateDTO(1);
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, updateDto));

        // VERIFY
        // O mais importante do caminho de erro: nenhuma escrita aconteceu.
        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    // ========================================================================
    // delete
    // ========================================================================

    @Test
    @DisplayName("delete: busca a entidade e manda o repository removê-la")
    void delete_whenPersonExists_deletesEntity() {

        // ARRANGE
        Person entity = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // ACT
        service.delete(1L);

        // VERIFY
        // O método é void, então não há retorno para conferir: o que importa é
        // o efeito (delete) e a ordem (primeiro busca, depois remove).
        InOrder order = inOrder(repository);
        order.verify(repository).findById(1L);
        order.verify(repository).delete(entity);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("delete: quando o ID não existe, lança exceção e não remove nada")
    void delete_whenPersonDoesNotExist_throwsAndDoesNotDelete() {

        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));

        // VERIFY
        verify(repository, never()).delete(any());
    }
}

/*
 * ============================================================================
 * FORA DO ESCOPO DESTE TESTE: HATEOAS, STATUS HTTP E HEADERS
 * ============================================================================
 *
 * Links HATEOAS, códigos de status, headers e content negotiation são
 * responsabilidade do CONTROLLER, não do Service. Eles são testados com
 * @WebMvcTest + MockMvc, que sobem só a camada web e simulam requisições HTTP:
 *
 *   - existe o link rel="self"?
 *   - o href aponta para o recurso correto (ex.: /person/v2/1)?
 *   - GET / POST / PUT / DELETE respondem com o status esperado?
 *
 * Observação: o atributo "type" de um Link HATEOAS é o media type do recurso,
 * e não o verbo HTTP.
 *
 * Divisão de responsabilidades nos testes:
 *
 *   Service    -> regras de negócio            -> JUnit + Mockito (este arquivo)
 *   Controller -> contrato HTTP e links        -> @WebMvcTest + MockMvc
 *   Repository -> consultas e mapeamento JPA   -> @DataJpaTest (ou Testcontainers)
 * ============================================================================
 */