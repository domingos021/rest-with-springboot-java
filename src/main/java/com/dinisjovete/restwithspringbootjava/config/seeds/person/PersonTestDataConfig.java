package com.dinisjovete.restwithspringbootjava.config.seeds.person;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;
import com.dinisjovete.restwithspringbootjava.repositories.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

// ============================================================================
// CARGA DE DADOS DE TESTE (perfis "test" e "dev")
// ============================================================================
// Ordem 1: as pessoas existem antes de seeds que dependem delas (ex.: livros).
//
// Regras deste seed:
//  1. Idempotente: pode rodar quantas vezes quiser, sem duplicar ninguém.
//  2. Identidade = e-mail e CPF. Se qualquer um já existir, a pessoa é pulada.
//  3. Reproduzível: os usuários de paginação são sempre os mesmos (seed fixa).
// ============================================================================
@Configuration
@Profile({"test", "dev"})
@Order(1)
public class PersonTestDataConfig implements CommandLineRunner {

    private static final Logger logger =
            Logger.getLogger(PersonTestDataConfig.class.getName());

    // 537 não é múltiplo de 10, 20 nem 50: a última página sempre sai incompleta,
    // que é o caso de borda mais importante ao testar paginação.
    private static final int PAGINATION_USERS_COUNT = 537;
    private static final long RANDOM_SEED = 42L;

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final FakePersonGenerator generator = new FakePersonGenerator(RANDOM_SEED);

    public PersonTestDataConfig(
            PersonRepository personRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Checking database seed data...");

        String clientPassword = passwordEncoder.encode("123456");
        String adminPassword = passwordEncoder.encode("212985");

        UsedIdentities used = loadUsedIdentities();

        seedFixedUsers(used, clientPassword, adminPassword);
        seedPaginationUsers(used, clientPassword);

        logger.info("Database seeding completed successfully!");
    }

    // =========================================================================
    // USUÁRIOS FIXOS (contas conhecidas para desenvolvimento)
    // =========================================================================
    private void seedFixedUsers(UsedIdentities used, String clientPassword, String adminPassword) {
        List<Person> fixedUsers = List.of(
                new Person(null, "Ayrton", "Senna", "11122233344", "ayrton.senna@email.com",
                        clientPassword, "São Paulo - Brasil", "Male", PersonRole.CLIENT),
                new Person(null, "Nikola", "Tesla", "22233344455", "nikola.tesla@email.com",
                        clientPassword, "Smiljan - Croácia", "Male", PersonRole.CLIENT),
                new Person(null, "Ada", "Lovelace", "33344455666", "ada.lovelace@email.com",
                        clientPassword, "Londres - Inglaterra", "Female", PersonRole.CLIENT),
                new Person(null, "Albert", "Einstein", "44455566777", "albert.einstein@email.com",
                        clientPassword, "Ulm - Alemanha", "Male", PersonRole.CLIENT),
                new Person(null, "Marie", "Curie", "55566677888", "marie.curie@email.com",
                        clientPassword, "Varsóvia - Polônia", "Female", PersonRole.CLIENT),
                new Person(null, "Domingos", "Jovete", "700660020", "domingos.jovete@email.com",
                        adminPassword, "Angola - Luanda", "Male", PersonRole.ADMIN)
        );

        int created = saveNewPeople(fixedUsers, used);
        logger.info("Fixed users: " + created + " created, "
                + (fixedUsers.size() - created) + " skipped.");
    }

    // =========================================================================
    // USUÁRIOS DE PAGINAÇÃO (gerados, mas sempre os mesmos)
    // =========================================================================
    private void seedPaginationUsers(UsedIdentities used, String clientPassword) {
        List<Person> candidates = new ArrayList<>(PAGINATION_USERS_COUNT);

        for (int index = 1; index <= PAGINATION_USERS_COUNT; index++) {
            /*
             * GENERATOR: método da classe FakePersonGenerator.
             *
             * generate(int index, String encodedPassword):
             * - index: índice usado para gerar dados consistentes e únicos para o usuário.
             * - encodedPassword: senha codificada atribuída ao usuário.
             * - Retorna um objeto Person com os dados gerados.
             *
             * Enquanto index <= PAGINATION_USERS_COUNT (537), o loop gera um usuário
             * e o adiciona à lista candidates.
             *
             * O método recebe o índice e a senha codificada. A mesma senha é usada
             * para todos os usuários gerados pelo loop.
             */
            candidates.add(generator.generate(index, clientPassword));
        }

        int created = saveNewPeople(candidates, used);
        logger.info("Pagination users: " + created + " created, "
                + (PAGINATION_USERS_COUNT - created) + " already existed.");
    }

    // =========================================================================
    // REGRA CENTRAL: só salva quem ainda não existe (por e-mail e por CPF)
    // =========================================================================
    private int saveNewPeople(List<Person> candidates, UsedIdentities used) {
        List<Person> toSave = new ArrayList<>();

        // Percorre os candidatos e verifica se o e-mail ou CPF já está registrado em `used`.
        // Adiciona a `toSave` apenas os candidatos que passam pelas verificações;
        // depois, salva essa lista em lote no repositório.
        for (Person candidate : candidates) {
            if (used.hasEmail(candidate.getEmail())) {
                continue; // já existe: comportamento normal ao reiniciar
            }
            if (used.hasCpf(candidate.getCpf())) {
                logger.warning("Seed skipped, CPF already in use: " + candidate.getEmail());
                continue;
            }
            used.register(candidate.getEmail(), candidate.getCpf());
            toSave.add(candidate);
        }

        personRepository.saveAll(toSave);// salva a lista em lote no repositório
        return toSave.size();
    }

    // Lê o banco uma única vez e guarda e-mails e CPFs já ocupados.
    private UsedIdentities loadUsedIdentities() {
        UsedIdentities used = new UsedIdentities();
        personRepository.findAll().forEach(p -> used.register(p.getEmail(), p.getCpf()));
        return used;
    }

    // Guarda as identidades já ocupadas (no banco ou neste mesmo seed).
    private static final class UsedIdentities {
        private final Set<String> emails = new HashSet<>();
        private final Set<String> cpfs = new HashSet<>();

        boolean hasEmail(String email) {
            return emails.contains(email.toLowerCase(Locale.ROOT));
        }

        boolean hasCpf(String cpf) {
            return cpfs.contains(cpf);
        }

        void register(String email, String cpf) {
            emails.add(email.toLowerCase(Locale.ROOT));
            cpfs.add(cpf);
        }
    }
}