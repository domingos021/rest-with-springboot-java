package com.dinisjovete.restwithspringbootjava.config.seeds;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.logging.Logger;

// ============================================================================
// CONFIGURATION LAYER & DATABASE SEEDING
// ============================================================================
// Purpose:
// Executed at application startup to populate the local PostgreSQL database
// with mock data for testing/development environments in an idempotent way.
//
// Lifecycle:
// 1. Spring Context starts up with active profile "test" or "dev".
// 2. PersonTestDataConfig Bean is created via Constructor Injection.
// 3. CommandLineRunner.run() is executed automatically after context initialization.
// ============================================================================

/**
 * Spring configuration class dedicated to the test and development environment setup
 * and database seeding for Person entities.
 * <p>
 * Responsibilities:
 * - Configures settings specific to the "test" and "dev" Spring profiles.
 * - Executes idempotent database seeding via {@link CommandLineRunner} at application startup,
 *   checking individual records by unique constraints (e.g., e-mail) to prevent ID inflation.
 */
@Configuration
@Profile({"test", "dev"})
/*
 * ============================================================================
 * @Order(1) - ORDEM DE EXECUÇÃO DOS SEEDS
 * ============================================================================
 * Quando existem vários CommandLineRunner, o Spring os executa em uma ordem
 * que, sem @Order, não é garantida. Quanto MENOR o número, mais cedo roda.
 *
 * Este seed precisa rodar PRIMEIRO (@Order(1)) porque cada Book possui um autor
 * obrigatório (Person, coluna author_id não nula). O BookTestDataConfig
 * (@Order(2)) busca esses autores pelo e-mail, então eles já precisam existir.
 *
 *      1º PersonTestDataConfig  (@Order(1))  -> cria as pessoas
 *      2º BookTestDataConfig    (@Order(2))  -> cria os livros ligados às pessoas
 * ============================================================================
 */
@Order(1)
public class PersonTestDataConfig implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(PersonTestDataConfig.class.getName());

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonTestDataConfig(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========================================================================
    // STARTUP EXECUTION (CommandLineRunner)
    // ========================================================================

    @Override
    public void run(String... args) throws Exception {

        logger.info("Checking database seed data...");

        // Definindo uma senha padrão para os testes (ex: "123456" criptografada)
        String defaultEncodedPassword = passwordEncoder.encode("123456");
        String defaultEncodedPasswordAdmin = passwordEncoder.encode("212985");

        // Seed individual e idempotente por e-mail:
        // Garante que os registros sejam criados apenas se ainda não existirem,
        // preservando a sequência de IDs do banco de dados (evita inflar os IDs a cada restart).

        if (personRepository.findByEmail("ayrton.senna@email.com").isEmpty()) {
            Person p1 = new Person(
                    null,
                    "Ayrton",
                    "Senna",
                    "11122233344", // CPF exigido pela entidade
                    "ayrton.senna@email.com", // E-mail exigido (unique)
                    defaultEncodedPassword, // Senha criptografada com BCrypt
                    "São Paulo - Brasil",
                    "Male",
                    PersonRole.CLIENT
            );
            personRepository.save(p1);
            logger.info("Seeded user: Ayrton Senna");
        }

        if (personRepository.findByEmail("nikola.tesla@email.com").isEmpty()) {
            Person p2 = new Person(
                    null,
                    "Nikola",
                    "Tesla",
                    "22233344455",
                    "nikola.tesla@email.com",
                    defaultEncodedPassword,
                    "Smiljan - Croácia",
                    "Male",
                    PersonRole.CLIENT
            );
            personRepository.save(p2);
            logger.info("Seeded user: Nikola Tesla");
        }

        if (personRepository.findByEmail("ada.lovelace@email.com").isEmpty()) {
            Person p3 = new Person(
                    null,
                    "Ada",
                    "Lovelace",
                    "33344455666",
                    "ada.lovelace@email.com",
                    defaultEncodedPassword,
                    "Londres - Inglaterra",
                    "Female",
                    PersonRole.CLIENT
            );
            personRepository.save(p3);
            logger.info("Seeded user: Ada Lovelace");
        }

        if (personRepository.findByEmail("albert.einstein@email.com").isEmpty()) {
            Person p4 = new Person(
                    null,
                    "Albert",
                    "Einstein",
                    "44455566777",
                    "albert.einstein@email.com",
                    defaultEncodedPassword,
                    "Ulm - Alemanha",
                    "Male",
                    PersonRole.CLIENT
            );
            personRepository.save(p4);
            logger.info("Seeded user: Albert Einstein");
        }

        if (personRepository.findByEmail("marie.curie@email.com").isEmpty()) {
            Person p5 = new Person(
                    null,
                    "Marie",
                    "Curie",
                    "55566677888",
                    "marie.curie@email.com",
                    defaultEncodedPassword,
                    "Varsóvia - Polônia",
                    "Female",
                    PersonRole.CLIENT
            );
            personRepository.save(p5);
            logger.info("Seeded user: Marie Curie");
        }

        if (personRepository.findByEmail("domingos.jovete@email.com").isEmpty()) {
            Person p6 = new Person(
                    null,
                    "Domingos",
                    "Jovete",
                    "700660020",
                    "domingos.jovete@email.com",
                    defaultEncodedPasswordAdmin,
                    "Angola - Luanda",
                    "Male",
                    PersonRole.ADMIN
            );
            personRepository.save(p6);
            logger.info("Seeded user: Domingos Jovete");
        }

        logger.info("Database seeding verification completed successfully!");

        // ====================================================================
        // DATABASE SEEDING COMPLETE
        // ====================================================================
    }
}