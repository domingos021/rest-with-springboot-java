package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * ============================================================================
 * TESTE DE UNIDADE - PersonInsertDTOV2 (v2)
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * Este DTO é a PORTA DE ENTRADA da API (POST e PUT). Ele não tem regra de
 * negócio, mas carrega as regras de VALIDAÇÃO que barram dados ruins antes
 * de chegarem ao Service. Por isso o foco principal aqui é:
 *
 *   - Cada campo obrigatório é realmente obrigatório.
 *   - Os limites de tamanho (mínimo e máximo) funcionam, inclusive nas bordas.
 *   - Senha, telefone, e-mail e data de nascimento seguem seus formatos.
 *   - A conversão de JSON para o DTO (data dd/MM/yyyy) funciona.
 *   - A senha nunca aparece no toString (não vaza em logs).
 *
 *
 *      new PersonInsertDTOV2(...)  ----->  Validator (Jakarta)  ----->  violações
 *            |
 *            +----->  ObjectMapper  <----->  JSON
 *            |
 *            +----->  getters / setters / toString
 *
 *
 * POR QUE JUNIT PURO + VALIDATOR (SEM SPRING)
 * -------------------------------------------
 * As anotações (@NotBlank, @Size...) são do Jakarta Validation. Podemos
 * executá-las diretamente com um Validator criado na mão, sem subir o Spring.
 * Resultado: teste rápido e focado só no DTO.
 *
 * O teste de controller (@WebMvcTest) já prova que "{}" gera 400. AQUI
 * provamos, campo a campo, o MOTIVO de cada recusa.
 *
 *
 * COMO LER AS VERIFICAÇÕES DE VALIDAÇÃO
 * -------------------------------------
 * Em vez de comparar o texto da mensagem de erro (que pode mudar), conferimos
 * QUAL anotação foi violada. Assim o teste não quebra se alguém só reescrever
 * uma mensagem.
 *
 *   hasViolation(dto, "password", Pattern.class)
 *   -> "o campo password violou a regra @Pattern?"
 *
 *
 * PADRÃO DE ESCRITA
 * -----------------
 *   ARRANGE : monta o DTO (parte de um DTO válido e altera só o que importa).
 *   ACT     : altera o campo ou executa a validação / conversão.
 *   ASSERT  : confere o resultado.
 *
 * Nomes: metodo_quandoCondicao_resultadoEsperado
 * ============================================================================
 */
class PersonInsertDTOV2Test {

    /* Dados fixos e fáceis de reconhecer. Todos formam um DTO VÁLIDO. */
    private static final String FIRST_NAME = "Domingos";
    private static final String LAST_NAME = "Silva";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 5, 15);
    private static final String CPF = "12345678901";
    private static final String PHONE = "+55 61 99999-9999";
    private static final String EMAIL = "domingos@gmail.com";
    private static final String PASSWORD = "Diniz123@";
    private static final String ADDRESS = "Rua Teste, 10";
    private static final String GENDER = "Male";

    /*
     * Não sabemos quais perfis existem no enum e não precisamos saber.
     * Pegamos o primeiro: o teste continua válido se os perfis forem renomeados.
     */
    private static final PersonRole ROLE = PersonRole.values()[0];

    /* Validator do Jakarta: criado uma vez e reaproveitado (é caro de criar). */
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private ObjectMapper objectMapper;
    private PersonInsertDTOV2 dto;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        // JavaTimeModule ensina o Jackson a trabalhar com LocalDate.
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        dto = buildValidDto();
    }

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    @Test
    @DisplayName("Construtor padrão: cria o objeto com todos os campos nulos")
    void constructor_whenDefault_createsObjectWithAllFieldsNull() {

        // ACT
        PersonInsertDTOV2 empty = new PersonInsertDTOV2();

        // ASSERT
        assertAll(
                () -> assertNull(empty.getFirstName()),
                () -> assertNull(empty.getLastName()),
                () -> assertNull(empty.getBirthDate()),
                () -> assertNull(empty.getCpf()),
                () -> assertNull(empty.getPhone()),
                () -> assertNull(empty.getEmail()),
                () -> assertNull(empty.getPassword()),
                () -> assertNull(empty.getAddress()),
                () -> assertNull(empty.getGender()),
                () -> assertNull(empty.getRole())
        );
    }

    @Test
    @DisplayName("Construtor completo: atribui cada argumento ao campo correspondente")
    void constructor_whenParameterized_assignsEachArgumentToItsField() {

        // ACT
        PersonInsertDTOV2 created = new PersonInsertDTOV2(
                FIRST_NAME, LAST_NAME, BIRTH_DATE, CPF, PHONE,
                EMAIL, PASSWORD, ADDRESS, GENDER, ROLE);

        // ASSERT
        // assertAll executa TODAS as verificações e mostra todas as falhas
        // juntas, em vez de parar na primeira.
        assertAll(
                () -> assertEquals(FIRST_NAME, created.getFirstName()),
                () -> assertEquals(LAST_NAME, created.getLastName()),
                () -> assertEquals(BIRTH_DATE, created.getBirthDate()),
                () -> assertEquals(CPF, created.getCpf()),
                () -> assertEquals(PHONE, created.getPhone()),
                () -> assertEquals(EMAIL, created.getEmail()),
                () -> assertEquals(PASSWORD, created.getPassword()),
                () -> assertEquals(ADDRESS, created.getAddress()),
                () -> assertEquals(GENDER, created.getGender()),
                () -> assertEquals(ROLE, created.getRole())
        );
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    @Test
    @DisplayName("firstName: o valor definido no setter é devolvido pelo getter")
    void firstName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setFirstName(FIRST_NAME);

        // ASSERT
        assertEquals(FIRST_NAME, target.getFirstName());
    }

    @Test
    @DisplayName("lastName: o valor definido no setter é devolvido pelo getter")
    void lastName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setLastName(LAST_NAME);

        // ASSERT
        assertEquals(LAST_NAME, target.getLastName());
    }

    @Test
    @DisplayName("birthDate: o valor definido no setter é devolvido pelo getter")
    void birthDate_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setBirthDate(BIRTH_DATE);

        // ASSERT
        assertEquals(BIRTH_DATE, target.getBirthDate());
    }

    @Test
    @DisplayName("cpf: o valor definido no setter é devolvido pelo getter")
    void cpf_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setCpf(CPF);

        // ASSERT
        assertEquals(CPF, target.getCpf());
    }

    @Test
    @DisplayName("phone: o valor definido no setter é devolvido pelo getter")
    void phone_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setPhone(PHONE);

        // ASSERT
        assertEquals(PHONE, target.getPhone());
    }

    @Test
    @DisplayName("email: o valor definido no setter é devolvido pelo getter")
    void email_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setEmail(EMAIL);

        // ASSERT
        assertEquals(EMAIL, target.getEmail());
    }

    @Test
    @DisplayName("password: o valor definido no setter é devolvido pelo getter em texto puro")
    void password_whenSet_isReturnedByGetterAsPlainText() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setPassword(PASSWORD);

        // ASSERT
        // O DTO guarda a senha em texto puro porque o Service precisa dela
        // para criptografar. A criptografia NÃO é responsabilidade do DTO.
        assertEquals(PASSWORD, target.getPassword());
    }

    @Test
    @DisplayName("address: o valor definido no setter é devolvido pelo getter")
    void address_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setAddress(ADDRESS);

        // ASSERT
        assertEquals(ADDRESS, target.getAddress());
    }

    @Test
    @DisplayName("gender: o valor definido no setter é devolvido pelo getter")
    void gender_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setGender(GENDER);

        // ASSERT
        assertEquals(GENDER, target.getGender());
    }

    @Test
    @DisplayName("role: o valor definido no setter é devolvido pelo getter")
    void role_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonInsertDTOV2 target = new PersonInsertDTOV2();

        // ACT
        target.setRole(ROLE);

        // ASSERT
        assertEquals(ROLE, target.getRole());
    }

    // ========================================================================
    // VALIDAÇÃO - DTO COMPLETO
    // ========================================================================

    @Test
    @DisplayName("Validação: um DTO com todos os dados corretos não tem nenhuma violação")
    void validation_whenAllFieldsAreValid_hasNoViolations() {

        // ACT
        Set<ConstraintViolation<PersonInsertDTOV2>> violations = validator.validate(dto);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validação: um DTO vazio viola exatamente os campos obrigatórios (telefone é opcional)")
    void validation_whenDtoIsEmpty_violatesAllRequiredFieldsButNotPhone() {

        // ACT
        // Equivale ao "{}" enviado no teste de controller.
        Set<String> invalidFields = validator.validate(new PersonInsertDTOV2()).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        // ASSERT
        assertEquals(
                Set.of("firstName", "lastName", "birthDate", "cpf", "email",
                        "password", "address", "gender", "role"),
                invalidFields);
    }

    // ========================================================================
    // VALIDAÇÃO - firstName e lastName (obrigatório, 2 a 50 caracteres)
    // ========================================================================

    @Test
    @DisplayName("firstName: nulo ou em branco viola @NotBlank")
    void firstName_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setFirstName(null);
        boolean nullViolates = hasViolation(dto, "firstName", NotBlank.class);

        dto.setFirstName("   ");
        boolean blankViolates = hasViolation(dto, "firstName", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @Test
    @DisplayName("firstName: com 1 caractere viola @Size, com 2 é válido")
    void firstName_whenBelowMinimumSize_violatesSizeAndMinimumIsAccepted() {

        // ACT + ASSERT
        dto.setFirstName("A");
        assertTrue(hasViolation(dto, "firstName", Size.class));

        dto.setFirstName("Al");
        assertTrue(isValid(dto, "firstName"));
    }

    @Test
    @DisplayName("firstName: com 51 caracteres viola @Size, com 50 é válido")
    void firstName_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        dto.setFirstName("a".repeat(51));
        assertTrue(hasViolation(dto, "firstName", Size.class));

        dto.setFirstName("a".repeat(50));
        assertTrue(isValid(dto, "firstName"));
    }

    @Test
    @DisplayName("lastName: nulo ou em branco viola @NotBlank")
    void lastName_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setLastName(null);
        boolean nullViolates = hasViolation(dto, "lastName", NotBlank.class);

        dto.setLastName("   ");
        boolean blankViolates = hasViolation(dto, "lastName", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @Test
    @DisplayName("lastName: com 1 caractere viola @Size, com 2 é válido")
    void lastName_whenBelowMinimumSize_violatesSizeAndMinimumIsAccepted() {

        // ACT + ASSERT
        dto.setLastName("S");
        assertTrue(hasViolation(dto, "lastName", Size.class));

        dto.setLastName("Si");
        assertTrue(isValid(dto, "lastName"));
    }

    @Test
    @DisplayName("lastName: com 51 caracteres viola @Size, com 50 é válido")
    void lastName_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        dto.setLastName("a".repeat(51));
        assertTrue(hasViolation(dto, "lastName", Size.class));

        dto.setLastName("a".repeat(50));
        assertTrue(isValid(dto, "lastName"));
    }

    // ========================================================================
    // VALIDAÇÃO - birthDate (obrigatória e no passado)
    // ========================================================================

    @Test
    @DisplayName("birthDate: nula viola @NotNull")
    void birthDate_whenNull_violatesNotNull() {

        // ACT
        dto.setBirthDate(null);

        // ASSERT
        assertTrue(hasViolation(dto, "birthDate", NotNull.class));
    }

    @Test
    @DisplayName("birthDate: hoje ou data futura violam @Past")
    void birthDate_whenTodayOrFuture_violatesPast() {

        // ACT + ASSERT
        // "Hoje" não é passado: a data precisa ser anterior ao dia atual.
        dto.setBirthDate(LocalDate.now());
        assertTrue(hasViolation(dto, "birthDate", Past.class));

        dto.setBirthDate(LocalDate.now().plusDays(1));
        assertTrue(hasViolation(dto, "birthDate", Past.class));
    }

    @Test
    @DisplayName("birthDate: ontem é uma data válida")
    void birthDate_whenYesterday_isValid() {

        // ACT
        dto.setBirthDate(LocalDate.now().minusDays(1));

        // ASSERT
        assertTrue(isValid(dto, "birthDate"));
    }

    // ========================================================================
    // VALIDAÇÃO - cpf (obrigatório, 11 a 14 caracteres)
    // ========================================================================

    @Test
    @DisplayName("cpf: nulo ou em branco viola @NotBlank")
    void cpf_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setCpf(null);
        boolean nullViolates = hasViolation(dto, "cpf", NotBlank.class);

        dto.setCpf("   ");
        boolean blankViolates = hasViolation(dto, "cpf", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @Test
    @DisplayName("cpf: com 10 caracteres viola @Size, com 11 é válido")
    void cpf_whenBelowMinimumSize_violatesSizeAndMinimumIsAccepted() {

        // ACT + ASSERT
        dto.setCpf("1234567890");
        assertTrue(hasViolation(dto, "cpf", Size.class));

        dto.setCpf("12345678901");
        assertTrue(isValid(dto, "cpf"));
    }

    @Test
    @DisplayName("cpf: com 15 caracteres viola @Size, com 14 (formatado) é válido")
    void cpf_whenAboveMaximumSize_violatesSizeAndFormattedCpfIsAccepted() {

        // ACT + ASSERT
        dto.setCpf("123.456.789-012");
        assertTrue(hasViolation(dto, "cpf", Size.class));

        dto.setCpf("123.456.789-01");
        assertTrue(isValid(dto, "cpf"));
    }

    // ========================================================================
    // VALIDAÇÃO - phone (opcional, até 20 caracteres, só números e símbolos)
    // ========================================================================

    @Test
    @DisplayName("phone: nulo é válido, porque o telefone é opcional")
    void phone_whenNull_isValid() {

        // ACT
        dto.setPhone(null);

        // ASSERT
        assertTrue(isValid(dto, "phone"));
    }

    @ParameterizedTest(name = "telefone \"{0}\" é aceito")
    @ValueSource(strings = {"+55 61 99999-9999", "(61) 99999-9999", "61-99999-9999", "61999999999"})
    @DisplayName("phone: formatos permitidos são aceitos")
    void phone_whenFormatIsAllowed_isValid(String validPhone) {

        // ACT
        dto.setPhone(validPhone);

        // ASSERT
        assertTrue(isValid(dto, "phone"));
    }

    @ParameterizedTest(name = "telefone \"{0}\" é recusado")
    @ValueSource(strings = {"abc123", "61 99999-9999x", "telefone", "61.99999.9999", ""})
    @DisplayName("phone: letras, símbolos não permitidos ou texto vazio violam @Pattern")
    void phone_whenFormatIsNotAllowed_violatesPattern(String invalidPhone) {

        // ACT
        // O texto vazio ("") também é recusado: a regex exige ao menos 1 caractere.
        dto.setPhone(invalidPhone);

        // ASSERT
        assertTrue(hasViolation(dto, "phone", Pattern.class));
    }

    @Test
    @DisplayName("phone: com 21 caracteres viola @Size, com 20 é válido")
    void phone_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        dto.setPhone("1".repeat(21));
        assertTrue(hasViolation(dto, "phone", Size.class));

        dto.setPhone("1".repeat(20));
        assertTrue(isValid(dto, "phone"));
    }

    // ========================================================================
    // VALIDAÇÃO - email (obrigatório e com formato válido)
    // ========================================================================

    @Test
    @DisplayName("email: nulo ou em branco viola @NotBlank")
    void email_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setEmail(null);
        boolean nullViolates = hasViolation(dto, "email", NotBlank.class);

        dto.setEmail("   ");
        boolean blankViolates = hasViolation(dto, "email", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @ParameterizedTest(name = "e-mail \"{0}\" é recusado")
    @ValueSource(strings = {"sem-arroba", "dois@@arrobas.com"})
    @DisplayName("email: formato inválido viola @Email")
    void email_whenFormatIsInvalid_violatesEmail(String invalidEmail) {

        // ACT
        dto.setEmail(invalidEmail);

        // ASSERT
        assertTrue(hasViolation(dto, "email", Email.class));
    }

    @Test
    @DisplayName("email: um endereço bem formado é válido")
    void email_whenWellFormed_isValid() {

        // ACT
        dto.setEmail("maria.souza@empresa.com.br");

        // ASSERT
        assertTrue(isValid(dto, "email"));
    }

    // ========================================================================
    // VALIDAÇÃO - password (8 a 100 caracteres, com maiúscula, minúscula,
    //             número e caractere especial)
    // ========================================================================

    @Test
    @DisplayName("password: nula ou em branco viola @NotBlank")
    void password_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setPassword(null);
        boolean nullViolates = hasViolation(dto, "password", NotBlank.class);

        dto.setPassword("        ");
        boolean blankViolates = hasViolation(dto, "password", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @ParameterizedTest(name = "senha \"{0}\" é aceita")
    @ValueSource(strings = {"Diniz123@", "Java2026#", "MinhaSenha1+"})
    @DisplayName("password: senhas que cumprem todas as regras são aceitas")
    void password_whenFollowsAllRules_isValid(String validPassword) {

        // ACT
        dto.setPassword(validPassword);

        // ASSERT
        assertTrue(isValid(dto, "password"));
    }

    @ParameterizedTest(name = "senha \"{0}\" é recusada")
    @ValueSource(strings = {
            "12345678",     // só números
            "diniz123",     // sem maiúscula e sem especial
            "DINIZ123@",    // sem minúscula
            "Diniz1234",    // sem caractere especial
            "Diniz@abc"     // sem número
    })
    @DisplayName("password: faltando algum requisito de composição, viola @Pattern")
    void password_whenMissingARequiredCharacterType_violatesPattern(String weakPassword) {

        // ACT
        dto.setPassword(weakPassword);

        // ASSERT
        assertTrue(hasViolation(dto, "password", Pattern.class));
    }

    @Test
    @DisplayName("password: com 7 caracteres viola @Size, com 8 é válida")
    void password_whenBelowMinimumSize_violatesSizeAndMinimumIsAccepted() {

        // ACT + ASSERT
        dto.setPassword("Abc123@");
        assertTrue(hasViolation(dto, "password", Size.class));

        dto.setPassword("Abc1234@");
        assertTrue(isValid(dto, "password"));
    }

    @Test
    @DisplayName("password: com 101 caracteres viola @Size, com 100 é válida")
    void password_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        // "Aa1@" garante os 4 tipos de caractere; o resto completa o tamanho.
        dto.setPassword("Aa1@" + "x".repeat(97));
        assertTrue(hasViolation(dto, "password", Size.class));

        dto.setPassword("Aa1@" + "x".repeat(96));
        assertTrue(isValid(dto, "password"));
    }

    // ========================================================================
    // VALIDAÇÃO - address (obrigatório, 5 a 100 caracteres)
    // ========================================================================

    @Test
    @DisplayName("address: nulo ou em branco viola @NotBlank")
    void address_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setAddress(null);
        boolean nullViolates = hasViolation(dto, "address", NotBlank.class);

        dto.setAddress("   ");
        boolean blankViolates = hasViolation(dto, "address", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @Test
    @DisplayName("address: com 4 caracteres viola @Size, com 5 é válido")
    void address_whenBelowMinimumSize_violatesSizeAndMinimumIsAccepted() {

        // ACT + ASSERT
        dto.setAddress("Rua1");
        assertTrue(hasViolation(dto, "address", Size.class));

        dto.setAddress("Rua 1");
        assertTrue(isValid(dto, "address"));
    }

    @Test
    @DisplayName("address: com 101 caracteres viola @Size, com 100 é válido")
    void address_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        dto.setAddress("a".repeat(101));
        assertTrue(hasViolation(dto, "address", Size.class));

        dto.setAddress("a".repeat(100));
        assertTrue(isValid(dto, "address"));
    }

    // ========================================================================
    // VALIDAÇÃO - gender (obrigatório, 1 a 20 caracteres)
    // ========================================================================

    @Test
    @DisplayName("gender: nulo ou em branco viola @NotBlank")
    void gender_whenNullOrBlank_violatesNotBlank() {

        // ACT
        dto.setGender(null);
        boolean nullViolates = hasViolation(dto, "gender", NotBlank.class);

        dto.setGender("   ");
        boolean blankViolates = hasViolation(dto, "gender", NotBlank.class);

        // ASSERT
        assertAll(
                () -> assertTrue(nullViolates),
                () -> assertTrue(blankViolates)
        );
    }

    @Test
    @DisplayName("gender: com 1 caractere é válido (limite mínimo)")
    void gender_whenOneCharacter_isValid() {

        // ACT
        dto.setGender("M");

        // ASSERT
        assertTrue(isValid(dto, "gender"));
    }

    @Test
    @DisplayName("gender: com 21 caracteres viola @Size, com 20 é válido")
    void gender_whenAboveMaximumSize_violatesSizeAndMaximumIsAccepted() {

        // ACT + ASSERT
        dto.setGender("a".repeat(21));
        assertTrue(hasViolation(dto, "gender", Size.class));

        dto.setGender("a".repeat(20));
        assertTrue(isValid(dto, "gender"));
    }

    // ========================================================================
    // VALIDAÇÃO - role (obrigatório)
    // ========================================================================

    @Test
    @DisplayName("role: nulo viola @NotNull")
    void role_whenNull_violatesNotNull() {

        // ACT
        dto.setRole(null);

        // ASSERT
        assertTrue(hasViolation(dto, "role", NotNull.class));
    }

    // ========================================================================
    // TOSTRING (a senha não pode vazar em logs)
    // ========================================================================

    @Test
    @DisplayName("toString: nunca contém a senha")
    void toString_neverContainsPassword() {

        // ACT
        String text = dto.toString();

        // ASSERT
        // Protege contra vazamento em logs: se alguém incluir password no
        // toString, este teste quebra.
        assertFalse(text.contains(PASSWORD));
    }

    @Test
    @DisplayName("toString: contém o nome da classe e os dados de identificação")
    void toString_containsClassNameAndIdentificationFields() {

        // ACT
        String text = dto.toString();

        // ASSERT
        assertAll(
                () -> assertTrue(text.startsWith("PersonInsertDTOV2{")),
                () -> assertTrue(text.contains(FIRST_NAME)),
                () -> assertTrue(text.contains(LAST_NAME))
        );
    }

    // ========================================================================
    // JSON (conversão de/para o corpo da requisição)
    // ========================================================================

    @Test
    @DisplayName("JSON: respeita a ordem definida em @JsonPropertyOrder")
    void json_respectsDeclaredPropertyOrder() throws Exception {

        // ACT
        List<String> fieldNames = new ArrayList<>();
        toJsonNode(dto).fieldNames().forEachRemaining(fieldNames::add);

        // ASSERT
        assertEquals(
                List.of("firstName", "lastName", "birthDate", "email", "password",
                        "cpf", "phone", "gender", "address", "role"),
                fieldNames);
    }

    @Test
    @DisplayName("JSON: escreve a data de nascimento como dd/MM/yyyy")
    void json_writesBirthDateAsDayMonthYear() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        assertEquals("15/05/1990", json.get("birthDate").asText());
    }

    @Test
    @DisplayName("JSON: converte o JSON de volta para um DTO com os mesmos dados")
    void json_whenReadBack_restoresAllFields() throws Exception {

        // ARRANGE
        String json = objectMapper.writeValueAsString(dto);

        // ACT
        PersonInsertDTOV2 restored = objectMapper.readValue(json, PersonInsertDTOV2.class);

        // ASSERT
        assertAll(
                () -> assertEquals(FIRST_NAME, restored.getFirstName()),
                () -> assertEquals(LAST_NAME, restored.getLastName()),
                () -> assertEquals(BIRTH_DATE, restored.getBirthDate()),
                () -> assertEquals(CPF, restored.getCpf()),
                () -> assertEquals(PHONE, restored.getPhone()),
                () -> assertEquals(EMAIL, restored.getEmail()),
                () -> assertEquals(PASSWORD, restored.getPassword()),
                () -> assertEquals(ADDRESS, restored.getAddress()),
                () -> assertEquals(GENDER, restored.getGender()),
                () -> assertEquals(ROLE, restored.getRole())
        );
    }

    @Test
    @DisplayName("JSON: data fora do formato dd/MM/yyyy faz a conversão falhar")
    void json_whenBirthDateHasWrongFormat_failsToRead() throws Exception {

        // ARRANGE
        // Troca a data para o formato ISO (yyyy-MM-dd), que o contrato não aceita.
        String json = objectMapper.writeValueAsString(dto)
                .replace("15/05/1990", "1990-05-15");

        // ACT + ASSERT
        // No Spring, essa falha vira HTTP 400 (corpo ilegível).
        assertThrows(JsonProcessingException.class,
                () -> objectMapper.readValue(json, PersonInsertDTOV2.class));
    }

    @Test
    @DisplayName("JSON: um objeto vazio gera um DTO com campos nulos (a recusa vem da validação)")
    void json_whenEmptyObject_createsDtoWithNullFields() throws Exception {

        // ACT
        PersonInsertDTOV2 restored = objectMapper.readValue("{}", PersonInsertDTOV2.class);

        // ASSERT
        assertAll(
                () -> assertNull(restored.getFirstName()),
                () -> assertNull(restored.getPassword()),
                () -> assertNull(restored.getBirthDate()),
                () -> assertNull(restored.getRole())
        );
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /* Monta um DTO com todos os campos válidos. Cada teste altera só o que precisa. */
    private PersonInsertDTOV2 buildValidDto() {
        return new PersonInsertDTOV2(
                FIRST_NAME, LAST_NAME, BIRTH_DATE, CPF, PHONE,
                EMAIL, PASSWORD, ADDRESS, GENDER, ROLE);
    }

    /* Valida SOMENTE o campo informado (os demais campos são ignorados). */
    private Set<ConstraintViolation<PersonInsertDTOV2>> violationsOf(PersonInsertDTOV2 target, String field) {
        return validator.validateProperty(target, field);
    }

    /* O campo violou a regra (anotação) informada? */
    private boolean hasViolation(PersonInsertDTOV2 target,
                                 String field,
                                 Class<? extends Annotation> constraint) {
        return violationsOf(target, field).stream()
                .anyMatch(violation -> violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(constraint));
    }

    /* O campo está totalmente válido (nenhuma violação)? */
    private boolean isValid(PersonInsertDTOV2 target, String field) {
        return violationsOf(target, field).isEmpty();
    }

    /* Converte o DTO em uma árvore JSON navegável. */
    private JsonNode toJsonNode(PersonInsertDTOV2 target) throws Exception {
        return objectMapper.readTree(objectMapper.writeValueAsString(target));
    }
}

/*
 * ============================================================================
 * FORA DO ESCOPO DESTE ARQUIVO (E COMO COBRIR)
 * ============================================================================
 *
 * 1) O @Valid NO CONTROLLER
 *    Aqui provamos que as regras existem no DTO. Que o Controller as aplica
 *    (e responde 400) está no PersonControllerV2Test (@WebMvcTest + MockMvc).
 *
 * 2) UNICIDADE DE CPF E E-MAIL
 *    "CPF já cadastrado" depende do banco: é do Repository (@DataJpaTest) e
 *    do Service, não deste DTO.
 *
 * 3) CRIPTOGRAFIA DA SENHA
 *    O DTO guarda a senha em texto puro. Quem criptografa é o Service e isso
 *    já é testado no PersonServiceV2Test.
 *
 * Divisão de responsabilidades nos testes:
 *
 *   DTO de entrada -> regras de validação  -> JUnit + Validator (este arquivo)
 *   Controller     -> contrato HTTP        -> @WebMvcTest + MockMvc
 *   Service        -> regras de negócio    -> JUnit + Mockito
 * ============================================================================
 */