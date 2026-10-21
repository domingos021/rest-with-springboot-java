package com.dinisjovete.restwithspringbootjava.data.dto.person.v2;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * ============================================================================
 * TESTE DE UNIDADE - PersonDTOV2 (v2)
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * O DTO é o "contrato de dados" que sai pela API. Ele não tem regra de negócio,
 * mas TEM comportamento que precisa ser protegido:
 *
 *   - Construtores e getters/setters guardam e devolvem os valores certos.
 *   - equals/hashCode comparam SOMENTE pelo ID.
 *   - toString NÃO vaza dados sensíveis (CPF, e-mail).
 *   - O JSON gerado respeita o contrato: nomes, ordem, formato de data,
 *     máscara de CPF e e-mail, campos nulos omitidos e perfil escondido.
 *   - Os serializadores de máscara tratam os casos de borda (nulo, curto,
 *     sem arroba).
 *
 *
 *      new PersonDTOV2(...)  ----->  ObjectMapper  ----->  JSON
 *            |                           |
 *            |                           +--> CpfMaskSerializer
 *            |                           +--> EmailMaskSerializer
 *            v
 *      getters / setters / equals / hashCode / toString
 *
 *
 * POR QUE JUNIT PURO (SEM SPRING)
 * -------------------------------
 * O DTO é uma classe Java simples. Não precisa de contexto do Spring, banco ou
 * servidor. Para testar o JSON usamos um ObjectMapper criado na mão, que é
 * rápido e deixa o teste independente da configuração da aplicação.
 *
 *
 * PADRÃO DE ESCRITA
 * -----------------
 *   ARRANGE : prepara o objeto com os dados do teste.
 *   ACT     : executa o método (ou gera o JSON) que está sendo testado.
 *   ASSERT  : confere o resultado.
 *
 * Nomes: metodo_quandoCondicao_resultadoEsperado
 * ============================================================================
 */
class PersonDTOV2Test {

    /* Dados fixos e fáceis de reconhecer nos testes. */
    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Domingos";
    private static final String LAST_NAME = "Silva";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 5, 15);
    private static final String CPF = "12345678901";
    private static final String PHONE = "+244923000000";
    private static final String EMAIL = "domingos@gmail.com";
    private static final String ADDRESS = "Rua Teste, 10";
    private static final String GENDER = "Male";

    /*
     * Não sabemos (e não precisamos saber) quais perfis existem no enum.
     * Pegamos o primeiro valor disponível: o teste continua válido mesmo que
     * os perfis sejam renomeados.
     */
    private static final PersonRole ROLE = PersonRole.values()[0];

    /*
     * Mixin: diz ao Jackson para ignorar o getLinks() do RepresentationModel
     * SOMENTE neste teste. O DTO e a produção não são alterados.
     *
     * Por que isso é necessário: o PersonDTOV2 herda de RepresentationModel
     * (suporte a HATEOAS), e essa classe faz o Jackson gerar a propriedade
     * "links" no JSON. Como aqui o ObjectMapper é simples (sem o formato HAL
     * do Spring), esse campo extra estragaria os testes que conferem o JSON
     * exato (ordem dos campos, quantidade de campos, objeto vazio "{}").
     */
    abstract static class IgnoreLinksMixin {
        @JsonIgnore
        public abstract Links getLinks();
    }

    /*
     * ObjectMapper criado na mão. O JavaTimeModule ensina o Jackson a
     * trabalhar com LocalDate (no Spring Boot isso já vem registrado).
     */
    private ObjectMapper objectMapper;

    private PersonDTOV2 dto;

    @BeforeEach
    void setUp() {
        /*
         * Cria um ObjectMapper manualmente (fora do Spring).
         * O JavaTimeModule adiciona suporte para classes de data do Java 8+,
         * como LocalDate, permitindo a conversão correta para JSON.
         */
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // Liga o mixin ao ObjectMapper: a partir daqui o "links" não sai no JSON.
        objectMapper.addMixIn(RepresentationModel.class, IgnoreLinksMixin.class);

        dto = buildFullDto();
    }

    // ========================================================================
    // CONSTRUTORES
    // ========================================================================

    @Test
    @DisplayName("Construtor padrão: cria o objeto com todos os campos nulos")
    void constructor_whenDefault_createsObjectWithAllFieldsNull() {

        // ACT
        PersonDTOV2 empty = new PersonDTOV2();

        // ASSERT
        assertAll(
                () -> assertNull(empty.getId()),
                () -> assertNull(empty.getFirstName()),
                () -> assertNull(empty.getLastName()),
                () -> assertNull(empty.getBirthDate()),
                () -> assertNull(empty.getCpf()),
                () -> assertNull(empty.getPhone()),
                () -> assertNull(empty.getEmail()),
                () -> assertNull(empty.getAddress()),
                () -> assertNull(empty.getGender()),
                () -> assertNull(empty.getRole())
        );
    }

    @Test
    @DisplayName("Construtor completo: atribui cada argumento ao campo correspondente")
    void constructor_whenParameterized_assignsEachArgumentToItsField() {

        // ACT
        PersonDTOV2 created = new PersonDTOV2(
                ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, CPF,
                PHONE, EMAIL, ADDRESS, GENDER, ROLE
        );

        // ASSERT
        // assertAll executa TODAS as verificações e mostra todas as falhas
        // juntas, em vez de parar na primeira.
        assertAll(
                () -> assertEquals(ID, created.getId()),
                () -> assertEquals(FIRST_NAME, created.getFirstName()),
                () -> assertEquals(LAST_NAME, created.getLastName()),
                () -> assertEquals(BIRTH_DATE, created.getBirthDate()),
                () -> assertEquals(CPF, created.getCpf()),
                () -> assertEquals(PHONE, created.getPhone()),
                () -> assertEquals(EMAIL, created.getEmail()),
                () -> assertEquals(ADDRESS, created.getAddress()),
                () -> assertEquals(GENDER, created.getGender()),
                () -> assertEquals(ROLE, created.getRole())
        );
    }

    // ========================================================================
    // GETTERS E SETTERS
    // ========================================================================

    @Test
    @DisplayName("id: o valor definido no setter é devolvido pelo getter")
    void id_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setId(ID);

        // ASSERT
        assertEquals(ID, target.getId());
    }

    @Test
    @DisplayName("firstName: o valor definido no setter é devolvido pelo getter")
    void firstName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setFirstName(FIRST_NAME);

        // ASSERT
        assertEquals(FIRST_NAME, target.getFirstName());
    }

    @Test
    @DisplayName("lastName: o valor definido no setter é devolvido pelo getter")
    void lastName_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setLastName(LAST_NAME);

        // ASSERT
        assertEquals(LAST_NAME, target.getLastName());
    }

    @Test
    @DisplayName("birthDate: o valor definido no setter é devolvido pelo getter")
    void birthDate_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setBirthDate(BIRTH_DATE);

        // ASSERT
        assertEquals(BIRTH_DATE, target.getBirthDate());
    }

    @Test
    @DisplayName("cpf: o valor definido no setter é devolvido pelo getter SEM máscara")
    void cpf_whenSet_isReturnedByGetterWithoutMask() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setCpf(CPF);

        // ASSERT
        // A máscara só é aplicada na SERIALIZAÇÃO (JSON). Dentro do Java o
        // valor continua completo.
        assertEquals(CPF, target.getCpf());
    }

    @Test
    @DisplayName("phone: o valor definido no setter é devolvido pelo getter")
    void phone_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setPhone(PHONE);

        // ASSERT
        assertEquals(PHONE, target.getPhone());
    }

    @Test
    @DisplayName("email: o valor definido no setter é devolvido pelo getter SEM máscara")
    void email_whenSet_isReturnedByGetterWithoutMask() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setEmail(EMAIL);

        // ASSERT
        assertEquals(EMAIL, target.getEmail());
    }

    @Test
    @DisplayName("address: o valor definido no setter é devolvido pelo getter")
    void address_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setAddress(ADDRESS);

        // ASSERT
        assertEquals(ADDRESS, target.getAddress());
    }

    @Test
    @DisplayName("gender: o valor definido no setter é devolvido pelo getter")
    void gender_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setGender(GENDER);

        // ASSERT
        assertEquals(GENDER, target.getGender());
    }

    @Test
    @DisplayName("role: o valor definido no setter é devolvido pelo getter")
    void role_whenSet_isReturnedByGetter() {

        // ARRANGE
        PersonDTOV2 target = new PersonDTOV2();

        // ACT
        target.setRole(ROLE);

        // ASSERT
        assertEquals(ROLE, target.getRole());
    }

    @Test
    @DisplayName("setters: aceitam null e o getter devolve null")
    void setters_whenGivenNull_getterReturnsNull() {

        // ACT
        dto.setFirstName(null);
        dto.setBirthDate(null);
        dto.setRole(null);

        // ASSERT
        assertAll(
                () -> assertNull(dto.getFirstName()),
                () -> assertNull(dto.getBirthDate()),
                () -> assertNull(dto.getRole())
        );
    }

    // ========================================================================
    // EQUALS E HASHCODE (baseados somente no ID)
    // ========================================================================

    @Test
    @DisplayName("equals: o mesmo objeto é igual a si mesmo")
    void equals_whenSameInstance_returnsTrue() {

        // ASSERT
        assertEquals(dto, dto);
    }

    @Test
    @DisplayName("equals: dois objetos com o mesmo ID são iguais, mesmo com outros dados diferentes")
    void equals_whenSameId_returnsTrueEvenIfOtherFieldsDiffer() {

        // ARRANGE
        PersonDTOV2 other = new PersonDTOV2();
        other.setId(ID);
        other.setFirstName("Outro Nome");

        // ASSERT
        assertEquals(dto, other);
    }

    @Test
    @DisplayName("equals: dois objetos com IDs diferentes não são iguais")
    void equals_whenDifferentId_returnsFalse() {

        // ARRANGE
        PersonDTOV2 other = buildFullDto();
        other.setId(2L);

        // ASSERT
        assertNotEquals(dto, other);
    }

    @Test
    @DisplayName("equals: comparar com null retorna false")
    void equals_whenComparedWithNull_returnsFalse() {

        // ASSERT
        // Chamamos equals de propósito, para testar o método diretamente.
        assertFalse(dto.equals(null));
    }

    @Test
    @DisplayName("equals: comparar com outro tipo de objeto retorna false")
    void equals_whenComparedWithDifferentType_returnsFalse() {

        // ASSERT
        assertFalse(dto.equals("não sou um PersonDTOV2"));
    }

    @Test
    @DisplayName("equals: dois objetos sem ID (ambos null) são considerados iguais")
    void equals_whenBothIdsAreNull_returnsTrue() {

        // ASSERT
        // Comportamento de Objects.equals(null, null). Fica documentado aqui
        // para que uma mudança futura nessa regra seja percebida pelo teste.
        assertEquals(new PersonDTOV2(), new PersonDTOV2());
    }

    @Test
    @DisplayName("hashCode: objetos iguais produzem o mesmo hashCode")
    void hashCode_whenObjectsAreEqual_isTheSame() {

        // ARRANGE
        PersonDTOV2 other = new PersonDTOV2();
        other.setId(ID);

        // ASSERT
        // Contrato do Java: se equals é true, hashCode precisa ser igual.
        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    @DisplayName("hashCode: chamadas repetidas no mesmo objeto devolvem o mesmo valor")
    void hashCode_whenCalledRepeatedly_isConsistent() {

        // ASSERT
        assertEquals(dto.hashCode(), dto.hashCode());
    }

    // ========================================================================
    // TOSTRING (não pode vazar dados sensíveis)
    // ========================================================================

    @Test
    @DisplayName("toString: contém os campos públicos (id, nome, sobrenome, telefone)")
    void toString_containsPublicFields() {

        // ACT
        String text = dto.toString();

        // ASSERT
        assertAll(
                () -> assertTrue(text.contains("id=" + ID)),
                () -> assertTrue(text.contains(FIRST_NAME)),
                () -> assertTrue(text.contains(LAST_NAME)),
                () -> assertTrue(text.contains(PHONE))
        );
    }

    @Test
    @DisplayName("toString: nunca contém CPF nem e-mail")
    void toString_neverContainsCpfOrEmail() {

        // ACT
        String text = dto.toString();

        // ASSERT
        // Protege contra vazamento em logs: se alguém incluir cpf ou email
        // no toString, este teste quebra.
        assertAll(
                () -> assertFalse(text.contains(CPF)),
                () -> assertFalse(text.contains(EMAIL))
        );
    }

    // ========================================================================
    // HATEOAS (RepresentationModel)
    // ========================================================================

    @Test
    @DisplayName("links: o DTO aceita links HATEOAS e começa sem nenhum")
    void links_whenAdded_dtoReportsThem() {

        // ASSERT (estado inicial)
        assertFalse(dto.hasLinks());

        // ACT
        dto.add(Link.of("/person/v2/1"));

        // ASSERT
        assertTrue(dto.hasLinks());
    }

    @Test
    @DisplayName("equals: os links HATEOAS não influenciam a comparação")
    void equals_whenOnlyLinksDiffer_returnsTrue() {

        // ARRANGE
        PersonDTOV2 withLink = buildFullDto();
        withLink.add(Link.of("/person/v2/1"));

        // ASSERT
        assertEquals(dto, withLink);
    }

    // ========================================================================
    // SERIALIZAÇÃO JSON (contrato da API)
    // ========================================================================

    @Test
    @DisplayName("JSON: usa os nomes definidos em @JsonProperty, e não os nomes dos campos Java")
    void json_usesContractNamesInsteadOfJavaFieldNames() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        assertAll(
                () -> assertEquals(ID, json.get("ID_usuário").asLong()),
                () -> assertEquals(FIRST_NAME, json.get("Nome").asText()),
                () -> assertEquals(LAST_NAME, json.get("Sobrenome").asText()),
                () -> assertEquals(PHONE, json.get("Telefone").asText()),
                () -> assertEquals(ADDRESS, json.get("Endereço").asText()),
                () -> assertEquals(GENDER, json.get("Género").asText()),

                // nomes internos do Java NÃO podem aparecer no contrato
                () -> assertFalse(json.has("id")),
                () -> assertFalse(json.has("firstName")),
                () -> assertFalse(json.has("lastName")),
                () -> assertFalse(json.has("phone")),
                () -> assertFalse(json.has("address"))
        );
    }

    @Test
    @DisplayName("JSON: respeita a ordem definida em @JsonPropertyOrder")
    void json_respectsDeclaredPropertyOrder() throws Exception {

        // ACT
        List<String> fieldNames = new ArrayList<>();
        toJsonNode(dto).fieldNames().forEachRemaining(fieldNames::add);
        System.out.println(fieldNames); // <-- imprime no console

        // ASSERT
        // "Perfil" está na lista de ordem, mas o role é @JsonIgnore, então não
        // aparece. "Género" não está na lista, então vem depois dos demais.
        assertEquals(
                List.of("ID_usuário", "Nome", "Sobrenome", "Data_nascimento",
                        "CPF", "Telefone", "Email", "Endereço", "Género"),
                fieldNames);
    }

    @Test
    @DisplayName("JSON: formata a data de nascimento como dd/MM/yyyy")
    void json_formatsBirthDateAsDayMonthYear() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        assertEquals("15/05/1990", json.get("Data_nascimento").asText());
    }

    @Test
    @DisplayName("JSON: o CPF sai mascarado")
    void json_masksCpf() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        assertEquals("123.***.***-01", json.get("CPF").asText());
    }

    @Test
    @DisplayName("JSON: o e-mail sai mascarado")
    void json_masksEmail() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        assertEquals("do***@gmail.com", json.get("Email").asText());
    }

    @Test
    @DisplayName("JSON: o JSON completo nunca contém CPF nem e-mail em texto puro")
    void json_neverContainsRawCpfOrEmail() throws Exception {

        // ACT
        String json = objectMapper.writeValueAsString(dto);

        // ASSERT
        assertAll(
                () -> assertFalse(json.contains(CPF)),
                () -> assertFalse(json.contains(EMAIL))
        );
    }

    @Test
    @DisplayName("JSON: o perfil (role) nunca é exposto")
    void json_neverExposesRole() throws Exception {

        // ACT
        JsonNode json = toJsonNode(dto);

        // ASSERT
        // @JsonIgnore esconde o campo, tanto pelo nome Java quanto pelo "Perfil".
        assertAll(
                () -> assertFalse(json.has("role")),
                () -> assertFalse(json.has("Perfil"))
        );
    }

    @Test
    @DisplayName("JSON: campos nulos são omitidos (@JsonInclude NON_NULL)")
    void json_omitsNullFields() throws Exception {

        // ARRANGE
        PersonDTOV2 partial = new PersonDTOV2();
        partial.setId(ID);
        partial.setFirstName(FIRST_NAME);

        // ACT
        JsonNode json = toJsonNode(partial);
        System.out.println(json); // <-- imprime no console

        // ASSERT
        assertAll(
                () -> assertEquals(2, json.size()),
                () -> assertTrue(json.has("ID_usuário")),
                () -> assertTrue(json.has("Nome")),
                () -> assertFalse(json.has("CPF")),
                () -> assertFalse(json.has("Email")),
                () -> assertFalse(json.has("Data_nascimento"))
        );
    }

    @Test
    @DisplayName("JSON: um DTO vazio gera um objeto JSON vazio")
    void json_whenDtoIsEmpty_generatesEmptyObject() throws Exception {

        // ACT
        String json = objectMapper.writeValueAsString(new PersonDTOV2());

        // ASSERT
        assertEquals("{}", json);
    }

    // ========================================================================
    // CpfMaskSerializer (testado diretamente)
    // ========================================================================

    @Test
    @DisplayName("CpfMaskSerializer: com 11 dígitos, mantém os 3 primeiros e os 2 últimos")
    void cpfMask_whenValueHasElevenDigits_masksTheMiddle() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.CpfMaskSerializer(), "98765432100");

        // ASSERT
        assertEquals(quoted("987.***.***-00"), result);
    }

    @Test
    @DisplayName("CpfMaskSerializer: com menos de 11 caracteres, devolve o valor original")
    void cpfMask_whenValueIsShorterThanEleven_returnsOriginalValue() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.CpfMaskSerializer(), "12345");

        // ASSERT
        assertEquals(quoted("12345"), result);
    }

    @Test
    @DisplayName("CpfMaskSerializer: com valor nulo, escreve null")
    void cpfMask_whenValueIsNull_writesNull() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.CpfMaskSerializer(), null);

        // ASSERT
        assertEquals("null", result);
    }

    // ========================================================================
    // EmailMaskSerializer (testado diretamente)
    // ========================================================================

    @Test
    @DisplayName("EmailMaskSerializer: com nome de 3+ caracteres, mantém 2 letras e mascara o resto")
    void emailMask_whenNameHasThreeOrMoreCharacters_keepsTwoLetters() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.EmailMaskSerializer(), "maria@empresa.com");

        // ASSERT
        assertEquals(quoted("ma***@empresa.com"), result);
    }

    @Test
    @DisplayName("EmailMaskSerializer: com nome de até 2 caracteres, mantém só a primeira letra")
    void emailMask_whenNameHasTwoOrFewerCharacters_keepsOnlyFirstLetter() throws IOException {

        // ACT
        String shortName = serializeWith(new PersonDTOV2.EmailMaskSerializer(), "ab@empresa.com");
        String oneLetter = serializeWith(new PersonDTOV2.EmailMaskSerializer(), "a@empresa.com");

        // ASSERT
        assertAll(
                () -> assertEquals(quoted("a***@empresa.com"), shortName),
                () -> assertEquals(quoted("a***@empresa.com"), oneLetter)
        );
    }

    @Test
    @DisplayName("EmailMaskSerializer: sem '@', devolve o valor original")
    void emailMask_whenValueHasNoAtSign_returnsOriginalValue() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.EmailMaskSerializer(), "emailinvalido");

        // ASSERT
        assertEquals(quoted("emailinvalido"), result);
    }

    @Test
    @DisplayName("EmailMaskSerializer: com valor nulo, escreve null")
    void emailMask_whenValueIsNull_writesNull() throws IOException {

        // ACT
        String result = serializeWith(new PersonDTOV2.EmailMaskSerializer(), null);

        // ASSERT
        assertEquals("null", result);
    }

    // ========================================================================
    // MÉTODOS AUXILIARES
    // ========================================================================

    /* Monta um DTO com todos os campos preenchidos. */
    private PersonDTOV2 buildFullDto() {
        return new PersonDTOV2(
                ID, FIRST_NAME, LAST_NAME, BIRTH_DATE, CPF,
                PHONE, EMAIL, ADDRESS, GENDER, ROLE
        );
    }

    /* Converte o DTO em uma árvore JSON navegável (get("Nome"), has("CPF")...). */
    private JsonNode toJsonNode(PersonDTOV2 target) throws Exception {
        return objectMapper.readTree(objectMapper.writeValueAsString(target));
    }

    /*
     * Executa um serializador sozinho, sem passar pelo DTO, e devolve o texto
     * que ele escreveu. Permite testar os casos de borda (null, valor curto)
     * que o @JsonInclude NON_NULL esconderia no teste pelo DTO.
     */
    private String serializeWith(JsonSerializer<String> serializer, String value) throws IOException {
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = new JsonFactory().createGenerator(writer)) {
            serializer.serialize(value, generator, null);
        }
        return writer.toString();
    }

    /* Um texto JSON vem entre aspas: "valor". */
    private String quoted(String value) {
        return "\"" + value + "\"";
    }
}

/*
 * ============================================================================
 * FORA DO ESCOPO DESTE ARQUIVO (E COMO COBRIR)
 * ============================================================================
 *
 * 1) FORMATO HAL DOS LINKS (_links.self.href)
 *    Aqui o ObjectMapper é simples, então o JSON dos links não é o do Spring
 *    HATEOAS. O formato final dos links é testado no PersonControllerV2Test
 *    (@WebMvcTest + MockMvc).
 *
 * 2) CONVERSÃO ENTIDADE <-> DTO
 *    É responsabilidade do Mapper e já tem teste próprio.
 *
 * Divisão de responsabilidades nos testes:
 *
 *   DTO        -> dados, equals, máscara   -> JUnit puro (este arquivo)
 *   Controller -> contrato HTTP e links    -> @WebMvcTest + MockMvc
 *   Service    -> regras de negócio        -> JUnit + Mockito
 * ============================================================================
 */