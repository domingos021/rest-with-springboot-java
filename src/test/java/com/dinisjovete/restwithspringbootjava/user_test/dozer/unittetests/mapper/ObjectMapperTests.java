package com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper;
/*
 * =====================================================================
 * TÍTULO DA CLASSE: PersonMapperTest
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Classe de testes unitários (JUnit 5) para validar a integridade e a
 * precisão do mapeamento realizado pelo `PersonMapper` (MapStruct).
 * Testa cenários de conversão bidirecional (Entity ➔ DTO e DTO ➔ Entity)
 * tanto para instâncias isoladas quanto para listas completas utilizando
 * massas de dados simuladas (`MockPerson`).
 * =====================================================================
 */

import static com.dinisjovete.restwithspringbootjava.mappers.dozer.ObjectMpper.parseObject;
import static com.dinisjovete.restwithspringbootjava.mappers.dozer.ObjectMpper.parseListObjects; // <-- ADICIONE ESTA IMPORTAÇÃO
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import com.dinisjovete.restwithspringbootjava.data.dto.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ObjectMapperTests {
    MockPerson inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockPerson();
    }


    /*
     * ============================================================================
     * TESTE: Entity -> DTO
     * ============================================================================
     *
     * Objetivo:
     * Verificar se o Dozer converte corretamente uma entidade Person
     * para um PersonDto_dozer.
     *
     * Fluxo:
     *
     * Person (Entity)
     *        |
     *        ▼
     * parseObject()
     *        |
     *        ▼
     * PersonDto_dozer (DTO)
     *        |
     *        ▼
     * assertEquals()
     *        |
     *        ▼
     * Confirma que todos os atributos foram copiados corretamente.
     *
     * OUTPUT => resultado
     * PersonDto_dozer output = ?
     * ============================================================================
     */

    /*
     * private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
     *
     * 1. mapper ──► Objeto que traz todas as funções de mapeamento da biblioteca Dozer.
     * 2. .map()  ──► Função do Dozer que intercepta os dados, lê os atributos
     *                da origem (OG) e aplica a conversão para o destino (DT).
     */
    /*
     * ========================================================================
     * EXPLICAÇÃO DO MÉTODO parseObject() E DO MAPPER DO DOZER
     * ========================================================================
     *
     * 1. mapper: Objeto do Dozer que traz as funções de mapeamento.
     *    - .map(): Função interna que aplica a conversão dos atributos
     *      do objeto de origem para o objeto de destino.
     *
     * 2. parseObject(): Método genérico da classe ObjectMapper que recebe dois parâmetros:
     *    ├── 1º (source)      ──► Objeto de origem que será convertido (ex: inputObject.mockEntity())
     *    └── 2º (destination) ──► Classe do objeto de destino (ex: PersonDto_dozer.class)

     * DE FORMA DIRETA: Converte a entidade invocando o ( mockEntity() ->| método que cria o objeto da entidade| esta na classe MockPerson )
     * para um objeto do tipo PersonDto_dozer.
     *
     * 3. Funcionamento:
     *    - O método parseObject() retorna uma instância do tipo de destino (DT ->|PersonDto_dozer.class|) com os dados mapeados de (OG ->|mockEntity()|).
     *    - O Dozer garante que os atributos correspondentes sejam copiados corretamente
     *      de forma automática, evitando código repetitivo de getters e setters.
     * *
     * *CONCLUSÃO
     * Neste teste unitário, o método |parseEntityToDTOTest()| valida se a conversão de uma entidade Person para um DTO PersonDto_dozer
     * ocorre corretamente, garantindo que todos os atributos sejam preservados e mapeados de forma adequada.
     * O uso do Dozer simplifica o processo de conversão, tornando o código mais limpo e menos propenso a erros.
     * *
     * PersonDto_dozer output = ? significa que o meto espera como resultado ou saída do método parseObject()
     * um objeto do tipo PersonDto_dozer, que é o DTO resultante da conversão da entidade Person.
     * ========================================================================
     */
    @Test
    public void parseEntityToDTOTest() {

        PersonDTO output = parseObject(
                inputObject.mockEntity(),    // de -> 1ª Entrada: Gera a Entidade Person (origem - OG)
                PersonDTO.class       //para -> 2ª Saída: Define a classe do DTO que queremos receber (destino - DT)
        );
        assertEquals(Long.valueOf(0L), output.getId());
        /*
         * TESTE DE ASSERÇÃO (assertEquals):
         * Compara o valor obtido após a conversão (output) com o valor esperado
         * da origem (source), garantindo que o Dozer mapeou o campo corretamente.
         */
        /*
         *assertEquals("First Name Test0", output.getFirstName());
         * garante que o atributo first Name do output(personDto_dozer) seja igual ao valor esperado "First Name Test0" da fonte(source -> mockEntity)
         */
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("000.000.000-00", output.getCpf());
        assertEquals("test0@email.com", output.getEmail()); // Ajustado para bater com o mock
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        // Converte a lista completa gerada pelo Mock (14 itens) para DTOs
        List<PersonDTO> outputList = parseListObjects(inputObject.mockEntityList(), PersonDTO.class);

        // Valida se a lista convertida tem o tamanho correto (14 elementos)
        assertEquals(14, outputList.size());

        // -----------------------------------------------------------------
        // Validação do Primeiro Elemento (Índice 0 - Par)
        // -----------------------------------------------------------------
        PersonDTO outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("000.000.000-00", outputZero.getCpf());
        assertEquals("test0@email.com", outputZero.getEmail());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        assertEquals(PersonRole.CLIENT, outputZero.getRole());

        // -----------------------------------------------------------------
        // Validação de um Elemento Intermediário (Índice 7 - Ímpar)
        // -----------------------------------------------------------------
        PersonDTO outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("000.000.007-00", outputSeven.getCpf()); // Ajustado para o índice 7
        assertEquals("test7@email.com", outputSeven.getEmail());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        assertEquals(PersonRole.ADMIN, outputSeven.getRole());

        // -----------------------------------------------------------------
        // Validação de um Elemento Final (Índice 12 - Par)
        // -----------------------------------------------------------------
        PersonDTO outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("000.000.0012-00", outputTwelve.getCpf());
        assertEquals("test12@email.com", outputTwelve.getEmail());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
        assertEquals(PersonRole.CLIENT, outputTwelve.getRole());
    }

    @Test
    public void parseDTOToEntityTest() {
        Person output = parseObject(inputObject.mockDTO(), Person.class);

        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("First Name Test0", output.getFirstName());
        assertEquals("Last Name Test0", output.getLastName());
        assertEquals("000.000.000-00", output.getCpf());
        assertEquals("test0@email.com", output.getEmail());
        assertEquals("Address Test0", output.getAddress());
        assertEquals("Male", output.getGender());
        assertEquals(PersonRole.CLIENT, output.getRole());
    }

    @Test
    public void parserDTOListToEntityListTest() {
        List<Person> outputList = parseListObjects(inputObject.mockDTOList(), Person.class);
        Person outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());

        Person outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        Person outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
}


/*
 * ========================================================================
 * DIAGRAMA DE FLUXO DE DADOS: DO MOCK À CONVERSÃO VIA DOZER
 * ========================================================================
 *
 * [1. CLASSE DE MOCK: MockPerson]
 *  - Cria dados fictícios de teste.
 *  - Gera instâncias únicas ou listas completas (ex: 14 itens).
 *  - Exemplos:
 *     ├── mockEntity() / mockEntity(i) ──► Retorna uma Entidade (Person)
 *     └── mockDTO() / mockDTO(i)       ──► Retorna um DTO (PersonDto_dozer)
 *           │
 *           │ (Os objetos mockados entram no fluxo de teste da aplicação)
 *           ▼
 * [2. CAMADA DE SERVIÇO / TESTE UNITÁRIO]
 *  - A aplicação precisa persistir, transacionar ou retornar os dados.
 *  - Ocorre a necessidade de transição entre o Domínio (Entity) e o Transporte (DTO).
 *           │
 *           │ 3. Identifica a necessidade de conversão e aciona a classe utilitária:
 *           ▼
 * ┌────────────────────────────────────────────────────────────────────────┐
 * │ 3. CLASSE UTILITÁRIA DE MAPEAMENTO: ObjectMpper                        │
 * │                                                                        │
 * │  👉 MOMENTO EXATO DA INTERCEPÇÃO:                                      │
 * │  - parseObject(source, destination)                                    │
 * │  - parseListObjectStream(source, destination)                          │
 * │                                                                        │
 * │    [ Intercepta o Objeto de Origem: OG ]                               │
 * │                  │                                                     │
 * │                  ▼                                                     │
 * │      ( Aciona o Dozer Mapper Core )                                    │
 * │      mapper.map(source, destination)                                   │
 * │                  │                                                     │
 * │                  ▼                                                     │
 * │    [ Converte e devolve o Objeto de Destino: DT ]                      │
 * └─────────────────┬──────────────────────────────────────────────────────┘
 *                   │
 *                   │ 4. Retorna o objeto convertido e pronto para uso
 *                   ▼
 * [5. DESTINO FINAL DOS DADOS]
 *  - Se converteu DTO ➔ Entity: Segue para salvar no Banco de Dados (Repository).
 *  - Se converteu Entity ➔ DTO: Segue para retornar a resposta JSON ao Cliente/API.
 * ========================================================================
 */