package com.dinisjovete.restwithspringbootjava.controllers.person.v2;

import com.dinisjovete.restwithspringbootjava.config.hateoas.v2.PersonHateoasAssemblerV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonInsertDTOV2;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v2.PersonUpdateDTOV2;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.services.person.v2.PersonServiceV2;
import com.dinisjovete.restwithspringbootjava.user_test.dozer.unittetests.mapper.mocks.v2.MockPersonV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * ============================================================================
 * TESTE DA CAMADA WEB - PersonControllerV2 (v2)
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * O contrato HTTP do Controller: rota, verbo, status code, corpo da resposta,
 * validação de entrada, negociação de conteúdo e links HATEOAS.
 *
 * O Controller NÃO tem regra de negócio. Ele traduz HTTP <-> Java. Por isso
 * aqui o Service é um mock e só conferimos se a "tradução" está correta.
 *
 *
 *      Requisição HTTP simulada
 *               |
 *               v
 *          MockMvc  ---------------->  DispatcherServlet (do Spring MVC)
 *                                              |
 *                                              v
 *                                      PersonControllerV2    <- UNIDADE TESTADA (real)
 *                                         |          |
 *                                         |          +----> PersonHateoasAssemblerV2 (real)
 *                                         v
 *                                    PersonServiceV2         <- MOCK (@MockitoBean)
 *
 *
 * POR QUE @WebMvcTest E NÃO @SpringBootTest
 * -----------------------------------------
 * @WebMvcTest sobe só a "fatia web" do Spring: controllers, @ControllerAdvice,
 * conversores JSON/XML, validação. Não sobe banco, repositórios nem services.
 * Resultado: teste rápido e focado. @SpringBootTest sobe a aplicação inteira e
 * fica reservado para testes de integração.
 *
 *
 * O QUE UM TESTE DE CONTROLLER DEVE PROVAR
 * ----------------------------------------
 *   - Status code certo para cada situação (200, 201, 204, 400, 404, 406, 415).
 *   - O corpo da requisição é convertido corretamente em DTO e chega ao Service.
 *   - Entrada inválida é barrada ANTES de chegar ao Service.
 *   - Erros de negócio viram o status HTTP correto, sem vazar detalhe interno.
 *   - A resposta nunca expõe dados sensíveis (senha).
 *   - Links HATEOAS presentes e apontando para o recurso certo.
 *
 *
 * PADRÃO DE ESCRITA
 * -----------------
 *   ARRANGE : prepara dados e ensina o mock do Service o que responder.
 *   ACT     : mockMvc.perform(...) dispara a requisição simulada.
 *   ASSERT  : andExpect(...) confere status e corpo da RESPOSTA HTTP.
 *   VERIFY  : confere o que chegou ao Service (e o que NÃO deveria ter chegado).
 *
 * Nomes: metodo_quandoCondicao_resultadoEsperado
 * ============================================================================
 */
@WebMvcTest(PersonControllerV2.class)
/*
 * @WebMvcTest(PersonControllerV2.class)
 *
 * Carrega somente este controller (e @ControllerAdvice, conversores e
 * WebMvcConfigurer encontrados na varredura de componentes). Se o seu
 * ControllerAdvice estiver no pacote da aplicação, ele participa dos testes e
 * é isso que faz ResourceNotFoundException virar 404 aqui.
 */
@AutoConfigureMockMvc(addFilters = false)
/*
 * addFilters = false
 *
 * Desliga a cadeia de filtros (incluindo o Spring Security, se estiver no
 * projeto). Sem isso, as requisições de teste seriam barradas por login e CSRF
 * antes de chegar ao controller.
 *
 * Consequência importante: ESTE arquivo NÃO testa autenticação nem autorização.
 * Isso é de propósito, para isolar o contrato HTTP. Os testes de segurança de
 * rota ficam em uma classe própria (veja a nota no fim do arquivo).
 */
@Import(PersonHateoasAssemblerV2.class)
        /*
         * @Import(PersonHateoasAssemblerV2.class)
         *
         * @WebMvcTest NÃO carrega beans comuns (@Component), e o Controller precisa do
         * assembler no construtor. Importamos o assembler REAL para poder testar os links
         * HATEOAS de verdade. A alternativa seria mockar o assembler, mas aí os links
         * deixariam de ser verificados.
         */
class PersonControllerV2Test {

    private static final String BASE_URL = "/person/v2";

    /*
     * MockMvc: dispara requisições HTTP contra o controller SEM abrir porta de
     * rede nem subir servidor. É rápido e determinístico.
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * ObjectMapper do próprio Spring: usamos para transformar os DTOs dos mocks
     * em JSON, em vez de escrever JSON na mão. Assim o corpo da requisição
     * acompanha o DTO (se um campo mudar, o teste muda junto).
     */
    @Autowired
    private ObjectMapper objectMapper;

    /*
     * @MockitoBean: registra um mock do PersonServiceV2 no contexto do Spring e o
     * injeta no Controller. É o equivalente, para testes com contexto Spring, ao
     * @Mock + @InjectMocks que usamos no teste de service.
     *
     * Versões do Spring Boot:
     *   - 3.4 ou superior : @MockitoBean (org.springframework.test.context.bean.override.mockito)
     *   - anterior à 3.4  : @MockBean (org.springframework.boot.test.mock.mockito)
     */
    @MockitoBean
    private PersonServiceV2 service;

    /* Fábrica de dados fictícios (a mesma usada no teste de service). */
    private MockPersonV2 input;

    @BeforeEach
    void setUp() {
        input = new MockPersonV2();
    }

    // ========================================================================
    // GET /person/v2/{id}  ->  findById
    // ========================================================================

    @Test
    @DisplayName("GET /{id}: quando a pessoa existe, responde 200 com o corpo e o link self")
    void findById_whenPersonExists_returns200WithBodyAndSelfLink() throws Exception {

        // ARRANGE
        PersonDTOV2 dto = input.mockDTO(1);
        when(service.findById(1L)).thenReturn(dto);

        // ACT + ASSERT
        // jsonPath navega no JSON da resposta: "$" é a raiz, "$.ID_usuário" é o campo do id.
        // "_links.self.href" é onde o formato HAL (padrão do Spring HATEOAS) coloca
        // o link do próprio recurso.
        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                //isNotFound() -> quebra
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID_usuário").value(1))
                .andExpect(jsonPath("$.Nome").value("First Name Test1"))
                .andExpect(jsonPath("$.Sobrenome").value("Last Name Test1"))
                .andExpect(jsonPath("$.Género").value("Female"))
                .andExpect(jsonPath("$.Endereço").value("Address Test1"))
                //.andExpect(jsonPath("$.Perfil").value("ADMIN"))
                .andExpect(jsonPath("$._links.self.href", endsWith(BASE_URL + "/1")));
        // VERIFY
        verify(service).findById(1L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /{id}: a resposta nunca expõe a senha")
    void findById_neverExposesPassword() throws Exception {

        // ARRANGE
        when(service.findById(1L)).thenReturn(input.mockDTO(1));

        // ACT + ASSERT
        // doesNotExist() garante que o campo NÃO aparece no JSON. Protege a
        // invariante "senha nunca sai pela API": se alguém adicionar um campo
        // password ao PersonDTOV2, este teste quebra.
        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /{id}: quando o ID não existe, responde 404 sem vazar detalhes internos")
    void findById_whenPersonDoesNotExist_returns404WithoutLeakingInternals() throws Exception {

        // ARRANGE
        // O Service lança a exceção de negócio; o ControllerAdvice a converte em 404.
        when(service.findById(99L))
                .thenThrow(new ResourceNotFoundException("No record found for this ID!"));

        // ACT + ASSERT
        // O corpo do erro não pode conter nomes de pacote/classe (indício de
        // stack trace ou detalhe interno exposto ao cliente).
        mockMvc.perform(get(BASE_URL + "/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isNotFound()) //passa
                //.andExpect(status().isOk()) //quebra o sistema porque viola a regra do teste
                .andExpect(content().string(not(containsString("com.dinisjovete"))));

        // CONFIRMA QUE O CONTROLLER CHAMOU O SERVICE
        verify(service).findById(99L);
    }

    @Test
    @DisplayName("GET /{id}: quando o ID não é numérico, responde 400 e não aciona o Service")
    void findById_whenIdIsNotNumeric_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        // "abc" não converte para Long, então o Spring recusa antes do método
        // do controller ser chamado.
        //
        // Se este teste receber 500 em vez de 400, o seu ControllerAdvice está
        // capturando exceções demais (um handler genérico para Exception) e
        // transformando erro do CLIENTE em erro do SERVIDOR.
        mockMvc.perform(get(BASE_URL + "/{id}", "abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isBadRequest()); //passa
        //.andExpect(status().isNotFound()); //da erro

        // VERIFY
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("GET /{id}: quando o formato pedido não é suportado, responde 406")
    void findById_whenAcceptIsNotSupported_returns406() throws Exception {

        // ACT + ASSERT
        // O endpoint produz JSON, XML e YAML. Pedir text/html não é atendível.
        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .accept(MediaType.TEXT_HTML))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isNotAcceptable());
        //.andExpect(status().isNotFound()); //da erro

        // VERIFY
        verifyNoInteractions(service);
    }

    // ========================================================================
    // GET /person/v2  ->  findAll
    // ========================================================================

    @Test
    @DisplayName("GET /: responde 200 com a lista, o link de cada item e o link self da coleção")
    void findAll_whenThereArePeople_returns200WithItemsAndLinks() throws Exception {

        // ARRANGE
        when(service.findAll())
                .thenReturn(List.of(input.mockDTO(1), input.mockDTO(2)));

        // ACT + ASSERT
        // No HAL, uma coleção vem dentro de "_embedded". O NOME da lista é gerado
        // a partir do nome da classe (PersonDTOV2 -> personDTOV2List). Se você usar
        // @Relation no DTO, ajuste este nome.
        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isOk())
                //.andExpect(status().isNotFound()); //da erro
                /*
                 * Com Spring HATEOAS, um CollectionModel<PersonDTOV2> gera:
                 * {
                 *   "_embedded": { "personDTOV2List": [ {...}, {...} ] },
                 *   "_links": { "self": { "href": ".../person" } }
                 * }
                 * "$" = raiz do JSON | "." = entra no campo | "[0]" = item da lista
                 */
                .andExpect(jsonPath("$._embedded.personDTOV2List", hasSize(2)))

                // primeiro item
                .andExpect(jsonPath("$._embedded.personDTOV2List[0].ID_usuário").value(1))
                .andExpect(jsonPath("$._embedded.personDTOV2List[0].Nome").value("First Name Test1"))
                .andExpect(jsonPath("$._embedded.personDTOV2List[0]._links.self.href",
                        endsWith(BASE_URL + "/1")))

                // segundo item
                .andExpect(jsonPath("$._embedded.personDTOV2List[1].ID_usuário").value(2))
                .andExpect(jsonPath("$._embedded.personDTOV2List[1]._links.self.href",
                        endsWith(BASE_URL + "/2")))

                // link da própria coleção
                .andExpect(jsonPath("$._links.self.href", endsWith(BASE_URL)));
        // VERIFY
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("GET /: quando não há ninguém, responde 200 (e não 404) com coleção vazia")
    void findAll_whenThereIsNobody_returns200WithEmptyCollection() throws Exception {

        // ARRANGE
        when(service.findAll()).thenReturn(List.of());

        // ACT + ASSERT
        // Lista vazia é um resultado VÁLIDO, não um erro: o status correto é 200.
        // No HAL, uma coleção vazia omite "_embedded" e mantém "_links".
        mockMvc.perform(get(BASE_URL)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith(BASE_URL)));
    }

    // ========================================================================
    // POST /person/v2  ->  create
    // ========================================================================

    @Test
    @DisplayName("POST /: com corpo válido, responde 201 com o DTO criado, link self e sem senha")
    void create_whenBodyIsValid_returns201WithCreatedDto() throws Exception {

        // ARRANGE
        // O corpo da requisição é o DTO de entrada do mock convertido em JSON.
        PersonInsertDTOV2 body = input.mockInsertDTO(1);
        PersonDTOV2 created = input.mockDTO(1);

        when(service.create(any(PersonInsertDTOV2.class))).thenReturn(created);

        // ACT + ASSERT
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ID_usuário").value(1))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith(BASE_URL + "/1")));
        //.andExpect(jsonPath("$.email").value("email@email.com")); // <-- quebrar de propósito

        // VERIFY
        // O ArgumentCaptor captura o DTO que o Service realmente recebeu. Prova
        // que o JSON foi convertido corretamente (inclusive a senha, que o
        // Service precisa receber em texto puro para criptografar).
        ArgumentCaptor<PersonInsertDTOV2> captor = ArgumentCaptor.forClass(PersonInsertDTOV2.class);
        verify(service).create(captor.capture());
        assertEquals(body.getEmail(), captor.getValue().getEmail());
        assertEquals(body.getPassword(), captor.getValue().getPassword());
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("POST /: com JSON malformado, responde 400 e não aciona o Service")
    void create_whenBodyIsMalformedJson_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        // JSON incompleto: o Jackson não consegue ler o corpo.
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": "))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isBadRequest());

        // VERIFY
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /: com corpo que viola a validação (@Valid), responde 400 e não aciona o Service")
    void create_whenBodyFailsValidation_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        // Um objeto vazio "{}" deveria ser recusado pelas regras do
        // PersonInsertDTOV2 (@NotBlank, @Email, @Size etc.).
        //
        // ATENÇÃO: se este teste receber 201, descobrimos que o DTO NÃO tem
        // restrições suficientes (por exemplo, é possível cadastrar sem senha).
        // Nesse caso o teste está "vermelho" por um motivo real: a correção é
        // adicionar as anotações de validação ao DTO.
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isBadRequest());

        // VERIFY
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /: com Content-Type não suportado, responde 415")
    void create_whenContentTypeIsNotSupported_returns415() throws Exception {

        // ACT + ASSERT
        // O endpoint só consome JSON, XML e YAML.
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("texto qualquer"))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isUnsupportedMediaType());

        // VERIFY
        verifyNoInteractions(service);
    }

    // ========================================================================
    // PUT /person/v2/{id}  ->  update
    // ========================================================================

    @Test
    @DisplayName("PUT /{id}: com corpo válido, responde 200 com o DTO atualizado e link self")
    void update_whenBodyIsValid_returns200WithUpdatedDto() throws Exception {

        // ARRANGE
        PersonUpdateDTOV2 body = input.mockUpdateDTO(1);
        PersonDTOV2 updated = input.mockDTO(1);

        when(service.update(eq(1L), any(PersonUpdateDTOV2.class))).thenReturn(updated);

        // ACT + ASSERT
        mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ID_usuário").value(1))
                .andExpect(jsonPath("$.Nome").value("First Name Test1"))
                .andExpect(jsonPath("$._links.self.href", endsWith(BASE_URL + "/1")));

        // VERIFY
        // Confere que o ID da URL e o DTO do corpo chegaram juntos ao Service.
        ArgumentCaptor<PersonUpdateDTOV2> captor = ArgumentCaptor.forClass(PersonUpdateDTOV2.class);
        verify(service).update(eq(1L), captor.capture());
        assertEquals(body.getFirstName(), captor.getValue().getFirstName());
        assertEquals(body.getAddress(), captor.getValue().getAddress());
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("PUT /{id}: quando o ID não existe, responde 404")
    void update_whenPersonDoesNotExist_returns404() throws Exception {

        // ARRANGE
        when(service.update(eq(99L), any(PersonUpdateDTOV2.class)))
                .thenThrow(new ResourceNotFoundException("No record found for this ID!"));

        // ACT + ASSERT
        mockMvc.perform(put(BASE_URL + "/{id}", 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input.mockUpdateDTO(1))))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("com.dinisjovete"))));
    }

    @Test
    @DisplayName("PUT /{id}: com JSON malformado, responde 400 e não aciona o Service")
    void update_whenBodyIsMalformedJson_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        mockMvc.perform(put(BASE_URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": "))
                .andExpect(status().isBadRequest());

        // VERIFY
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("PUT /{id}: quando o ID não é numérico, responde 400 e não aciona o Service")
    void update_whenIdIsNotNumeric_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        mockMvc.perform(put(BASE_URL + "/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input.mockUpdateDTO(1))))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isBadRequest());

        // VERIFY
        verifyNoInteractions(service);
    }

    // ========================================================================
    // DELETE /person/v2/{id}  ->  delete
    // ========================================================================

    @Test
    @DisplayName("DELETE /{id}: quando a pessoa existe, responde 204 com corpo vazio")
    void delete_whenPersonExists_returns204WithEmptyBody() throws Exception {

        // ARRANGE
        // O método delete() do Service é void: um mock de método void já "não faz
        // nada" por padrão, então não precisamos configurar nenhum retorno.

        // ACT + ASSERT
        // 204 No Content: operação concluída, nada a devolver. O corpo precisa
        // estar realmente vazio.
        mockMvc.perform(delete(BASE_URL + "/{id}", 1L))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // VERIFY
        verify(service).delete(1L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @DisplayName("DELETE /{id}: quando o ID não existe, responde 404")
    void delete_whenPersonDoesNotExist_returns404() throws Exception {

        // ARRANGE
        // Para método void, a forma de ensinar o mock a lançar exceção é
        // doThrow(...).when(mock).metodo(...) (e não when(...).thenThrow).
        doThrow(new ResourceNotFoundException("No record found for this ID!"))
                .when(service).delete(99L);

        // ACT + ASSERT
        mockMvc.perform(delete(BASE_URL + "/{id}", 99L))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isNotFound())
                .andExpect(content().string(not(containsString("com.dinisjovete"))));
    }

    @Test
    @DisplayName("DELETE /{id}: quando o ID não é numérico, responde 400 e não aciona o Service")
    void delete_whenIdIsNotNumeric_returns400AndDoesNotCallService() throws Exception {

        // ACT + ASSERT
        mockMvc.perform(delete(BASE_URL + "/{id}", "abc"))
                .andDo(print()) // <-- imprime no console
                .andExpect(status().isBadRequest());

        // VERIFY
        verifyNoInteractions(service);
    }
}

/*
 * ============================================================================
 * FORA DO ESCOPO DESTE ARQUIVO (E COMO COBRIR)
 * ============================================================================
 *
 * 1) AUTENTICAÇÃO E AUTORIZAÇÃO
 *    Este teste desliga os filtros (addFilters = false). Para testar segurança
 *    de rota, crie uma classe separada SEM essa opção, importando a sua
 *    configuração de segurança, e cubra:
 *
 *      - sem login           -> 401
 *      - logado sem papel    -> 403
 *      - logado com papel    -> 200
 *      - POST/PUT/DELETE com CSRF ativo (se aplicável)
 *
 *    Ferramentas: @WithMockUser, SecurityMockMvcRequestPostProcessors.
 *
 * 2) REGRA DE NEGÓCIO
 *    Role no cadastro, senha criptografada, ID inexistente: são do Service e já
 *    estão em PersonServiceV2Test.
 *
 * 3) BANCO DE DADOS (CPF/e-mail únicos, consultas)
 *    São do Repository: @DataJpaTest, idealmente com Testcontainers.
 *
 * 4) FLUXO COMPLETO
 *    Controller + Service + banco juntos: @SpringBootTest + Testcontainers.
 *
 * Divisão de responsabilidades nos testes:
 *
 *   Controller -> contrato HTTP e links     -> @WebMvcTest + MockMvc (este arquivo)
 *   Service    -> regras de negócio         -> JUnit + Mockito
 *   Repository -> consultas e restrições    -> @DataJpaTest
 *   Integração -> o conjunto funcionando    -> @SpringBootTest
 * ============================================================================
 */

/*
 * JSONPath:
 *
 * "$"        = raiz do JSON
 * "."        = acessa um campo dentro do objeto atual
 * "[0]"      = acessa o primeiro elemento de uma lista (índice começa em 0)
 *
 *
 * Exemplo:
 *
 * {
 *   "_embedded": {
 *      "personDTOV2List": [
 *          {
 *             "ID_usuário": 1
 *          },
 *          {
 *             "ID_usuário": 2
 *          }
 *      ]
 *   }
 * }
 *
 *
 * 1º caso:
 *
 * "$._embedded.personDTOV2List"
 *
 * Vai até a lista de pessoas.
 *
 * hasSize(2) verifica se essa lista possui 2 elementos.
 *
 *
 * 2º caso:
 *
 * "$._embedded.personDTOV2List[0].ID_usuário"
 *
 * Vai até:
 *
 * raiz ($)
 *    |
 *    +-- _embedded
 *          |
 *          +-- personDTOV2List
 *                    |
 *                    +-- [0] primeira pessoa da lista
 *                              |
 *                              +-- ID_usuário
 *
 * Resultado:
 *
 * "ID_usuário": 1
 *
 */