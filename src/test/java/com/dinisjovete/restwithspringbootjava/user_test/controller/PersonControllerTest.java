package com.dinisjovete.restwithspringbootjava.user_test.controller;

import com.dinisjovete.restwithspringbootjava.controllers.person.v1.PersonController;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonInsertDTO;
import com.dinisjovete.restwithspringbootjava.data.dto.person.v1.PersonUpdateDTO;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.exception.project_exception.ResourceNotFoundException;
import com.dinisjovete.restwithspringbootjava.services.person.v1.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
*  MVC
* M -> Model
* V -> View
* C -> Controller
* **********
* -> @WebMvcTest(PersonController.class)
* "Não carregue minha aplicação inteira."
* "Carregue apenas a camada WEB."
* EX:
    * Spring Context

            NÃO

    Controller (outros)
    Service (real)
    Repository
    Banco
    Security
    JPA
    etc...
    *
    * Somente
    Spring Context

    Controller (específico)
    MockMvc
    Jackson (JSON)

    Somente isso.
 */

@WebMvcTest(PersonController.class)  // Configura o contexto web apenas para testar a classe PersonController de forma isolada
public class PersonControllerTest {

    /*
     * @MockitoBean: Cria uma versão falsa (um dublê/mock) da classe PersonService real.
     * O Spring substitui o serviço real por este clone falso em tempo de execução
     * apenas para este teste, impedindo que o sistema acesse o banco de dados.
     */
    @MockitoBean
    private PersonService service;

    // O Spring injeta automaticamente esta classe nativa do ecossistema de testes
    @Autowired
    private MockMvc mockMvc;

    // Utilitário nativo do Spring para converter objetos Java em strings JSON e vice-versa
    @Autowired
    private ObjectMapper objectMapper;

    private PersonDTO personDTO;
    private PersonInsertDTO personInsertDTO;
    private PersonUpdateDTO personUpdateDTO;

    // Executado automaticamente antes de cada método de teste para preparar os dados (setup)
    @BeforeEach
    void setUp() {
        // Inicializa um PersonDTO para simular as respostas de consulta
        personDTO = new PersonDTO(
                1L,
                "Ayrton",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );

        // Inicializa um PersonInsertDTO para simular o cadastro de uma nova pessoa
        personInsertDTO = new PersonInsertDTO();
        personInsertDTO.setFirstName("Nikola");
        personInsertDTO.setLastName("Tesla");
        personInsertDTO.setCpf("22233344455");
        personInsertDTO.setEmail("nikola.tesla@email.com");
        personInsertDTO.setPassword("123456");
        personInsertDTO.setAddress("Smiljan - Croácia");
        personInsertDTO.setGender("Male");
        personInsertDTO.setRole(PersonRole.CLIENT);

        // Inicializa um PersonUpdateDTO para simular a alteração de dados
        personUpdateDTO = new PersonUpdateDTO();
        personUpdateDTO.setFirstName("Ayrton (Updated)");
        personUpdateDTO.setLastName("Senna");
        personUpdateDTO.setAddress("Rio de Janeiro - Brasil");
        personUpdateDTO.setGender("Male");
    }

    // ========================================================================
    // 🟢 TESTES DE SUCESSO (HAPPY PATH - TESTES DE INTEGRAÇÃO WEB / MOCK)
    // ========================================================================

    @Test
    public void testFindById() throws Exception {
        // Ensinamos o serviço falso a retornar o PersonDTO quando buscarem pelo ID 1
        when(service.findById(1L)).thenReturn(personDTO);

        // Executamos a requisição GET e validamos os campos do JSON retornado pelo Controller
        mockMvc.perform(get("/person/1"))
                /*
                 * Garante que a requisição retornou o status HTTP 200 (OK)
                 */
                .andExpect(status().isOk())

                /*
                 * Verifica se a entidade existe validando a presença do ID no JSON
                 */
                .andExpect(jsonPath("$.id").exists())

                /*
                 * Verifica se os campos do JSON retornado correspondem aos valores esperados
                 */
                .andExpect(jsonPath("$.firstName").value("Ayrton"))
                .andExpect(jsonPath("$.lastName").value("Senna"))
                .andExpect(jsonPath("$.cpf").value("11122233344"))
                .andExpect(jsonPath("$.email").value("ayrton.senna@email.com"))
                .andExpect(jsonPath("$.address").value("São Paulo - Brasil"))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    public void testFindAll() throws Exception {
        List<PersonDTO> list = List.of(personDTO);

        // Ensinamos o serviço falso a retornar uma lista contendo a pessoa
        when(service.findAll()).thenReturn(list);

        // Executamos a requisição GET na raiz (/person) e validamos o array JSON retornado
        mockMvc.perform(get("/person"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Ayrton"))
                .andExpect(jsonPath("$[0].email").value("ayrton.senna@email.com"));
    }

    @Test
    public void testCreate() throws Exception {
        PersonDTO createdDTO = new PersonDTO(
                2L,
                "Nikola",
                "Tesla",
                "22233344455",
                "nikola.tesla@email.com",
                "Smiljan - Croácia",
                "Male",
                PersonRole.CLIENT
        );

        // Configura o mock para o método create recebendo qualquer PersonInsertDTO
        when(service.create(any(PersonInsertDTO.class))).thenReturn(createdDTO);

        // Executa a requisição POST enviando o objeto em formato JSON no corpo (RequestBody)
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personInsertDTO)))
                .andExpect(status().isCreated()) // Valida o HTTP Status 201 Created
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstName").value("Nikola"))
                .andExpect(jsonPath("$.email").value("nikola.tesla@email.com"));
    }

    @Test
    public void testUpdate() throws Exception {
        PersonDTO updatedDTO = new PersonDTO(
                1L,
                "Ayrton (Updated)",
                "Senna",
                "11122233344",
                "ayrton.senna@email.com",
                "Rio de Janeiro - Brasil",
                "Male",
                PersonRole.ADMIN
        );

        // Configura o mock para o método update
        when(service.update(eq(1L), any(PersonUpdateDTO.class))).thenReturn(updatedDTO);

        // Executa a requisição PUT enviando o ID na URL e o JSON no corpo
        mockMvc.perform(put("/person/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ayrton (Updated)"))
                .andExpect(jsonPath("$.address").value("Rio de Janeiro - Brasil"));
    }

    @Test
    public void testDelete() throws Exception {
        // Como o método delete retorna void, usamos doNothing() do Mockito
        doNothing().when(service).delete(1L);

        // Executa a requisição DELETE para o ID 1 e espera status 204 No Content
        mockMvc.perform(delete("/person/1"))
                .andExpect(status().isNoContent());
    }

    // ========================================================================
    // 🔴 TESTES DE ERRO / EXCEÇÃO (SAD PATH - VALIDAÇÃO DE FLUXOS ALTERNATIVOS)
    // ========================================================================

    @Test
    public void testFindByIdNotFound() throws Exception {
        // Configura o serviço mock para lançar a exceção personalizada de recurso não encontrado
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Resource not found"));

        // Executa a requisição GET para um ID inexistente e valida se o Controller lida com a exceção retornando 404 Not Found
        mockMvc.perform(get("/person/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    public void testCreateInvalidData() throws Exception {
        // Cria um DTO completamente vazio para violar as regras de validação (@NotBlank, etc.)
        PersonInsertDTO invalidDTO = new PersonInsertDTO();

        // Executa a requisição POST com dados inválidos e valida se a camada web barra com status 400 Bad Request
        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }
}


/*
 * ============================================================================
 * TESTE DE CONTROLLER - PERSONCONTROLLERTEST
 * ============================================================================
 *
 * Objetivo:
 *      Testar somente a camada Controller de forma isolada.
 *
 *      O teste NÃO utiliza:
 *
 *          ❌ Banco de Dados
 *          ❌ Repository real
 *          ❌ Service real
 *
 *      Ele utiliza:
 *
 *          ✅ MockMvc      -> Simula requisições HTTP
 *          ✅ MockitoBean  -> Cria um Service falso (Mock)
 *          ✅ JUnit 5      -> Executa los testes
 *
 *
 * ============================================================================
 * FLUXO NORMAL DA APLICAÇÃO
 * ============================================================================
 *
 *
 * Cliente (Postman / Front-end)
 *
 *              |
 *              | GET /person/{id}
 *              ↓
 *
 *      +----------------+
 *      |  Controller    |
 *      | PersonController|
 *      +----------------+
 *
 *              |
 *              | chama
 *              ↓
 *
 *      +----------------+
 *      |    Service     |
 *      | PersonService  |
 *      +----------------+
 *
 *              |
 *              | consulta
 *              ↓
 *
 *      +----------------+
 *      |  Repository    |
 *      +----------------+
 *
 *              |
 *              ↓
 *
 *          Banco de Dados
 *
 *
 * ============================================================================
 * FLUXO DURANTE O TESTE
 * ============================================================================
 *
 *
 *              JUnit
 *                |
 *                ↓
 *
 *           MockMvc
 *                |
 *                | Simula:
 *                |
 *                | GET http://localhost:8080/person/1
 *                ↓
 *
 *      +----------------------+
 *      |   PersonController   |
 *      +----------------------+
 *
 *                |
 *                |
 *                ↓
 *
 *      +----------------------+
 *      | PersonService (Mock) |
 *      +----------------------+
 *
 *                |
 *                |
 *        when(service.findById(1L))
 *                |
 *                ↓
 *
 *          Retorna PersonDTO falso
 *
 *                |
 *                ↓
 *
 *      Controller transforma em JSON
 *
 *                |
 *                ↓
 *
 *          Teste valida:
 *
 *          ✔ Status HTTP 200 / 201 / 204 / 400 / 404
 *          ✔ Campos do JSON
 *          ✔ Dados retornados
 *
 *
 * ============================================================================
 * IDEIA PRINCIPAL
 * ============================================================================
 *
 * O teste não verifica se o Service funciona.
 *
 * O Service terá seu próprio teste.
 *
 * Aqui a pergunta é:
 *
 * "O Controller recebe uma requisição HTTP e devolve a resposta correta?"
 *
 * ============================================================================
 */