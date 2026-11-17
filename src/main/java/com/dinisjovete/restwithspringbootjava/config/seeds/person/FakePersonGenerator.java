package com.dinisjovete.restwithspringbootjava.config.seeds.person;

import com.dinisjovete.restwithspringbootjava.data_model.entities.enums.PersonRole;
import com.dinisjovete.restwithspringbootjava.data_model.entities.person.v1.Person;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Random;

// ============================================================================
// GERADOR DETERMINÍSTICO DE PESSOAS DE TESTE
// ============================================================================
// Regra única: o usuário de número N é sempre o mesmo.
// Cada índice usa seu próprio Random (seed + índice), então o resultado não
// depende da ordem nem de quantas pessoas já existem no banco.
// Esta classe não acessa o banco: só monta objetos Person.
// ============================================================================
final class FakePersonGenerator {

    private static final String EMAIL_DOMAIN = "example.com"; // reservado para testes (RFC 2606)
    private static final int TWO_SURNAMES_PERCENT = 40;

    private final long seed;

    FakePersonGenerator(long seed) {
        this.seed = seed;
    }

    // Cria uma pessoa usando o índice e a senha já codificada.
    Person generate(int index, String encodedPassword) {
        /*
         * Cria um gerador aleatório usando o seed do objeto e o index recebido.
         * Com os mesmos valores, as escolhas aleatórias serão as mesmas.
         */
        Random random = new Random(seed + index);

        /*
         * random.nextBoolean() sorteia e retorna true ou false.
         * O resultado é guardado na variável female.
         * Nesta linha, ainda não é escolhido nenhum nome.
         */
        boolean female = random.nextBoolean();

        // Lista de primeiros nomes.
        List<String> nameList;

        if (female) {
            nameList = PersonSeedData.FEMALE_FIRST_NAMES;
        } else {
            nameList = PersonSeedData.MALE_FIRST_NAMES;
        }
        // essa função pick(random, nameList);
        // * Escolhe um primeiro nome aleatório da lista selecionada, nesse caso na lista nameList = PersonSeedData.FEMALE_FIRST_NAMES;
        String firstName = pick(random, nameList);
        //===============================================================================

        // * Escolhe o primeiro sobrenome da lista de sobrenomes.  nesse caso na lista PersonSeedData.LAS T_NAMES;
        String firstSurname = pick(random, PersonSeedData.LAST_NAMES); //definição do sobrenome
        // Por padrão, o sobrenome completo terá apenas o primeiro sobrenome.
        String lastName = firstSurname; //atribuição do sobrenome definido

        /*
         * sorteia um número de 0 a 99.
         * Se o resultado for menor que TWO_SURNAMES_PERCENT (por exemplo, 40),
         * a pessoa receberá um segundo sobrenome.
         */
        if (random.nextInt(100) < TWO_SURNAMES_PERCENT) {
            /*
             * Escolhe outro sobrenome, diferente do primeiro, e junta os dois.
             */
            lastName += " " + pickDifferent(
                    random, // 1ª, parâmetro: Gerador aleatório usado para escolher o sobrenome.
                    PersonSeedData.LAST_NAMES,  // 2ª, parâmetro Lista de sobrenomes disponíveis (values).
                    // 3ª, parâmetro  Sobrenome a excluir da escolha (excluded), para evitar repetição.
                    // não pode ser escolhido novamente( não e randomizado)
                    firstSurname
            );

            /*
            Método -> pickDifferent(
            recebe os seguintes parâmetros
            1ª Random random, -> aplica o random para gerar valores aleatório
            2ª List<String> values,
            3ª String excluded // esse valor será excluído da nova lista de sorteio para evitar repetição( firstSurname )
            )
          */
        }

        /*
         * Escolhe um endereço aleatório da lista de endereços.
         */
        String address = pick(random, PersonSeedData.ADDRESSES);

        /*
         * Cria e retorna um objeto Person com os dados escolhidos ou calculados.
         */
        return new Person(
                null, // O significado depende da definição da classe Person.
                firstName,
                lastName,
                CpfGenerator.fromIndex(index),
                buildEmail(firstName, firstSurname, index),
                encodedPassword,
                address,
                female ? "Female" : "Male",
                PersonRole.CLIENT
        );
    }

    // Ex.: "Inês" + "Araújo" + 7 -> "ines.araujo.007@example.com".
    // O índice no final ajuda a manter o e-mail único mesmo com nomes repetidos.
    private static String buildEmail(String firstName, String surname, int index) {
        return String.format(
                /*
                 * Locale.ROOT usa regras neutras de formatação, sem depender do idioma ou da região
                 * configurados no computador.
                 *
                 * O formato "%s.%s.%03d@%s" monta o texto com os valores recebidos:
                 * - o primeiro %s recebe firstName;
                 * - depois vem um ponto;
                 * - o segundo %s recebe surname;
                 * - %03d recebe index, formatado com pelo menos 3 dígitos e zeros à esquerda;
                 * - depois vem @;
                 * - o último %s recebe o domínio EMAIL_DOMAIN.
                 *
                 * Exemplo: com "ana", "silva", 7 e o domínio "example.com",
                 * o resultado será "ana.silva.007@example.com".
                 */
                Locale.ROOT,
                "%s.%s.%03d@%s",
                toAscii(firstName),
                toAscii(surname),
                index,
                EMAIL_DOMAIN
        );
    }

    // Remove acentos e deixa só letras minúsculas: "Inês" -> "ines".
    private static String toAscii(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z]", "");
    }

    /*
     * Escolhe um item aleatório da lista values e o retorna.
     *
     * random.nextInt(values.size()) gera um índice entre 0 e size - 1,
     * correspondente a uma posição válida da lista.
     *
     * Pré-condição: values não está vazia.
     */
    private static String pick(Random random, List<String> values) {
        return values.get(random.nextInt(values.size()));
    }

    /*
     * Escolhe e retorna um item aleatório de values que seja diferente de excluded.
     *
     * Primeiro, cria uma lista com os valores diferentes de excluded.
     * Depois, escolhe um item aleatório dessa lista.
     *
     * Pré-condição: values contém pelo menos um item diferente de excluded.
     * *
     * EXCLUDED
     * excluded é um parâmetro que representa o valor que o método deve excluir da escolha.
     * Neste caso, ele recebe firstSurname:
     * *
     * (values) é a lista de sobrenomes disponíveis.
     * (excluded) é o primeiro sobrenome já escolhido.
     * O método seleciona outro sobrenome, diferente do primeiro, para evitar repetição.
     *
     * Exemplo: se values contém ["Silva", "Santos", "Costa"] e excluded é "Santos",
     * a lista de alternativas fica ["Silva", "Costa"], e o método escolhe aleatoriamente entre esses dois.
     */
    private static String pickDifferent(
            Random random,
            List<String> values,
            String excluded // esse valor será excluído da nova lista de sorteio para evitar repetição
    ) {
        /*
         * Cria uma lista apenas com os valores diferentes de excluded.
         */
        List<String> alternatives = values.stream()
                //se o valor na lista(value) for diferente do valor a ser excluído (excluded),
                // ele é mantido na lista de alternativas
                .filter(value -> !value.equals(excluded))
                .toList(); // cria a lista somente com valores diferente do excluído

        /*
         * Escolhe e retorna um item aleatório dessa lista.
         */
        return alternatives.get(random.nextInt(alternatives.size()));
    }
}