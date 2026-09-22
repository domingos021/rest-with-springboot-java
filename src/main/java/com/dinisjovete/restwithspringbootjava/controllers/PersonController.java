package com.dinisjovete.restwithspringbootjava.controllers;
import com.dinisjovete.restwithspringbootjava.services.PersonService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {

    // 🟢 1. Adicionamos o 'final' para garantir imutabilidade e segurança
    private final PersonService service;

    // 🟢 2. O construtor faz a injeção automaticamente no Spring Boot 3.4
    public PersonController(PersonService personService) {
        this.service = personService;
    }
}

/*
 * ==========================================================================
 * COMPARAÇÃO: SEM INJEÇÃO DE DEPENDÊNCIA VS. COM INJEÇÃO DO SPRING BOOT (3.4)
 * ==========================================================================
 *
 * 1. O CENÁRIO MANUAL (SEM INJEÇÃO DE DEPENDÊNCIA):
 *    No modelo manual, o próprio Controller é responsável por "dar vida"
 *    ao serviço usando a palavra `new`.
 *
 *    EXEMPLO PRÁTICO:
 *    ----------------------------------------------------------------------
 *    @RestController
 *    @RequestMapping("/person")
 *    public class PersonControllerManual {
 *
 *        // 🔴 O Controller cria o objeto manualmente (Acoplamento Forte)
 *        private final PersonService personService = new PersonService();
 *
 *        @GetMapping(value = "/{id}")
 *        public Person findById(@RequestParam(value = "id") Long id) {
 *            return personService.findById(id);
 *        }
 *    }
 *    ----------------------------------------------------------------------
 *    * Problema: Se o `PersonService` precisar de outras dependências no futuro
 *      (como um repositório de banco de dados), o `new PersonService()` vai
 *      quebrar e você terá que mexer diretamente no Controller.
 *
 *
 * 2. O CENÁRIO COM SPRING BOOT (INJEÇÃO DE DEPENDÊNCIA POR CONSTRUTOR):
 *    No modelo profissional, o Spring gerencia o ciclo de vida dos objetos
 *    e entrega a dependência pronta para o Controller.
 *
 *    EXEMPLO PRÁTICO (Spring Boot 3.4+):
 *    ----------------------------------------------------------------------
 *    // A classe de serviço vira um "Bean" gerenciado pelo Spring
 *    @Service
 *    public class PersonService {
 *        public Person findById(Long id) {
 *            return new Person();
 *        }
 *    }
 *
 *    @RestController
 *    @RequestMapping("/person")
 *    public class PersonControllerWithDI {
 *
 *        // 🟢 Declaramos o serviço como 'final', mas SEM dar 'new'
 *        private final PersonService personService;
 *
 *        // Injeção por Construtor:
 *        // Na versão 3.4, se a classe tem APENAS UM construtor, o Spring Boot
 *        // injeta o serviço automaticamente (não precisa da anotação @Autowired).
 *        public PersonControllerWithDI(PersonService personService) {
 *            this.personService = personService;
 *        }
 *
 *        @GetMapping(value = "/{id}")
 *        public Person findById(@RequestParam(value = "id") Long id) {
 *            return personService.findById(id);
 *        }
 *    }
 *    ----------------------------------------------------------------------
 *    * Vantagem: O Controller não se preocupa em *como* o serviço é criado.
 *      Garante desacoplamento, imutabilidade, economia de memória (Singleton)
 *      e facilidade para testes automatizados.
 * ==========================================================================
 */

/*
 * ==========================================================================
 * BOAS PRÁTICAS: INJEÇÃO POR CONSTRUTOR VS. FIELD INJECTION (@Autowired)
 * ==========================================================================
 *
 * 1. POR QUE A INJEÇÃO POR CONSTRUTOR É MAIS PROFISSIONAL?
 *    Embora colocar `@Autowired` direto no atributo funcione (Field Injection),
 *    a comunidade Java e o Spring recomendam a Injeção por Construtor pelos
 *    seguintes motivos cruciais:
 *
 *    - **Imutabilidade (`final`):** Podemos declarar o serviço como `private final`,
 *      garantindo que a dependência nunca será alterada ou corrompida após a
 *      criação do objeto.
 *    - **Facilidade em Testes Unitários:** Fica muito mais fácil testar o Controller
 *      passando objetos simulados (*mocks*) diretamente para o construtor,
 *      sem precisar subir o contexto inteiro do Spring.
 *    - **Prevenção de Erros (Fail-Fast):** A aplicação avisa ou falha logo na
 *      inicialização se faltar alguma dependência obrigatória, em vez de
 *      lançar um erro inesperado apenas quando o método for acessado.
 *
 * 2. EXEMPLO DA ABORDAGEM PROFISSIONAL (Spring Boot 3.4+):
 *    ----------------------------------------------------------------------
 *    @RestController
 *    @RequestMapping("/person")
 *    public class PersonController {
 *
 *        // Atributo imutável e seguro
 *        private final PersonService personService;
 *
 *        // Injeção por Construtor: o Spring Boot injeta automaticamente aqui
 *        public PersonController(PersonService personService) {
 *            this.personService = personService;
 *        }
 *    }
 *    ----------------------------------------------------------------------
 * ==========================================================================
 */
