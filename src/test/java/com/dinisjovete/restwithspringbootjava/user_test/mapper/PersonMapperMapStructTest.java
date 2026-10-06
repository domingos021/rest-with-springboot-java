package com.dinisjovete.restwithspringbootjava.user_test.mapper;

/*
 * =====================================================================
 * TÍTULO DA CLASSE: PersonMapperMapStructTest
 * ---------------------------------------------------------------------
 * O QUE ELA FAZ:
 * Classe de testes unitários (JUnit 5) para validar a integridade e a
 * precisão do mapeamento realizado pelo `PersonMapper_MapStruct`.
 * Testa cenários de conversão bidirecional (Entity ➔ DTO e DTO ➔ Entity)
 * tanto para instâncias isoladas quanto para listas completas utilizando
 * a massa de dados simulada (`MockPerson`) e o `PersonDTO` unificado.
 * =====================================================================
 */

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.mappers.mapperstruct.v1.PersonMapper_MapStruct;
import com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull; // Import necessário para validar o ID nulo na criação

public class PersonMapperMapStructTest {

    MockPerson inputObject; //→ É a fonte que fornece a massa de dados simulada (o nosso "gabarito").  o mockPerson: cria uma instançia do tipo person

    PersonMapper_MapStruct mapper;// → É a ferramenta (gerada pelo MapStruct) que traz todas as funções de conversão entre Entity e DTO.

    /*
     * @BeforeEach: Anotação do JUnit que indica que este método deve ser
     * executado automaticamente antes de CADA método de teste (@Test).
     *
     * Função: Inicializa o mock e o mapper para garantir um ambiente
     * sempre limpo, atualizado e isolado para cada execução.
     */
    @BeforeEach
    public void setUp() {
        // Instancia um novo objeto MockPerson antes de cada teste.
        // Isso garante que cada método de teste receba uma massa de dados nova,
        // limpa e totalmente isolada, evitando interferências entre os testes.
        inputObject = new MockPerson();

        // Instancia o mapper gerado pelo MapStruct para o ambiente de testes unitários.
        // É este objeto que traz todas as funções de conversão (Entity <-> DTO).
        mapper = Mappers.getMapper(PersonMapper_MapStruct.class);
    }

    @Test
    public void parseEntityToDTOTest() {

        /*
         * -------------------------------------------------------------------------
         * FLUXO DO TESTE DE CONVERSÃO (Entity ➔ DTO)
         * -------------------------------------------------------------------------
         * 1. Person input = inputObject.mockEntity();
         *    Gera a entidade de origem com dados de teste conhecidos (ex: ID 0,
         *    CPF "000.000.000-00"). Este objeto funciona como o nosso "gabarito".
         *
         * 2. PersonDTO output = mapper.mapEntityToDto(input);
         *    O mapper converte os dados do input para DTO, linha por linha.
         *    O resultado armazenado em 'output' é o "valor atual/real" gerado.
         *
         * 3. O assertEquals atua como a pergunta de validação:
         *    "Mapper, o objeto que você converteu tem os valores exatamente iguais
         *    aos valores originais do input?"
         *    Se baterem perfeitamente, o teste passa, provando que a conversão
         *    foi 100% fiel.
         * -------------------------------------------------------------------------
         */
        /*
         * Person input = inputObject.mockEntity();
         * - 'inputObject' é a instância da nossa classe de apoio (MockPerson).
         * - O método .mockEntity() retorna um objeto do tipo Person (Entity),
         *   garantido pela assinatura do método lá na classe MockPerson.
         */
        Person input = inputObject.mockEntity();

        // Verifica o CPF da entidade antes do MapStruct-> debug
        System.out.println("Entity EMAIL: " + input.getEmail()); // mostra a entidade criada filtrando apenas o email

        //conversão de entity(input) para Dto
        PersonDTO output = mapper.mapEntityToDto(input);
        // Verifica o CPF da entidade antes do MapStruct-> debug
        System.out.println("DTO EMAIL: " + output.getEmail()); // mostra a entidade criada filtrando apenas o email

        /*
         * assertEquals(expected:-> "000.000.000-00", output.getCpf());
         * assertEquals -> compara o valor esperado (1º argumento) com o valor real obtido (2º argumento).
         * Se os valores forem iguais, o teste passa; caso contrário, ele falha e lança uma exceção de asserção.
         * Ex:
         *   - 1º argumento (Esperado): "000.000.000-00" (o valor que definimos como padrão no mock/esperado).
         *   - 2º argumento (Atual): output.getCpf() (o valor real gerado após a conversão do mapper).
         */

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
    public void parseEntityListToDTOListTest() {

        //--------------------Debugando erro de igualdade do assert equals do CPF entre Entity e DTO--------------------
        // Cria a lista de entidades apenas uma vez para teste de debug-> system
        List<Person> inputList = inputObject.mockEntityList();

        // Verifica o CPF da entidade antes do MapStruct-> debug
        System.out.println("Entity CPF: " + inputList.get(12).getCpf()); // mostra a lista criada filtrando apenas o cpf

        // Converte a lista de Entity para DTO
        List<PersonDTO> outputList = mapper.mapEntityListToDtoList(inputList);

        // Verifica o CPF do DTO após o MapStruct debug
        System.out.println("DTO CPF: " + outputList.get(12).getCpf());// mostra a lista convertida filtrando o cpf da nova lista de DTO para ver se e igual ao da entity list

      // ---------------------------------fim do debugger----------------------------------------------------
        assertEquals(14, outputList.size());

        // Validação do Primeiro Elemento (Índice 0 - Par)
        PersonDTO outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("000.000.000-00", outputZero.getCpf());
        assertEquals("test0@email.com", outputZero.getEmail());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());
        assertEquals(PersonRole.CLIENT, outputZero.getRole());

        // Validação de um Elemento Intermediário (Índice 7 - Ímpar)
        PersonDTO outputSeven = outputList.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("000.000.007-00", outputSeven.getCpf());
        assertEquals("test7@email.com", outputSeven.getEmail());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());
        assertEquals(PersonRole.ADMIN, outputSeven.getRole());

        // Validação de um Elemento Final (Índice 12 - Par)
        PersonDTO outputTwelve = outputList.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("000.000.012-00", outputTwelve.getCpf());
        assertEquals("test12@email.com", outputTwelve.getEmail());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
        assertEquals(PersonRole.CLIENT, outputTwelve.getRole());
    }

    @Test
    public void parseDTOToEntityTest() {
        PersonInsertDTO input = inputObject.mockInsertDTO(0); // Usando o mock de insert correto
        System.out.println("DTO ENTIDADE : " + input); // Debug: Verifica o DTO de inserção

        Person output = mapper.mapDtoToEntity(input);
        System.out.println("ENTIDADE CRIADA : " + output); // Debug: Verifica a entidade criada

        // Na criação (Insert), a entidade nova ainda não possui ID gerado (deve ser null)
        assertNull(output.getId());

        // Valida se os dados estruturais do DTO de inserção foram copiados corretamente
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
        // Converte a lista de DTOs para uma lista de Entities
        List<Person> outputList = mapper.mapDtoListToEntityList(inputObject.mockDTOList());

        // Pega os itens específicos que serão validados
        Person outputZero = outputList.get(0);
        Person outputSeven = outputList.get(7);
        Person outputTwelve = outputList.get(12);

        // Debug visual no console
        System.out.println("--- DEBUG: LISTA CONVERTIDA (DTO -> Entity) ---");
        System.out.println("Índice 0  : " + outputZero);
        System.out.println("Índice 7  : " + outputSeven);
        System.out.println("Índice 12 : " + outputTwelve);
        System.out.println("----------------------------------------------");

        // Validações do índice 0
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("First Name Test0", outputZero.getFirstName());
        assertEquals("Last Name Test0", outputZero.getLastName());
        assertEquals("Address Test0", outputZero.getAddress());
        assertEquals("Male", outputZero.getGender());

        // Validações do índice 7
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("First Name Test7", outputSeven.getFirstName());
        assertEquals("Last Name Test7", outputSeven.getLastName());
        assertEquals("Address Test7", outputSeven.getAddress());
        assertEquals("Female", outputSeven.getGender());

        // Validações do índice 12
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("First Name Test12", outputTwelve.getFirstName());
        assertEquals("Last Name Test12", outputTwelve.getLastName());
        assertEquals("Address Test12", outputTwelve.getAddress());
        assertEquals("Male", outputTwelve.getGender());
    }
}