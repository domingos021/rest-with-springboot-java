package com.dinisjovete.restwithspringbootjava.integration_tests.config.cors;

import com.dinisjovete.restwithspringbootjava.config.cors.CorsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * ============================================================================
 * TESTE DA REGRA GLOBAL DE CORS - CorsConfig
 * ============================================================================
 *
 * O QUE ESTÁ SENDO TESTADO
 * ------------------------
 * A classe CorsConfig (config.cors): quais origens, métodos e cabeçalhos o
 * navegador pode usar para consumir a API, e por quanto tempo ele guarda a
 * resposta do preflight (maxAge = 3600).
 *
 *
 * O QUE É CORS (resumo para este teste)
 * -------------------------------------
 * O navegador bloqueia um front-end (http://localhost:3000) de ler respostas de
 * outra origem (http://localhost:8080) a menos que o SERVIDOR autorize por
 * cabeçalhos. Existem dois momentos:
 *
 *   1) PREFLIGHT: antes de uma requisição "não simples" (PUT, PATCH, DELETE,
 *      ou com cabeçalhos como Authorization), o navegador pergunta ao servidor
 *      com uma requisição OPTIONS:
 *
 *          OPTIONS /recurso
 *          Origin: http://localhost:3000
 *          Access-Control-Request-Method: PATCH
 *
 *      O servidor responde se autoriza (Access-Control-Allow-*) ou recusa (403).
 *
 *   2) REQUISIÇÃO REAL: o navegador envia a requisição de verdade (GET, POST,
 *      PATCH...) com o cabeçalho Origin, e o servidor devolve
 *      Access-Control-Allow-Origin se a origem for permitida.
 *
 *
 * POR QUE UM CONTROLLER "SONDA" (CorsProbeController)
 * ---------------------------------------------------
 * A regra de CORS é GLOBAL (registry.addMapping("/**")): vale para qualquer
 * rota. Para testá-la não precisamos de PersonControllerV2, do Service nem do
 * banco. Um controller mínimo, declarado dentro deste arquivo, com um método
 * para cada verbo, é suficiente. Vantagens:
 *   - O teste não quebra quando as rotas reais mudam.
 *   - Um único foco: CORS. O contrato HTTP das rotas fica nos testes de cada
 *     controller (PersonControllerV2Test).
 *
 *
 *      Requisição HTTP simulada (com cabeçalho Origin)
 *               |
 *               v
 *          MockMvc ---------> DispatcherServlet
 *                                   |
 *                                   v
 *                         regra de CORS (CorsConfig)   <- UNIDADE TESTADA (real)
 *                                   |
 *                      permitido?   |   não permitido?
 *                          |        +-------> 403 (o controller nem é chamado)
 *                          v
 *                   CorsProbeController (sonda)
 *
 *
 * PADRÃO DE ESCRITA
 * -----------------
 *   ARRANGE : monta a requisição simulada (verbo, URL, cabeçalhos).
 *   ACT     : mockMvc.perform(...)
 *   ASSERT  : status e cabeçalhos Access-Control-* da RESPOSTA.
 *
 * Nomes: metodo_quandoCondicao_resultadoEsperado
 * ============================================================================
 */
@WebMvcTest(controllers = CorsConfigTest.CorsProbeController.class)
/*
 * @WebMvcTest(controllers = ...)
 *
 * Sobe só a "fatia web" do Spring e só a sonda como controller. Os controllers
 * reais (Person, Book) NÃO são carregados, então não precisamos mockar nenhum
 * Service. Não há Spring Security no projeto, então não há filtros a desligar.
 */
@Import({CorsConfig.class, CorsConfigTest.CorsProbeController.class})
/*
 * @Import
 *
 * Declara explicitamente o que este teste precisa: a CorsConfig REAL (a classe
 * testada) e a sonda. O @WebMvcTest já tende a encontrar ambas pela varredura,
 * mas o @Import deixa a dependência visível e não depende dessa varredura.
 */
@TestPropertySource(properties =
        "app.cors.allowed-origins=http://localhost:3000,http://localhost:5173")
        /*
         * @TestPropertySource
         *
         * Fixa as origens permitidas DENTRO do teste. Sem isso, o valor viria de
         * application.properties, que lê a variável de ambiente CORS_ORIGINS: se ela
         * estivesse definida na máquina (ou no CI), o teste passaria ou falharia
         * conforme o ambiente. Teste bom dá o mesmo resultado em qualquer máquina.
         *
         * Os valores são os mesmos padrões de desenvolvimento do application.properties
         * (React/Next na 3000 e Vite na 5173). A vírgula separa as origens, e o Spring
         * converte a String em String[] no @Value da CorsConfig.
         */
class CorsConfigTest {

    private static final String URL = "/cors-probe";

    private static final String ORIGIN_REACT = "http://localhost:3000";
    private static final String ORIGIN_VITE = "http://localhost:5173";

    @Autowired
    private MockMvc mockMvc;

    // ========================================================================
    // PREFLIGHT (OPTIONS) - origem permitida
    // ========================================================================

    @ParameterizedTest(name = "preflight de {0} vindo de origem permitida")
    @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
    @DisplayName("Preflight: origem permitida pedindo cada método da lista, responde 200 e autoriza")
    void preflight_whenOriginAndMethodAreAllowed_returns200AndAllowHeaders(String method) throws Exception {

        // ARRANGE + ACT + ASSERT
        // @ParameterizedTest roda este mesmo teste uma vez para cada valor de
        // @ValueSource. Assim provamos, em um só código, que GET, POST, PUT,
        // PATCH e DELETE estão liberados (inclusive o PATCH).
        //
        // Os dois cabeçalhos abaixo são o que o navegador envia de verdade e o
        // que faz o Spring reconhecer a requisição como PREFLIGHT:
        //   Origin                          -> quem está pedindo
        //   Access-Control-Request-Method   -> o método que ele quer usar depois
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGIN_REACT)
                        .header("Access-Control-Request-Method", method))
                .andExpect(status().isOk())
                // O servidor devolve a PRÓPRIA origem quando ela é permitida.
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN_REACT))
                // A lista de métodos autorizados deve conter o pedido.
                .andExpect(header().string("Access-Control-Allow-Methods", containsString(method)));
    }

    @Test
    @DisplayName("Preflight: a segunda origem configurada (Vite, 5173) também é permitida")
    void preflight_whenOriginIsTheSecondConfiguredOne_returns200() throws Exception {

        // ACT + ASSERT
        // Prova que a lista de origens foi dividida corretamente pela vírgula:
        // se só a primeira funcionasse, o @Value String[] estaria errado.
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGIN_VITE)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN_VITE));
    }

    @Test
    @DisplayName("Preflight: cabeçalhos customizados (Authorization, Content-Type) são aceitos")
    void preflight_whenRequestingCustomHeaders_allowsThem() throws Exception {

        // ACT + ASSERT
        // A regra usa allowedHeaders("*"): qualquer cabeçalho é aceito. O
        // navegador lista os que pretende enviar em Access-Control-Request-Headers
        // e o servidor deve autorizá-los em Access-Control-Allow-Headers.
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGIN_REACT)
                        .header("Access-Control-Request-Method", "PUT")
                        .header("Access-Control-Request-Headers", "Authorization, Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Authorization")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }

    @Test
    @DisplayName("Preflight: informa que o resultado pode ser guardado por 3600 segundos (1 hora)")
    void preflight_returnsMaxAgeOf3600Seconds() throws Exception {

        // ACT + ASSERT
        // maxAge(3600) evita que o navegador repita o preflight a cada
        // requisição durante 1 hora. Se alguém alterar o valor na CorsConfig,
        // este teste avisa.
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGIN_REACT)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    @DisplayName("Preflight: não autoriza credenciais (cookies) porque allowCredentials está desligado")
    void preflight_doesNotAllowCredentials() throws Exception {

        // ACT + ASSERT
        // Na CorsConfig, allowCredentials(true) está comentado. Este teste
        // protege essa decisão de segurança: se alguém ligar credenciais sem
        // querer, ele quebra e obriga a revisar a regra com calma (credenciais
        // exigem origens exatas, nunca "*").
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGIN_REACT)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }

    // ========================================================================
    // PREFLIGHT (OPTIONS) - origem NÃO permitida
    // ========================================================================

    @ParameterizedTest(name = "preflight vindo de {0}")
    @ValueSource(strings = {
            "http://evil.example.com",   // outro domínio qualquer
            "http://localhost:3001",     // mesma máquina, porta diferente
            "https://localhost:3000"     // mesma porta, protocolo diferente (https)
    })
    @DisplayName("Preflight: origem não permitida, responde 403 e não autoriza nada")
    void preflight_whenOriginIsNotAllowed_returns403WithoutAllowOrigin(String origin) throws Exception {

        // ACT + ASSERT
        // A origem é a combinação protocolo + domínio + porta, e a comparação é
        // EXATA. Por isso porta ou protocolo diferentes contam como outra origem.
        mockMvc.perform(options(URL)
                        .header("Origin", origin)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    // ========================================================================
    // REQUISIÇÃO REAL (depois do preflight, ou GET/POST simples)
    // ========================================================================

    @ParameterizedTest(name = "requisição real {0} vinda de origem permitida")
    @ValueSource(strings = {"GET", "POST", "PUT", "PATCH", "DELETE"})
    @DisplayName("Requisição real: origem permitida, responde 200 e devolve Access-Control-Allow-Origin")
    void actualRequest_whenOriginIsAllowed_returns200WithAllowOrigin(String method) throws Exception {

        // ACT + ASSERT
        // request(HttpMethod, url) monta uma requisição com o verbo informado.
        // Sem o Access-Control-Allow-Origin na resposta, o navegador esconderia
        // a resposta do código JavaScript do front-end.
        mockMvc.perform(request(HttpMethod.valueOf(method), URL)
                        .header("Origin", ORIGIN_REACT))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGIN_REACT));
    }

    @Test
    @DisplayName("Requisição real: origem não permitida, responde 403")
    void actualRequest_whenOriginIsNotAllowed_returns403() throws Exception {

        // ACT + ASSERT
        // Além do navegador, o próprio servidor recusa a requisição cross-origin
        // de origem desconhecida: o controller nem chega a ser executado.
        mockMvc.perform(get(URL)
                        .header("Origin", "http://evil.example.com"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    @Test
    @DisplayName("Requisição sem cabeçalho Origin (não é CORS): responde 200 normalmente")
    void actualRequest_withoutOriginHeader_isNotTreatedAsCors() throws Exception {

        // ACT + ASSERT
        // Ferramentas como Postman, curl ou outro servidor chamando a API não
        // enviam Origin: não é uma requisição CORS e deve funcionar. Lembrete
        // importante: CORS protege o NAVEGADOR do usuário, não é autenticação
        // nem barreira contra clientes que não são navegadores.
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    // ========================================================================
    // CONTROLLER SONDA (só existe para este teste)
    // ========================================================================

    /*
     * Um endpoint por verbo, todos na mesma rota, sem regra de negócio. Serve
     * apenas para o Spring ter algo a responder quando a regra de CORS permite
     * a requisição.
     */
    @RestController
    @RequestMapping(URL)
    static class CorsProbeController {

        @GetMapping
        public String get() {
            return "ok";
        }

        @PostMapping
        public String post() {
            return "ok";
        }

        @PutMapping
        public String put() {
            return "ok";
        }

        @PatchMapping
        public String patch() {
            return "ok";
        }

        @DeleteMapping
        public String delete() {
            return "ok";
        }
    }
}

/*
 * ============================================================================
 * FORA DO ESCOPO DESTE ARQUIVO
 * ============================================================================
 *
 * 1) SPRING SECURITY
 *    O projeto não tem SecurityConfig hoje, então a regra de CORS do Spring MVC
 *    (CorsConfig) é a única em vigor. Se um dia você adicionar Spring Security,
 *    o preflight (OPTIONS, sem credenciais) pode ser barrado ANTES do CORS
 *    atuar, e será preciso habilitar .cors(...) na cadeia de segurança. Nesse
 *    dia, revise este teste: ele precisará importar a SecurityConfig.
 *
 * 2) CONTRATO DAS ROTAS REAIS
 *    Status, corpo, validação e links HATEOAS ficam em PersonControllerV2Test.
 *
 * 3) CORS EM AMBIENTE REAL
 *    Este teste prova a REGRA. Quem aplica a regra de fato é o navegador, e só
 *    um teste com front-end real (ou um teste de integração com o servidor
 *    subido) mostraria o comportamento de ponta a ponta.
 * ============================================================================
 */

//./mvnw test -Dtest=CorsConfigTest