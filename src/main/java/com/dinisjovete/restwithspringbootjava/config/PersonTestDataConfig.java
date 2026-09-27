package com.dinisjovete.restwithspringbootjava.config;

import com.dinisjovete.restwithspringbootjava.data_model.entities.Person;
import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.logging.Logger;

// ============================================================================
// CONFIGURATION LAYER & DATABASE SEEDING
// ============================================================================
// Purpose:
// Executed at application startup to populate the local PostgreSQL database
// with mock data for testing/development environments.
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
 * - Executes database seeding via {@link CommandLineRunner} at application startup.
 * - Clears and re-inserts mock data on startup to reflect code or schema updates.
 */
@Configuration
@Profile({"test", "dev"})
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

        logger.info("Cleaning database to ensure seed data is updated...");
        // Remove registros antigos para garantir que alterações no código ou novas colunas
        // sejam refletidas imediatamente ao reiniciar a aplicação no ambiente dev/test.
        personRepository.deleteAll();

        logger.info("Initializing database with updated seed data (with BCrypt passwords)...");

        // Definindo uma senha padrão para os testes (ex: "123456" criptografada)
        String defaultEncodedPassword = passwordEncoder.encode("123456");

        Person p1 = new Person(
                null,
                "Ayrton",
                "Senna",
                "11122233344", // CPF exigido pela entidade
                "ayrton.senna@email.com", // E-mail exigido (unique)
                defaultEncodedPassword, // Senha criptografada com BCrypt
                "São Paulo - Brasil",
                "Male",
                PersonRole.ADMIN
        );

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

        // Saving mock persons into the database
        personRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        logger.info("Database seeding completed successfully with encrypted passwords!");

        // ====================================================================
        // DATABASE SEEDING COMPLETE
        // ====================================================================
    }
}