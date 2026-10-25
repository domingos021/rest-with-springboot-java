package com.dinisjovete.restwithspringbootjava.config.seeds;

import com.dinisjovete.restwithspringbootjava.data_model.entities.book.Book;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import com.dinisjovete.restwithspringbootjava.repositories.book.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

// ============================================================================
// CONFIGURATION LAYER & DATABASE SEEDING
// ============================================================================
// Purpose:
// Executed at application startup to populate the local PostgreSQL database
// with mock book data for testing/development environments in an idempotent way.
//
// Lifecycle:
// 1. Spring Context starts up with active profile "test" or "dev".
// 2. BookTestDataConfig Bean is created via Constructor Injection.
// 3. CommandLineRunner.run() is executed automatically after context initialization,
//    AFTER the person seed (see @Order), because every book needs an author (Person).
// ============================================================================

/**
 * Spring configuration class dedicated to the test and development environment setup
 * and database seeding for Book entities.
 * <p>
 * Responsibilities:
 * - Configures settings specific to the "test" and "dev" Spring profiles.
 * - Executes idempotent database seeding via {@link CommandLineRunner} at application startup,
 *   checking individual records by title to prevent duplicates and ID inflation.
 * - Links each book to an author that was already seeded as a Person (found by id).
 */
@Configuration
@Profile({"test", "dev"})
/*
 * ============================================================================
 * @Order(2) - ORDEM DE EXECUÇÃO DOS SEEDS
 * ============================================================================
 * Quando existem vários CommandLineRunner, o Spring os executa em uma ordem
 * que, sem @Order, não é garantida. Quanto MAIOR o número, mais tarde roda.
 *
 * Este seed precisa rodar DEPOIS do de pessoas (@Order(2)) porque cada Book
 * possui um autor obrigatório (Person, coluna author_id não nula). O método
 * seedBook() busca o autor pelo id; se o PersonTestDataConfig (@Order(1))
 * ainda não tivesse rodado, o autor não existiria e o livro seria pulado.
 *
 *      1º PersonTestDataConfig  (@Order(1))  -> cria as pessoas
 *      2º BookTestDataConfig    (@Order(2))  -> cria os livros ligados às pessoas
 * ============================================================================
 */
@Order(2)
public class BookTestDataConfig implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(BookTestDataConfig.class.getName());

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    public BookTestDataConfig(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    // ========================================================================
    // STARTUP EXECUTION (CommandLineRunner)
    // ========================================================================

    @Override
    public void run(String... args) throws Exception {

        logger.info("Checking book seed data...");

        // Seed individual e idempotente por título:
        // Garante que os registros sejam criados apenas se ainda não existirem,
        // preservando a sequência de IDs do banco de dados (evita inflar os IDs a cada restart).
        //
        // O autor é buscado pelo ID da Person criada no seed de pessoas.
        // Os IDs seguem a ordem de criação do PersonTestDataConfig em banco vazio:
        //
        //      1 Ayrton Senna | 2 Nikola Tesla | 3 Ada Lovelace
        //      4 Albert Einstein | 5 Marie Curie | 6 Domingos Jovete
        //
        // ATENÇÃO: o ID é gerado pelo banco. Se o banco já tiver outras pessoas, ou se
        // a sequência tiver sido consumida, esses números mudam e o autor pode não ser
        // o esperado (ou não existir; nesse caso o livro é pulado com um aviso no log).

        seedBook(
                "Relativity: The Special and General Theory",
                new BigDecimal("79.90"),
                LocalDateTime.of(1916, 1, 1, 0, 0),
                4L // Albert Einstein
        );

        seedBook(
                "My Inventions",
                new BigDecimal("59.90"),
                LocalDateTime.of(1919, 1, 1, 0, 0),
                2L // Nikola Tesla
        );

        seedBook(
                "Treatise on Radioactivity",
                new BigDecimal("89.90"),
                LocalDateTime.of(1910, 1, 1, 0, 0),
                5L // Marie Curie
        );

        seedBook(
                "Notes on the Analytical Engine",
                new BigDecimal("69.90"),
                LocalDateTime.of(1843, 1, 1, 0, 0),
                3L // Ada Lovelace
        );

        seedBook(
                "REST com Spring Boot",
                new BigDecimal("99.90"),
                LocalDateTime.of(2026, 1, 1, 0, 0),
                6L // Domingos Jovete
        );

        // ====================================================================
        // TESTE DO RELACIONAMENTO OneToMany (Person) / ManyToOne (Book)
        // ====================================================================
        // Mais livros para a MESMA pessoa (mesmo authorId), para validar que
        // uma Person pode ter vários Books:
        //
        //      Einstein (4)  -> 3 livros
        //      Tesla (2)     -> 2 livros
        //      Curie (5)     -> 2 livros
        //      Ayrton (1)    -> 0 livros (de propósito: Person sem nenhum livro)

        seedBook(
                "The World as I See It",
                new BigDecimal("64.90"),
                LocalDateTime.of(1934, 1, 1, 0, 0),
                4L // Albert Einstein
        );

        seedBook(
                "The Evolution of Physics",
                new BigDecimal("74.90"),
                LocalDateTime.of(1938, 1, 1, 0, 0),
                4L // Albert Einstein
        );

        seedBook(
                "Experiments with Alternate Currents of High Potential and High Frequency",
                new BigDecimal("84.90"),
                LocalDateTime.of(1892, 1, 1, 0, 0),
                2L // Nikola Tesla
        );

        seedBook(
                "Pierre Curie",
                new BigDecimal("54.90"),
                LocalDateTime.of(1923, 1, 1, 0, 0),
                5L // Marie Curie
        );

        logger.info("Book seeding verification completed successfully!");

        // ====================================================================
        // DATABASE SEEDING COMPLETE
        // ====================================================================
    }

    // ========================================================================
    // HELPER: cria o livro apenas se o título ainda não existir e o autor existir
    // ========================================================================
    private void seedBook(String title, BigDecimal price, LocalDateTime launchDate, Long authorId) {

        if (bookRepository.existsByTitle(title)) {
            return;
        }

        // findById já vem pronto no JpaRepository e retorna Optional<Person>
        Person author = personRepository.findById(authorId).orElse(null);

        if (author == null) {
            logger.warning("Book not seeded (author id not found: " + authorId + "): " + title);
            return;
        }

        /*
        Forma com construtor (mais curta):
        bookRepository.save(new Book(null, title, price, launchDate, author));
         */

        // Forma explícita (mais legível): cada valor é atribuído pelo seu setter.
        // O id fica null: quem gera é o banco (GenerationType.IDENTITY).
        Book book = new Book();
        book.setTitle(title);
        book.setPrice(price);
        book.setLaunchDate(launchDate);
        book.setAuthor(author);

        bookRepository.save(book);
        logger.info("Seeded book: " + title);
    }
}