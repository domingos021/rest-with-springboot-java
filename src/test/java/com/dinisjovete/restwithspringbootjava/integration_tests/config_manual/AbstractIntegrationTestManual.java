package com.dinisjovete.restwithspringbootjava.integration_tests.config_manual;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

/*
AbstractIntegrationTestManual

        |
        |
        ├── PostgreSQLContainer
        |       |
        |       └── Cria o banco PostgreSQL no Docker
        |
        |
        ├── Initializer
        |       |
        |       └── Configura o Spring para acessar esse banco
        |
        |
        └── ApplicationContext
                |
                └── Permite que o Spring suba a aplicação
 */



/*
 * Configuração manual: sem @ServiceConnection, nós mesmos
 * entregamos a URL, o usuário e a senha do container ao Spring.
 *
 * Fluxo:
 * static { start() } -> Container PostgreSQL sobe (uma vez por JVM)
 *      -> Initializer injeta as propriedades no ApplicationContext
 *      -> Spring Boot + Flyway + JPA conectam no container
 */
/*
 * ==========================================================
 * ENTENDENDO:
 *
 * AbstractIntegrationTestManual.Initializer.class
 * ==========================================================
 *
 * Esse trecho representa uma referência para uma classe.
 *
 * A leitura é:
 *
 *      AbstractIntegrationTestManual
 *              |
 *              |
 *              └── Initializer
 *                       |
 *                       └── .class
 *
 *
 * Ou seja:
 *
 * "Pegue a classe interna chamada Initializer,
 * que está dentro da classe AbstractIntegrationTestManual."
 *
 *
 * Não é uma classe separada chamada:
 *
 *      Initializer.class
 *
 * Ela é uma classe interna declarada dentro de:
 *
 *      AbstractIntegrationTestManual
 *
 *
 * Exemplo:
 *
 * public abstract class AbstractIntegrationTestManual {
 *
 *      static class Initializer {
 *
 *      }
 *
 * }
 *
 *
 * Para o Java, a classe interna recebe o nome:
 *
 *      AbstractIntegrationTestManual.Initializer
 *
 *
 * O .class no final significa que estamos passando
 * a referência da classe para o Spring, e não criando
 * um objeto.
 *
 *
 * Então:
 *
 * @ContextConfiguration(
 *      initializers =
 *      AbstractIntegrationTestManual.Initializer.class
 * )
 *
 *
 * significa:
 *
 * "Spring, utilize a classe interna Initializer,
 * que pertence à AbstractIntegrationTestManual,
 * como responsável por preparar o ApplicationContext
 * antes da aplicação iniciar."
 *
 * ==========================================================
 */
@ContextConfiguration(
        initializers = AbstractIntegrationTestManual.Initializer.class
)
public abstract class AbstractIntegrationTestManual {

    //1ª Cria o banco PostgreSQL no Docker
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    // Container singleton: sobe uma vez e é compartilhado por todas as
    // classes de teste. O Ryuk o derruba ao fim da JVM.
    // inicia o banco que foi criado
    static {
        postgres.start();
    }

    /*
       * essa e a classe interna do AbstractIntegrationTestManual
       * responsável por preparar o ApplicationContext
       * antes da aplicação iniciar."
     */
    //2ª Configura o Spring para acessar esse banco
    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        // 3ª Permite que o Spring suba a aplicação
        @Override // sobrescreve esta interface (ApplicationContextInitializer)
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertyValues.of(

                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    // Obtém a URL JDBC gerada pelo Testcontainers
                    // para o container PostgreSQL em execução.
                    // Exemplo:
                    // jdbc:postgresql://localhost:54321/test


                    "spring.datasource.username=" + postgres.getUsername(),
                    // Obtém o usuário configurado no container PostgreSQL.
                    // Normalmente:
                    // postgres


                    "spring.datasource.password=" + postgres.getPassword()
                    // Obtém a senha configurada no container PostgreSQL.

            ).applyTo(
                    applicationContext.getEnvironment()
            );
        }
    }
}

        /*
         * ==========================================================
         * APLICAÇÃO DAS PROPRIEDADES NO SPRING ENVIRONMENT
         * ==========================================================
         *
         * O método applyTo() aplica as propriedades criadas pelo
         * TestPropertyValues dentro do Environment do Spring.
         *
         *
         * O trecho:
         *
         *      applicationContext.getEnvironment()
         *
         * obtém o ambiente de configuração utilizado pelo Spring
         * durante a criação do ApplicationContext.
         *
         *
         * Neste momento, as informações vindas do
         * PostgreSQLContainer:
         *
         *      - spring.datasource.url
         *      - spring.datasource.username
         *      - spring.datasource.password
         *
         * são adicionadas ao ambiente do Spring.
         *
         *
         * Antes do applyTo():
         *
         *      PostgreSQLContainer
         *              |
         *              ▼
         *      URL / usuário / senha
         *      (apenas disponíveis no container)
         *
         *
         * Depois do applyTo():
         *
         *      PostgreSQLContainer
         *              |
         *              ▼
         *      TestPropertyValues
         *              |
         *              ▼
         *      Environment do Spring
         *              |
         *              ▼
         *      DataSource
         *              |
         *              ▼
         *      JPA + Hibernate + Flyway
         *
         *
         * Ou seja:
         *
         * O applyTo() é o ponto onde as configurações do banco
         * criado pelo Testcontainers entram oficialmente no
         * ambiente do Spring para que a aplicação consiga
         * utilizar esse banco.
         *
         * ==========================================================
         */
