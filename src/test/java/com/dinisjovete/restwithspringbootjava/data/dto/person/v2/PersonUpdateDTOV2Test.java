package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * ============================================================================
 * TESTE DE UNIDADE - PersonUpdateDTOV2 (v2)
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * Este DTO é o corpo do PUT /person/v2/{id}. Ele carrega somente os dados que
 * podem ser alterados numa atualização comum: nome, sobrenome, telefone,
 * endereço e gênero. Por isso o teste prova duas coisas:
 *
 *   1) As regras de VALIDAÇÃO de cada campo funcionam (obrigatoriedade,
 *      tamanhos e formato do telefone).
 *   2) O CONTRATO é restrito: campos de identidade e segurança (id, cpf,
 *      birthDate, email, password, role) NÃO existem aqui. Isso impede que
 *      alguém altere esses dados por um PUT comum.
 *
 *
 *      new PersonUpdateDTOV2(...)  ----->  Validator (Jakarta)  ----->  violações
 *            |
 *            +----->  ObjectMapper  <----->  JSON
 *            |
 *            +----->  getters / setters
 *
 *
 * POR QUE JUNIT PURO + VALIDATOR (SEM SPRING)
 * -------------------------------------------
 * As anotações (@NotBlank, @Size, @Pattern) são do Jakarta Validation e rodam
 * direto em um Validator criado na mão. Sem contexto do Spring o teste é
 * rápido e focado só no DTO. Que o Controller aplica o @Valid e responde 400
 * é provado no PersonControllerV2Test.
 *
 *
 * COMO LER AS VERIFICAÇÕES DE VALIDAÇÃO
 * -------------------------------------
 * Conferimos QUAL anotação foi violada, e não o texto da mensagem (que pode
 * mudar sem alterar a regra):
 *
 *   hasViolation(dto, "firstName", Size.class)
 *   -> "o campo firstName violou a regra @Size?"
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
class PersonUpdateDTOV2Test {

    /* Dados fixos e fáceis de reconhecer. Todos formam um DTO VÁLIDO. */
    private static final String FIRST_NAME = "Diniz";
    private static final String LAST_NAME = "Jovete";
    private static final String PHONE = "+55 61 99999-9999";
    private static final String ADDRESS = "Rua Principal, 123";
    private static final String GENDER = "M";

    /* Validator do Jakarta: criado uma vez e reaproveitado (é caro de criar). */
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private ObjectMapper objectMapper;
    private PersonUpdateDTOV2 dto;

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
        objectMapper = new ObjectMapper();
        dto = buildValidDto();
    }

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    @Test
    @DisplayName("Construtor padrão: cria o objeto com todos os campos nulos")
    void constructor_whenDefault_createsObjectWithAllFieldsNull() {

        // ACT
        PersonUpdateDTOV2 empty = new PersonUpdateDTOV2();

        // ASSERT
        assertAll(
                () -> assertNull(empty.getFirstName()),
                () -> assertNull(empty.getLastName()),
                () -> assertNull(empty.getPhone()),
                () -> assertNull(empty.getAddress()),
                () -> assertNull(empty.getGender())
        );
    }

    @Test
    @DisplayName("Construtor completo: atribui cada argumento ao campo correspondente")
    void constructor_whenParameterized_assignsEachArgumentToItsField() {

        // ACT
        PersonUpdateDTOV2 created = new PersonUpdateDTOV2(
                FIRST_NAME, LAST_NAME, PHONE, ADDRESS, GENDER);

        // ASSERT
        // assertAll executa TODAS as verificações e mostra todas as falhas
        // juntas, em vez de parar na primeira.
        assertAll(
                () -> assertEquals(FIRST_NAME, created.getFirstName()),
                () -> assertEquals(LAST_NAME, created.getLastName()),
                () -> assertEquals(PHONE, created.getPhone()),
                () -> assertEquals(ADDRESS, created.getAddress()),
                () -> assertEquals(GENDER, created.getGender())
        );
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    @Test
    @DisplayName("firstName: o valor definido no setter é devolvido pelo getter")
    void firstName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonUpdateDTOV2 target = new PersonUpdateDTOV2();

        // ACT
        target.setFirstName(FIRST_NAME);

        // ASSERT
        assertEquals(FIRST_NAME, target.getFirstName());
    }

    @Test
    @DisplayName("lastName: o valor definido no setter é devolvido pelo getter")
    void lastName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonUpdateDTOV2 target = new PersonUpdateDTOV2();

        // ACT
        target.setLastName(LAST_NAME);

        // ASSERT
        assertEquals(LAST_NAME, target.getLastName());
    }

    @Test
    @DisplayName("phone: o valor definido no setter é devolvido pelo getter")
    void phone_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonUpdateDTOV2 target = new PersonUpdateDTOV2();

        // ACT
        target.setPhone(PHONE);

        // ASSERT
        assertEquals(PHONE, target.getPhone());
    }

    @Test
    @DisplayName("address: o valor definido no setter é devolvido pelo getter")
    void address_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonUpdateDTOV2 target = new PersonUpdateDTOV2();

        // ACT
        target.setAddress(ADDRESS);

        // ASSERT
        assertEquals(ADDRESS, target.getAddress());
    }

    @Test
    @DisplayName("gender: o valor definido no setter é devolvido pelo getter")
    void gender_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonUpdateDTOV2 target = new PersonUpdateDTOV2();

        // ACT
        target.setGender(GENDER);

        // ASSERT
        assertEquals(GENDER, target.getGender());
    }

    // ========================================================================
    // VALIDAÇÃO - DTO COMPLETO
    // ========================================================================

    @Test
    @DisplayName("Validação: um DTO com todos os dados corretos não tem nenhuma violação")
    void validation_whenAllFieldsAreValid_hasNoViolations() {

        // ACT
        Set<ConstraintViolation<PersonUpdateDTOV2>> violations = validator.validate(dto);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("Validação: um DTO vazio viola exatamente os campos obrigatórios (telefone é opcional)")
    void validation_whenDtoIsEmpty_violatesAllRequiredFieldsButNotPhone() {

        // ACT
        // Equivale ao "{}" enviado no PUT.
        Set<String> invalidFields = validator.validate(new PersonUpdateDTOV2()).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        // ASSERT
        assertEquals(Set.of("firstName", "lastName", "address", "gender"), invalidFields);
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
        dto.setLastName("J");
        assertTrue(hasViolation(dto, "lastName", Size.class));

        dto.setLastName("Jo");
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
    // CONTRATO RESTRITO (somente campos editáveis)
    // ========================================================================

    @Test
    @DisplayName("Contrato: o DTO declara somente os 5 campos editáveis")
    void contract_declaresOnlyEditableFields() {

        // ACT
        // Lê os campos declarados na classe (ignorando constantes estáticas
        // como serialVersionUID).
        Set<String> declaredFields = Arrays.stream(PersonUpdateDTOV2.class.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .collect(Collectors.toSet());

        // ASSERT
        // Protege contra "mass assignment": se alguém adicionar id, cpf,
        // password, role etc. a este DTO, o PUT comum passaria a permitir a
        // alteração desses dados e este teste quebra.
        assertEquals(Set.of("firstName", "lastName", "phone", "address", "gender"), declaredFields);
    }

    @Test
    @DisplayName("Contrato: campos de identidade e segurança enviados no JSON são ignorados")
    void contract_whenJsonHasSensitiveFields_ignoresThemAndKeepsEditableOnes() throws Exception {

        // ARRANGE
        // O Spring Boot desliga FAIL_ON_UNKNOWN_PROPERTIES por padrão. Fazemos
        // o mesmo aqui para simular o comportamento real da API.
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String json = """
                {
                    "firstName": "Diniz",
                    "lastName": "Jovete",
                    "phone": "+55 61 99999-9999",
                    "address": "Rua Principal, 123",
                    "gender": "M",
                    "id": 99,
                    "cpf": "12345678901",
                    "password": "Hacker123@",
                    "role": "ADMIN"
                }
                """;

        // ACT
        PersonUpdateDTOV2 result = objectMapper.readValue(json, PersonUpdateDTOV2.class);

        // ASSERT
        // Os campos editáveis chegam; os demais simplesmente não têm onde
        // ser guardados, então nunca alcançam o Service.
        assertAll(
                () -> assertEquals(FIRST_NAME, result.getFirstName()),
                () -> assertEquals(LAST_NAME, result.getLastName()),
                () -> assertEquals(PHONE, result.getPhone()),
                () -> assertEquals(ADDRESS, result.getAddress()),
                () -> assertEquals(GENDER, result.getGender())
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
        assertEquals(List.of("firstName", "lastName", "phone", "address", "gender"), fieldNames);
    }

    @Test
    @DisplayName("JSON: converte o JSON de volta para um DTO com os mesmos dados")
    void json_whenReadBack_restoresAllFields() throws Exception {

        // ARRANGE
        String json = objectMapper.writeValueAsString(dto);

        // ACT
        PersonUpdateDTOV2 restored = objectMapper.readValue(json, PersonUpdateDTOV2.class);

        // ASSERT
        assertAll(
                () -> assertEquals(FIRST_NAME, restored.getFirstName()),
                () -> assertEquals(LAST_NAME, restored.getLastName()),
                () -> assertEquals(PHONE, restored.getPhone()),
                () -> assertEquals(ADDRESS, restored.getAddress()),
                () -> assertEquals(GENDER, restored.getGender())
        );
    }

    @Test
    @DisplayName("JSON: um objeto vazio gera um DTO com campos nulos (a recusa vem da validação)")
    void json_whenEmptyObject_createsDtoWithNullFields() throws Exception {

        // ACT
        PersonUpdateDTOV2 restored = objectMapper.readValue("{}", PersonUpdateDTOV2.class);

        // ASSERT
        assertAll(
                () -> assertNull(restored.getFirstName()),
                () -> assertNull(restored.getLastName()),
                () -> assertNull(restored.getPhone()),
                () -> assertNull(restored.getAddress()),
                () -> assertNull(restored.getGender())
        );
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /* Monta um DTO com todos os campos válidos. Cada teste altera só o que precisa. */
    private PersonUpdateDTOV2 buildValidDto() {
        return new PersonUpdateDTOV2(FIRST_NAME, LAST_NAME, PHONE, ADDRESS, GENDER);
    }

    /* Valida SOMENTE o campo informado (os demais campos são ignorados). */
    private Set<ConstraintViolation<PersonUpdateDTOV2>> violationsOf(PersonUpdateDTOV2 target, String field) {
        return validator.validateProperty(target, field);
    }

    /* O campo violou a regra (anotação) informada? */
    private boolean hasViolation(PersonUpdateDTOV2 target,
                                 String field,
                                 Class<? extends Annotation> constraint) {
        return violationsOf(target, field).stream()
                .anyMatch(violation -> violation.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(constraint));
    }

    /* O campo está totalmente válido (nenhuma violação)? */
    private boolean isValid(PersonUpdateDTOV2 target, String field) {
        return violationsOf(target, field).isEmpty();
    }

    /* Converte o DTO em uma árvore JSON navegável. */
    private JsonNode toJsonNode(PersonUpdateDTOV2 target) throws Exception {
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
 * 2) O QUE O UPDATE FAZ COM OS DADOS
 *    Atualizar a entidade, ID inexistente e demais regras de negócio são do
 *    Service e já estão no PersonServiceV2Test.
 *
 * Divisão de responsabilidades nos testes:
 *
 *   DTO de atualização -> validação e contrato -> JUnit + Validator (este arquivo)
 *   Controller         -> contrato HTTP        -> @WebMvcTest + MockMvc
 *   Service            -> regras de negócio    -> JUnit + Mockito
 * ============================================================================
 */