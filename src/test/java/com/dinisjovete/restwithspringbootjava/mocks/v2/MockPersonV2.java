package com.dinisjovete.restwithspringbootjava.mocks.v2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/*
 * ============================================================================
 * MASSA DE DADOS PARA TESTES - MockPersonV2
 * ============================================================================
 *
 * O QUE É
 * -------
 * Uma fábrica de objetos de teste (Test Data Builder). NÃO é um mock do
 * Mockito: não simula comportamento, apenas devolve objetos REAIS preenchidos
 * com valores previsíveis.
 *
 *
 * O QUE ELA FABRICA
 * -----------------
 *
 *      MockPersonV2
 *          |
 *          +------ mockEntity(n)      -> Person              (entidade, com senha)
 *          +------ mockDTO(n)         -> PersonDTOV2         (resposta da API, com ID, sem senha)
 *          +------ mockInsertDTO(n)   -> PersonInsertDTOV2   (entrada do POST, sem ID, com senha)
 *          +------ mockUpdateDTO(n)   -> PersonUpdateDTOV2   (entrada do PUT, só campos editáveis)
 *
 * Cada método também tem uma versão em lista (mockEntityList, mockDTOList...)
 * com 14 elementos, usada nos testes de conversão em lote.
 *
 *
 * ONDE É USADA
 * ------------
 * - Testes dos Mappers V2 (conversão DTO <-> Entity).
 * - Testes do Service (PersonServiceV2Test).
 *
 *
 * V1 x V2
 * -------
 * Esta classe representa o contrato da API na versão 2. Em relação à V1, os
 * DTOs ganham os campos birthDate e phone.
 *
 * Atenção: mockEntity continua devolvendo a entidade Person da V1, que ainda
 * NÃO possui esses dois campos (eles serão incorporados à entidade
 * posteriormente). Por isso:
 *
 *      mockDTO / mockInsertDTO / mockUpdateDTO  -> TÊM birthDate e/ou phone
 *      mockEntity                               -> NÃO tem
 *
 *
 * PADRÃO DOS DADOS GERADOS
 * ------------------------
 * Todos os valores derivam do número (n) recebido, o que torna os testes
 * DETERMINÍSTICOS: sabemos de antemão o que cada índice produz.
 *
 *      n = 0                              n = 1
 *      ---------------------------------  ---------------------------------
 *      id        = 0                      id        = 1
 *      firstName = "First Name Test0"     "First Name Test1"
 *      cpf       = 000.000.000-00         000.000.001-00
 *      phone     = "12345678900"          "12345678901"
 *      email     = test0@email.com        test1@email.com
 *      password  = "password0"            "password1"
 *      gender    = Male                   Female
 *      role      = CLIENT                 ADMIN
 *
 *      birthDate = 1995-10-20 em TODOS os índices (valor fixo, não depende de n)
 *
 * Regra de paridade:
 *
 *      n par   -> gender = "Male",   role = CLIENT
 *      n ímpar -> gender = "Female", role = ADMIN
 *
 * Atenção: isso significa que mockEntity(1) NUNCA vem com role nula. Para
 * testar o ramo "role nula" é preciso forçar com setRole(null) no teste.
 *
 *
 * CPF DINÂMICO: String.format("000.000.%03d-00", n)
 * -------------------------------------------------
 *
 *      %03d significa:
 *
 *      %   -> inicia uma formatação
 *      0   -> completa com zeros à esquerda quando faltarem dígitos
 *      3   -> reserva no mínimo 3 posições
 *      d   -> valor inteiro decimal
 *
 *      n = 0  -> 000.000.000-00
 *      n = 1  -> 000.000.001-00
 *      n = 12 -> 000.000.012-00
 *
 *      Por que gerar assim?
 *
 *      - Unicidade: cada objeto da lista tem um CPF diferente, evitando
 *        conflito de chave única em cenários com banco.
 *      - Previsibilidade: o índice 7 sempre gera "000.000.007-00".
 *      - Formato padrão: o bloco final sempre tem 3 dígitos.
 *
 * ============================================================================
 */
public class MockPersonV2 {


    /*
     * ============================================================================
     * SOBRECARGA DE MÉTODO (Method Overloading) - 1ª VERSÃO
     * ============================================================================
     *
     * mockEntity() SEM parâmetros.
     *
     * É um atalho: chama a segunda versão do método (mockEntity(Integer))
     * passando 0 como índice padrão.
     *
     *
     * O que é sobrecarga?
     *
     * Dois ou mais métodos com o MESMO NOME, mas com ASSINATURAS diferentes
     * (quantidade ou tipo dos parâmetros). Cada um tem sua própria lógica e o
     * Java escolhe qual executar olhando os argumentos da chamada.
     *
     *
     * Neste caso:
     *
     *      mockEntity()         ->  versão 1: sem argumento
     *            |
     *            v
     *      mockEntity(0)        ->  versão 2: recebe o índice e cria a Person
     *
     * ============================================================================
     */
    public Person mockEntity() {
        return mockEntity(0); // o Java identifica sozinho que deve chamar a versão com Integer
    }


    /*
     * mockDTO() SEM parâmetros.
     *
     * Atalho para mockDTO(0): devolve um PersonDTOV2 padrão com índice 0.
     */
    public PersonDTOV2 mockDTO() {
        return mockDTO(0);
    }


    /*
     * ============================================================================
     * ABORDAGEM ALTERNATIVA - SEM SOBRECARGA (APENAS DEMONSTRATIVA)
     * ============================================================================
     *
     * Faz exatamente o mesmo que mockEntity(Integer), mas com um nome diferente.
     *
     * Serve para mostrar que o problema também poderia ser resolvido SEM
     * sobrecarga, usando um nome distinto para cada variação. A vantagem da
     * sobrecarga é manter uma API única e fácil de lembrar: sempre mockEntity.
     *
     * A formatação do CPF (%03d) está explicada no cabeçalho desta classe.
     *
     * ============================================================================
     */
    public Person mockEntityAlternativoSemSobrecarga(Integer number) {
        Person person01 = new Person();

        person01.setId(number.longValue());
        person01.setFirstName("First Name Test" + number);
        person01.setLastName("Last Name Test" + number);
        person01.setCpf(String.format("000.000.%03d-00", number));
        person01.setEmail("test" + number + "@email.com");
        person01.setPassword("password" + number);
        person01.setAddress("Address Test" + number);

        person01.setGender(
                ((number % 2) == 0) ? "Male" : "Female"
        );

        // Role alternada pela paridade do número (par = CLIENT, ímpar = ADMIN)
        person01.setRole(
                (number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN
        );

        return person01;
    }


    /*
     * ============================================================================
     * LISTAS DE MASSA DE DADOS (14 ELEMENTOS)
     * ============================================================================
     *
     * Os quatro métodos de lista abaixo seguem o MESMO laço:
     *
     *      1 - Cria uma lista vazia (ArrayList).
     *
     *      2 - O contador i começa em 0 e vai até 13 (i < 14): 14 voltas.
     *
     *      3 - A cada volta chama a fábrica individual com o índice i:
     *
     *              i = 0   -> mockXxx(0)   -> objeto 0  adicionado
     *              i = 1   -> mockXxx(1)   -> objeto 1  adicionado
     *              ...
     *              i = 13  -> mockXxx(13)  -> objeto 13 adicionado
     *
     *      4 - Devolve a lista pronta.
     *
     * Como a lista começa vazia e recebe um objeto por volta, a POSIÇÃO do
     * objeto na lista coincide com o índice i. Assim, lista.get(5) contém
     * o objeto gerado com mockXxx(5).
     *
     * ============================================================================
     */


    /*
     * mockEntityList() -> List<Person>
     *
     * 14 entidades Person (com senha). Útil nos testes de conversão
     * Entity -> DTO em lote.
     */
    public List<Person> mockEntityList() {
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            // chama a versão sobrecarregada mockEntity(Integer) com o índice atual
            persons.add(mockEntity(i));
        }

        return persons;
    }


    /*
     * mockDTOList() -> List<PersonDTOV2>
     *
     * 14 DTOs de resposta COM ID. Usado principalmente nos testes de conversão
     * de lista de DTO para Entity (como parserDTOListToEntityListTest), onde os
     * IDs são validados.
     */
    public List<PersonDTOV2> mockDTOList() {
        List<PersonDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockDTO(i));
        }

        return persons;
    }


    /*
     * mockInsertDTOList() -> List<PersonInsertDTOV2>
     *
     * 14 DTOs de entrada SEM ID. Ideal para os testes do fluxo de criação
     * (Insert / POST).
     */
    public List<PersonInsertDTOV2> mockInsertDTOList() {
        List<PersonInsertDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockInsertDTO(i));
        }

        return persons;
    }


    /*
     * mockUpdateDTOList() -> List<PersonUpdateDTOV2>
     *
     * 14 DTOs com apenas os campos editáveis. Ideal para os testes do fluxo de
     * atualização (Update / PUT) na V2.
     */
    public List<PersonUpdateDTOV2> mockUpdateDTOList() {
        List<PersonUpdateDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockUpdateDTO(i));
        }

        return persons;
    }


    /*
     * ============================================================================
     * SOBRECARGA DE MÉTODO (Method Overloading) - 2ª VERSÃO
     * ============================================================================
     *
     * mockEntity(Integer number) -> Person
     *
     * Mesmo nome da primeira versão, assinatura diferente (recebe um parâmetro).
     *
     * O argumento "number" funciona como identificador: a partir dele são
     * gerados valores únicos e previsíveis de ID, nome, CPF e e-mail
     * (veja o padrão no cabeçalho da classe).
     *
     * Representa a entidade como ela existe no banco, por isso INCLUI a senha.
     * Não possui birthDate nem phone: a entidade ainda não tem esses campos.
     *
     * ============================================================================
     */
    public Person mockEntity(Integer number) {
        Person person = new Person();
        person.setId(number.longValue());
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setCpf(String.format("000.000.%03d-00", number));
        person.setEmail("test" + number + "@email.com");
        person.setPassword("password" + number);
        person.setAddress("Address Test" + number);
        person.setGender(((number % 2) == 0) ? "Male" : "Female");
        // Role alternada pela paridade do número (par = CLIENT, ímpar = ADMIN)
        person.setRole((number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN);
        return person;
    }


    /**
     * mockDTO(Integer number) -> PersonDTOV2
     *
     * DTO de RESPOSTA completo: inclui o ID, para ser validado nos testes de
     * conversão, e NÃO inclui senha, porque a API nunca expõe esse dado.
     * Traz também os campos novos da V2: birthDate (fixo em 1995-10-20) e
     * phone ("1234567890" + number).
     */
    public PersonDTOV2 mockDTO(Integer number) {

        PersonDTOV2 person = new PersonDTOV2();

        person.setId(number.longValue()); // ID_usuário
        person.setFirstName("First Name Test" + number); // Nome
        person.setLastName("Last Name Test" + number); // Sobrenome
        person.setBirthDate(LocalDate.of(1995, 10, 20)); // Data_nascimento
        person.setCpf(String.format("000.000.%03d-00", number)); // CPF
        person.setPhone("1234567890" + number); // Telefone
        person.setEmail("test" + number + "@email.com"); // Email
        person.setAddress("Address Test" + number); // Endereço
        person.setGender(((number % 2) == 0) ? "Male" : "Female");
        person.setRole((number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN); // Perfil

        return person;
    }


    /*
     * mockInsertDTO(Integer number) -> PersonInsertDTOV2
     *
     * DTO de ENTRADA para cadastro: sem ID (quem gera é o banco) e COM senha em
     * texto puro ("password" + number), que o Service irá criptografar antes
     * de salvar. Inclui os campos novos da V2 (birthDate e phone).
     */
    public PersonInsertDTOV2 mockInsertDTO(Integer number) {
        PersonInsertDTOV2 person = new PersonInsertDTOV2();
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setBirthDate(LocalDate.of(1995, 10, 20));
        person.setCpf(String.format("000.000.%03d-00", number));
        person.setPhone("1234567890" + number);
        person.setEmail("test" + number + "@email.com");
        person.setPassword("Password@" + number);
        person.setAddress("Address Test" + number);
        person.setGender(((number % 2) == 0) ? "Male" : "Female");
        person.setRole((number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN);
        return person;
    }


    /*
     * mockUpdateDTO(Integer number) -> PersonUpdateDTOV2
     *
     * DTO de ENTRADA para atualização (PUT) na V2: contém somente os campos
     * que a API permite alterar. Em relação à V1, ganha o phone. Não tem ID,
     * CPF, e-mail, senha, data de nascimento nem role.
     */
    public PersonUpdateDTOV2 mockUpdateDTO(Integer number) {
        PersonUpdateDTOV2 person = new PersonUpdateDTOV2();
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setPhone("1234567890" + number);
        person.setAddress("Address Test" + number);
        person.setGender(((number % 2) == 0) ? "Male" : "Female");
        return person;
    }
}