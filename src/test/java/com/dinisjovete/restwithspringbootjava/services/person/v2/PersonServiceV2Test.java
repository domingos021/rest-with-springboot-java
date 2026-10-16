package com.dinisjovete.restwithspringbootjava.services.person.v2;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.v2.PersonMapper_MapStructV2;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper.mocks.v2.MockPersonV2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


/*
 * ============================================================================
 * CLASSE DE TESTE UNITÁRIO - PERSON SERVICE V2
 * ============================================================================
 *
 * Objetivo:
 *
 * Testar isoladamente a camada de serviço (Service).
 *
 * O Service contém as regras de negócio da aplicação.
 *
 * Durante o teste:
 *
 *      Controller
 *          |
 *          X
 *          |
 *      Service  <------ Unidade testada
 *          |
 *          |
 *     -----------------
 *     |               |
 * Repository       Mapper
 *    Mock             Mock
 *
 *
 * O banco de dados e outras dependências externas não são utilizadas.
 *
 * O Mockito cria objetos simulados (Mocks) para controlar o comportamento
 * dessas dependências e permitir testar somente a lógica do Service.
 *
 * ============================================================================
 */


/*
 * ============================================================================
 * CICLO DE VIDA DA CLASSE DE TESTE
 * ============================================================================
 *
 * @TestInstance(TestInstance.Lifecycle.PER_CLASS)
 *
 * Define como o JUnit cria as instâncias da classe de teste.
 *
 *
 * PER_CLASS:
 *
 * O JUnit cria apenas uma única instância da classe de teste e reutiliza
 * essa mesma instância durante todos os métodos anotados com @Test.
 *
 *
 * Vantagens:
 *
 * - Permite compartilhar atributos entre métodos de teste.
 * - Permite utilizar @BeforeAll e @AfterAll sem precisar declarar métodos
 *   como static.
 *
 * ============================================================================
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)


/*
 * ============================================================================
 * INTEGRAÇÃO DO MOCKITO COM JUNIT 5
 * ============================================================================
 *
 * @ExtendWith(MockitoExtension.class)
 *
 * Faz a integração entre JUnit 5 e Mockito.
 *
 *
 * Responsabilidades:
 *
 * - Inicializar automaticamente objetos com @Mock.
 * - Criar objetos anotados com @InjectMocks.
 * - Gerenciar o ciclo de vida dos mocks durante os testes.
 *
 *
 * Sem essa anotação seria necessário inicializar manualmente:
 *
 * MockitoAnnotations.openMocks(this);
 *
 * ============================================================================
 */
@ExtendWith(MockitoExtension.class)
class PersonServiceV2Test {


    /*
     * ============================================================================
     * MASSA DE DADOS (TEST DATA BUILDER)
     * ============================================================================
     *
     * MockPersonV2 não é um Mock do Mockito.
     *
     * Ele é uma classe auxiliar responsável por criar objetos falsos
     * utilizados nos testes.
     *
     *
     * Exemplo:
     *
     * mockEntity(1)
     *
     * retorna uma entidade Person preenchida com dados previsíveis.
     *
     *
     * mockDTO(1)
     *
     * retorna um PersonDTOV2 preenchido com dados previsíveis.
     *
     *
     * Essa abordagem evita repetir a criação manual de objetos em todos
     * os métodos de teste.
     *
     * ============================================================================
     */
    MockPersonV2 input;



    /*
     * ============================================================================
     * UNIDADE SENDO TESTADA
     * ============================================================================
     *
     * @InjectMocks
     *
     * Informa ao Mockito que essa classe é o objeto principal do teste.
     *
     *
     * O Mockito:
     *
     * 1 - Cria uma instância real de PersonServiceV2.
     *
     * 2 - Procura dependências marcadas com @Mock.
     *
     * 3 - Injeta essas dependências dentro do Service.
     *
     *
     * Exemplo:
     *
     * PersonServiceV2
     *          |
     *          |
     *          +------ PersonRepository (Mock)
     *          |
     *          +------ PersonMapper (Mock)
     *
     *
     * O Service executa sua lógica real.
     * Apenas as dependências externas são simuladas.
     *
     * ============================================================================
     */
    @InjectMocks
    private PersonServiceV2 service;



    /*
     * ============================================================================
     * DEPENDÊNCIAS SIMULADAS PELO MOCKITO
     * ============================================================================
     *
     * @Mock
     *
     * Cria objetos falsos controlados pelo Mockito.
     *
     *
     * Esses objetos:
     *
     * - Não acessam banco de dados.
     * - Não executam implementação real.
     * - Permitem definir retornos esperados.
     *
     *
     * Assim conseguimos testar apenas a regra de negócio existente
     * dentro do PersonServiceV2.
     *
     * ============================================================================
     */
    @Mock
    PersonRepository repository;


    @Mock
    PersonMapper_MapStructV2 mapper;

    @Mock
    PasswordEncoder passwordEncoder;



    /*
     * ============================================================================
     * PREPARAÇÃO ANTES DE CADA TESTE
     * ============================================================================
     *
     * @BeforeEach
     *
     * Executa esse método antes da execução de cada método @Test.
     *
     *
     * Responsabilidade:
     *
     * Criar novamente a massa de dados para garantir que cada teste tenha
     * um ambiente independente.
     *
     * ============================================================================
     */
    @BeforeEach
    void setUp() {


        /*
         * Cria o objeto responsável por fornecer dados falsos para os testes.
         *
         * Ele será utilizado para gerar:
         *
         * - Entidades Person.
         * - DTOs PersonDTOV2.
         *
         */
        input = new MockPersonV2();

    }


    @Test
    void findById() {


        /*
         * ============================================================================
         * ETAPA 1 - ARRANGE (PREPARAÇÃO DO CENÁRIO)
         * ============================================================================
         *
         * Criação da massa de dados que será utilizada durante o teste.
         *
         *
         * mockEntity(1):
         *
         * Retorna uma entidade Person preenchida com valores fictícios.
         *
         * Essa entidade representa o objeto que normalmente viria do banco
         * de dados através do Repository.
         *
         *
         * mockDTO(1):
         *
         * Retorna um PersonDTOV2 preenchido com valores fictícios.
         *
         * Esse DTO representa o objeto que seria retornado para o Controller.
         *
         * ============================================================================
         */
        Person person = input.mockEntity(1);

        PersonDTOV2 personDTO = input.mockDTO(1);



        /*
         * ============================================================================
         * ETAPA 2 - ARRANGE (CONFIGURAÇÃO DOS MOCKS)
         * ============================================================================
         *
         * Configurando o comportamento do Mapper.
         *
         *
         * Cenário real:
         *
         * Service recebe uma entidade Person.
         *
         * Service chama o Mapper:
         *
         * PersonEntity
         *       |
         *       |
         *       v
         * PersonDTOV2
         *
         *
         * Como o Mapper é um Mock, precisamos informar manualmente
         * qual será o retorno esperado.
         *
         * ============================================================================
         */
        when(mapper.mapEntityToDto(person))
                .thenReturn(personDTO);



        /*
         * ============================================================================
         * ETAPA 3 - ARRANGE (CONFIGURAÇÃO DO REPOSITORY)
         * ============================================================================
         *
         * Configurando o comportamento do Repository.
         *
         *
         * Cenário real:
         *
         * Service solicita:
         *
         * repository.findById(1L)
         *
         *
         * Banco de dados retornaria:
         *
         * Person encontrada
         *
         *
         * No teste:
         *
         * O Mockito intercepta essa chamada e retorna o objeto
         * Person criado pelo MockPersonV2.
         *
         * ============================================================================
         */
        when(repository.findById(1L))
                .thenReturn(Optional.of(person));



        /*
         * ============================================================================
         * ETAPA 4 - ACT (EXECUÇÃO DO COMPORTAMENTO TESTADO)
         * ============================================================================
         *
         * Agora executamos o método real do Service.
         *
         *
         * Fluxo durante o teste:
         *
         *
         * service.findById(1L)
         *
         *          |
         *          v
         *
         * PersonServiceV2
         *
         *          |
         *          |
         *          +------ repository.findById(1L)
         *          |
         *          |        retorna Person (Mock)
         *          |
         *          |
         *          +------ mapper.mapEntityToDto(person)
         *
         *                   retorna PersonDTOV2 (Mock)
         *
         *
         * O Service executa a regra real, porém utilizando dependências
         * simuladas.
         *
         * ============================================================================
         */
        var result = service.findById(1L);



        /*
         * ============================================================================
         * ETAPA 5 - ASSERT (VALIDAÇÃO DO RESULTADO)
         * ============================================================================
         *
         * Aqui verificamos se o comportamento esperado realmente aconteceu.
         *
         * ============================================================================
         */


        /*
         * O resultado retornado pelo Service nunca deve ser nulo.
         */
        Assertions.assertNotNull(result);



        /*
         * O DTO retornado deve possuir um ID preenchido.
         */
        Assertions.assertNotNull(result.getId());



        /*
         * Confirma que o ID retornado pelo Service é exatamente o mesmo
         * ID existente no DTO preparado durante o cenário de teste.
         */
        Assertions.assertEquals(
                personDTO.getId(),
                result.getId()
        );



        /*
         * ============================================================================
         * ETAPA 6 - VERIFY (VALIDAÇÃO DAS INTERAÇÕES)
         * ============================================================================
         *
         * Além de verificar o resultado, podemos confirmar se as dependências
         * foram chamadas corretamente.
         *
         *
         * Aqui estamos garantindo que:
         *
         * 1 - O Service realmente chamou o Repository.
         *
         * 2 - O Service realmente chamou o Mapper.
         *
         * ============================================================================
         */
        verify(repository)
                .findById(1L);


        verify(mapper)
                .mapEntityToDto(person);

    }



    /*
     * ============================================================================
     * TESTES FUTUROS
     * ============================================================================
     *
     * Os próximos testes seguirão a mesma estrutura:
     *
     *
     * ARRANGE
     *
     * Criar dados e configurar mocks.
     *
     *
     * ACT
     *
     * Executar o método do Service.
     *
     *
     * ASSERT
     *
     * Validar o resultado.
     *
     *
     * VERIFY
     *
     * Confirmar chamadas importantes.
     *
     * ============================================================================
     */


    @Test
    void create() {

        /*
                      TESTE
                |
                v
         PersonInsertDTOV2
         (password = "password1")
                        |
                        |
                        v
                PersonServiceV2
                        |
                        |
               passwordEncoder.encode()
                        |
                        v
         Person Entity
         (password = "$2a$10$....")
                        |
                        |
                        v
                repository.save()
                        |
                        v
               PersonDTOV2 retorno
         * ============================================================================
         * ARRANGE - CRIAÇÃO DA MASSA DE DADOS
         * ============================================================================
         *
         * Criamos os objetos necessários para simular o fluxo de criação:
         *
         * PersonInsertDTOV2:
         * Representa os dados recebidos pelo Service.
         *
         * Person:
         * Representa a entidade que será persistida no banco.
         *
         * PersonDTOV2:
         * Representa o DTO que será retornado pela API.
         *
         * ============================================================================
         */

        Person person = input.mockEntity(1);

        PersonInsertDTOV2 personDTO = input.mockInsertDTO(1);

        PersonDTOV2 personDTO2 = input.mockDTO(1);



        /*
         * ============================================================================
         * ARRANGE - CONFIGURAÇÃO DO MAPPER (DTO -> ENTITY)
         * ============================================================================
         *
         * O Service recebe um DTO de entrada e converte para uma entidade.
         *
         * ============================================================================
         */

        when(mapper.mapDtoToEntity(personDTO))
                .thenReturn(person);



        /*
         * ============================================================================
         * ARRANGE - CONFIGURAÇÃO DO PASSWORD ENCODER
         * ============================================================================
         *
         * O Service recebe a senha original enviada pelo usuário:
         *
         * password = "password1"
         *
         * Antes de salvar, ele aplica BCrypt:
         *
         * passwordEncoder.encode(password)
         *
         * Aqui simulamos o retorno do hash gerado.
         *
         * ============================================================================
         */

        when(passwordEncoder.encode(personDTO.getPassword()))
                .thenReturn("senha_hash_teste");



        /*
         * ============================================================================
         * ARRANGE - CONFIGURAÇÃO DO REPOSITORY
         * ============================================================================
         *
         * O Repository recebe a entidade já processada pelo Service
         * e retorna a entidade salva.
         *
         * ============================================================================
         */

        when(repository.save(person))
                .thenReturn(person);



        /*
         * ============================================================================
         * ARRANGE - CONFIGURAÇÃO DO MAPPER (ENTITY -> DTO)
         * ============================================================================
         *
         * Após salvar, o Service converte novamente a entidade
         * para o DTO de resposta.
         *
         * ============================================================================
         */

        when(mapper.mapEntityToDto(person))
                .thenReturn(personDTO2);



        /*
         * ============================================================================
         * ACT - EXECUÇÃO DO MÉTODO REAL
         * ============================================================================
         */

        var result = service.create(personDTO);



        /*
         * ============================================================================
         * ASSERT - VALIDAÇÃO DO RESULTADO
         * ============================================================================
         */

        Assertions.assertNotNull(result);


        Assertions.assertEquals(
                personDTO2.getId(), // ID do DTO de saída esperado
                result.getId()      // ID retornado pelo Service
        );



        /*
         * ============================================================================
         * VERIFY - VALIDAÇÃO DAS INTERAÇÕES
         * ============================================================================
         *
         * Garantimos que o Service executou todas as etapas necessárias.
         *
         * ============================================================================
         */

        verify(mapper)
                .mapDtoToEntity(personDTO);


        verify(passwordEncoder)
                .encode(personDTO.getPassword());


        verify(repository)
                .save(person);


        verify(mapper)
                .mapEntityToDto(person);

    }



    @Test
    void update() {

        /*
         * ============================================================================
         * 1 - MASSA DE DADOS
         * ============================================================================
         */
        Person person = input.mockEntity(1);
        PersonUpdateDTOV2 userUpdate = input.mockUpdateDTO(1);
        PersonDTOV2 dtoUpdate = input.mockDTO(1);


        /*
         * ============================================================================
         * 2 - PREPARAÇÃO DO CENÁRIO (ARRANGE)
         * ============================================================================
         */

        // Busca a pessoa existente no banco
        when(repository.findById(1L))
                .thenReturn(Optional.of(person));

        // Atualiza os dados da entidade existente (método void)
        doNothing()
                .when(mapper)
                .updateEntityFromDTO(userUpdate, person);

        // Salva a entidade atualizada
        when(repository.save(person))
                .thenReturn(person);

        // Converte a entidade salva para DTO de saída
        when(mapper.mapEntityToDto(person))
                .thenReturn(dtoUpdate);


        /*
         * ============================================================================
         * 3 - EXECUÇÃO (ACT)
         * ============================================================================
         */
        var result = service.update(1L, userUpdate);


        /*
         * ============================================================================
         * 4 - VALIDAÇÃO (ASSERT)
         * ============================================================================
         */

        Assertions.assertNotNull(result);

        Assertions.assertNotNull(result.getId());

        Assertions.assertEquals(
                dtoUpdate.getId(),
                result.getId()
        );

        Assertions.assertEquals(
                dtoUpdate.getFirstName(),
                result.getFirstName()
        );

        Assertions.assertEquals(
                dtoUpdate.getLastName(),
                result.getLastName()
        );


        /*
         * ============================================================================
         * 5 - VERIFICAÇÃO DAS INTERAÇÕES (VERIFY)
         * ============================================================================
         */

        verify(repository).findById(1L);

        verify(mapper)
                .updateEntityFromDTO(userUpdate, person);

        verify(repository)
                .save(person);

        verify(mapper)
                .mapEntityToDto(person);
    }



    @Test
    void delete() {

        /*
         * ============================================================================
         * 1 - MASSA DE DADOS
         * ============================================================================
         */
        Person person = input.mockEntity(1);


        /*
         * ============================================================================
         * 2 - PREPARAÇÃO DO CENÁRIO (ARRANGE)
         * ============================================================================
         */

        // Simula a busca da pessoa existente
        when(repository.findById(1L))
                .thenReturn(Optional.of(person));


        /*
         * ============================================================================
         * 3 - EXECUÇÃO (ACT)
         * ============================================================================
         */

        service.delete(1L);


        /*
         * ============================================================================
         * 4 - VALIDAÇÃO DAS INTERAÇÕES (VERIFY)
         * ============================================================================
         *
         * Como delete() não retorna nada, validamos se o fluxo interno ocorreu:
         * 1 - Buscou a entidade pelo ID
         * 2 - Mandou o repository remover essa entidade
         *
         * ============================================================================
         */

        verify(repository)
                .findById(1L);

        verify(repository)
                .delete(person);
    }


    @Test
    void findAll() {

        /*
         * ============================================================================
         * 1 - MASSA DE DADOS
         * ============================================================================
         */

        Person person1 = input.mockEntity(1);
        Person person2 = input.mockEntity(2);

        PersonDTOV2 dto1 = input.mockDTO(1);
        PersonDTOV2 dto2 = input.mockDTO(2);


        List<Person> people = List.of(person1, person2);



        /*
         * ============================================================================
         * 2 - ARRANGE
         * ============================================================================
         */

        /*
         * Simula o Repository buscando todas as pessoas no banco.
         *
         * Fluxo real:
         *
         * Banco
         *   |
         *   v
         * Repository
         *   |
         *   v
         * List<Person>
         *
         */

        when(repository.findAll())
                .thenReturn(people);



        /*
         * O Mapper recebe cada entidade individualmente
         * e transforma em DTO.
         *
         * Person
         *   |
         *   v
         * PersonDTOV2
         *
         */

        when(mapper.mapEntityToDto(person1))
                .thenReturn(dto1);


        when(mapper.mapEntityToDto(person2))
                .thenReturn(dto2);



        /*
         * ============================================================================
         * 3 - ACT
         * ============================================================================
         */

        var result = service.findAll();



        /*
         * ============================================================================
         * 4 - ASSERT
         * ============================================================================
         */

        Assertions.assertNotNull(result);


        Assertions.assertEquals(
                2,
                result.size()
        );


        // valida primeiro elemento

        Assertions.assertEquals(
                dto1.getId(),
                result.get(0).getId()
        );


        Assertions.assertEquals(
                dto1.getFirstName(),
                result.get(0).getFirstName()
        );



        // valida segundo elemento

        Assertions.assertEquals(
                dto2.getId(),
                result.get(1).getId()
        );


        Assertions.assertEquals(
                dto2.getFirstName(),
                result.get(1).getFirstName()
        );



        /*
         * ============================================================================
         * 5 - VERIFY
         * ============================================================================
         */

        verify(repository)
                .findAll();


        verify(mapper)
                .mapEntityToDto(person1);


        verify(mapper)
                .mapEntityToDto(person2);
    }


}



/*
 * ============================================================================
 * TESTE DE HATEOAS - RESPONSABILIDADE DO CONTROLLER
 * ============================================================================
 *
 *
 * Observação importante:
 *
 * O teste de links HATEOAS NÃO pertence ao Service.
 *
 *
 * Motivo:
 *
 * O Service é responsável por:
 *
 * - Regras de negócio.
 * - Busca e transformação de dados.
 * - Comunicação com Repository e Mapper.
 *
 *
 * O Controller é responsável por:
 *
 * - HTTP.
 * - Status code.
 * - Headers.
 * - Content Negotiation.
 * - Representação final da resposta.
 * - Links HATEOAS.
 *
 *
 * Fluxo:
 *
 *
 * Controller
 *      |
 *      |
 *      v
 * PersonHateoasAssembler
 *      |
 *      |
 *      v
 * PersonDTO + Links
 *
 *
 * Portanto, a validação dos links deve ser feita utilizando:
 *
 * @WebMvcTest
 * MockMvc
 *
 *
 * Exemplo de validação:
 *
 *
 * Assertions:
 *
 * - Verificar se existe coleção de links.
 * - Verificar se existe link "self".
 * - Verificar se o href aponta para o recurso correto.
 *
 *
 * Exemplo:
 *
 *
 * result.getLinks()
 *
 * deve possuir:
 *
 * rel = self
 *
 * href = /person/v2/1
 *
 *
 * Observação:
 *
 * O atributo "type" do Link HATEOAS não representa verbo HTTP.
 *
 * Ele representa o Media Type associado ao recurso.
 *
 *
 * Verbos HTTP como:
 *
 * GET
 * POST
 * PUT
 * DELETE
 *
 *
 * Devem ser testados na camada Controller através do MockMvc.
 *
 * ============================================================================
 */