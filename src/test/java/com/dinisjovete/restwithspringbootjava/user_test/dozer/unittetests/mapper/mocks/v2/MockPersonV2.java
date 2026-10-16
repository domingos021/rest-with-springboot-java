package com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper.mocks.v2;

import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/*
- Mock para PersonDTOV2.

 *
- Representa o contrato da API na versão 2.

- Possui campos novos como birthDate e phone,

- que serão incorporados posteriormente na entidade Person.

 */
public class MockPersonV2 {

    /*
- ========================================================================

- SOBRECARGA DE MÉTODO (Method Overloading) - 1ª Versão

- ========================================================================

- mockEntity() sem parâmetros:

- Atua como um atalho que invoca a 2ª versão do método mockEntity(Integer),

- passando 0 como argumento para criar uma instância padrão com índice 0.

- *

- versão 1 - o próprio método sem argumento| public Person mockEntity()

- versão 2 - o próprio método com argumento| return mockEntity(0) -> cria a entidade person(objeto)

-                         *

- SOBRECARGA DE MÉTODO

-  Consiste no seguinte: dois métodos com o mesmo nome, mas com assinaturas diferentes (número ou tipo de parâmetros).

- ambos trabalhando juntos, mas cada um com sua própria lógica.

- O Java identifica automaticamente qual versão chamar com base nos argumentos fornecidos.

- *

- Este Exemplo:

-    1. mockEntity() sem parâmetros → chama mockEntity(0) internamente.

     */
    public Person mockEntity() {
        return mockEntity(0); // O Java identifica automaticamente que deve chamar a versão com Integer
    }

    /*
- mockDTO() sem parâmetros:

- Retorna uma instância padrão de PersonInsertDTO com índice 0.

     */
    public PersonDTOV2 mockDTO() {
        return mockDTO(0);
    }

    /*
- ========================================================================

- ABORDAGEM ALTERNATIVA (Demonstração sem Sobrecarga)

- ========================================================================

- Este método faz exatamente a mesma coisa que o mockEntity(Integer),

- mas utiliza um nome diferente para demonstrar que poderíamos resolver

- o problema sem aplicar o conceito de Method Overloading.

     */
    public Person mockEntityAlternativoSemSobrecarga(Integer number) {
        Person person01 = new Person();

        person01.setId(number.longValue());
        person01.setFirstName("First Name Test" + number);
        person01.setLastName("Last Name Test" + number);

        /*
- Formatação do CPF utilizando String.format().

         *
- %03d significa:

- %  -> indica que será aplicada uma formatação

- 0  -> completa com zeros à esquerda caso faltem caracteres

- 3  -> quantidade mínima de posições reservadas

- d  -> valor numérico inteiro

         *
- Exemplos:

- number = 0  -> 000.000.000-00

- number = 1  -> 000.000.001-00

- number = 12 -> 000.000.012-00

         *
- Dessa forma o CPF do mock mantém sempre o mesmo padrão.

         */
        person01.setCpf(String.format("000.000.%03d-00", number));

        person01.setEmail("test" + number + "@email.com");
        person01.setPassword("password" + number);
        person01.setAddress("Address Test" + number);

        person01.setGender(
                ((number % 2) == 0) ? "Male" : "Female"
        );

        // Atribui uma Role alternada ou padrão baseada no número
        person01.setRole(
                (number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN
        );

        return person01;
    }

    /*
- ========================================================================

- FLUXO DO LAÇO FOR EM mockEntityList()

- ========================================================================

- Objetivo: Popular uma lista com 14 instâncias diferentes de Person.

     *
- Como o laço funciona:

- Inicializa uma lista vazia (ArrayList).

- 2. O contador 'i' começa em 0 e vai até 13 (i < 14), totalizando 14 voltas.

- 3. Em cada iteração, chamamos a versão sobrecarregada mockEntity(i):

-    - i = 0  → mockEntity(0)  → Person 0 adicionada à lista

-    - i = 1  → mockEntity(1)  → Person 1 adicionada à lista

-    - ...

-    - i = 13 → mockEntity(13) → Person 13 adicionada à lista

- 4. Retorna a lista pronta preenchida com os dados dinâmicos.

     */
    public List<Person> mockEntityList() {
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {

            // Invoca a versão do método mockEntity() que recebe um argumento Integer.
            // Neste caso, o argumento utilizado é o índice atual 'i'.
            // O método cria uma nova instância de Person com valores de teste
            // baseados no valor recebido em 'i'.
            // O objeto Person retornado é adicionado à lista persons na próxima
            // posição disponível. Como os objetos são adicionados sequencialmente
            // a partir de uma lista vazia, a posição acaba coincidindo com o índice 'i'.

            persons.add(mockEntity(i));
        }

        return persons;
    }

    /*
- ========================================================================

- FLUXO DO LAÇO FOR EM mockDTOList() (Retorna PersonDTO Completo com ID)

- ========================================================================

- Objetivo: Popular uma lista com 14 instâncias diferentes de PersonDTO.

     *
- Utilizado especialmente nos testes de conversão de lista de DTO para Entity

- (como no parserDTOListToEntityListTest), onde os IDs são validados.

     */
    public List<PersonDTOV2> mockDTOList() {
        List<PersonDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockDTO(i));
        }

        return persons;
    }

    /*
- ========================================================================

- FLUXO DO LAÇO FOR EM mockInsertDTOList() (Retorna PersonInsertDTO)

- ========================================================================

- Objetivo: Popular uma lista com 14 instâncias de PersonInsertDTO (sem ID),

- ideal para testes de fluxo de criação (Insert).

     */
    public List<PersonInsertDTOV2> mockInsertDTOList() {
        List<PersonInsertDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockInsertDTO(i));
        }

        return persons;
    }

    /*
- ========================================================================

- FLUXO DO LAÇO FOR EM mockUpdateDTOList() (Retorna PersonUpdateDTOV2)

- ========================================================================

- Objetivo: Popular uma lista com 14 instâncias de PersonUpdateDTOV2,

- ideal para testes de fluxo de atualização (Update / PUT).

     */
    public List<PersonUpdateDTOV2> mockUpdateDTOList() {
        List<PersonUpdateDTOV2> persons = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            persons.add(mockUpdateDTO(i));
        }

        return persons;
    }

    /*
- ========================================================================

- SOBRECARGA DE MÉTODO (Method Overloading) - 2ª Versão

- ========================================================================

- Este método possui exatamente o mesmo nome conceitual (mockEntity),

- mas diferencia-se pela assinatura ao receber um parâmetro (Integer number).

- O argumento 'number' atua como identificador para gerar valores únicos

- (ID, nomes, emails, CPFs) para cada instância gerada.

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
        // Atribui uma Role alternada ou padrão baseada no número
        person.setRole((number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN);
        return person;
    }

    /**
     -
     Mock para PersonDTO de saída completo (inclui ID para validação em testes de conversão)
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
- Mock para PersonInsertDTO (sem ID, focado em cenários de cadastro/inserção)

     */
    public PersonInsertDTOV2 mockInsertDTO(Integer number) {
        PersonInsertDTOV2 person = new PersonInsertDTOV2();
        person.setFirstName("First Name Test" + number);
        person.setLastName("Last Name Test" + number);
        person.setBirthDate(LocalDate.of(1995, 10, 20));
        person.setCpf(String.format("000.000.%03d-00", number));
        person.setPhone("1234567890" + number);
        person.setEmail("test" + number + "@email.com");
        person.setPassword("password" + number);
        person.setAddress("Address Test" + number);
        person.setGender(((number % 2) == 0) ? "Male" : "Female");
        person.setRole((number % 2 == 0) ? PersonRole.CLIENT : PersonRole.ADMIN);
        return person;
    }

    /*
- Mock para PersonUpdateDTOV2 (focado em cenários de atualização/PUT)

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


/*
- Geração dinâmica do CPF utilizando o índice (`number`).

- Por que fazer isso?

- Unicidade: Garante que cada objeto gerado na lista tenha um CPF

-    diferente, evitando conflitos de chave única em cenários reais.

- 2. Previsibilidade: Mantém os testes determinísticos, permitindo

-    saber exatamente qual CPF corresponderá a cada índice

-    (ex: índice 7 gera "000.000.007-00").

- 3. Formato Padrão: Usa %03d para manter sempre 3 dígitos no bloco,

-    assegurando consistência visual em toda a massa de dados de teste.

 *
-   person.setCpf(String.format("000.000.%03d-00", number));

 */