package com.dinisjovete.restwithspringbootjava.config.seeds.person;

// ============================================================================
// GERADOR DE CPF VÁLIDO PARA DADOS DE TESTE
// ============================================================================
// O CPF é calculado a partir do índice do usuário, sem sorteio. Assim:
//  - o mesmo índice sempre gera o mesmo CPF (seed reproduzível);
//  - índices diferentes nunca geram o mesmo CPF (unicidade por construção);
//  - os dois últimos dígitos são verificadores reais, então passam em @CPF.
// ============================================================================
final class CpfGenerator {

    // Faixa de 9 dígitos usada como base: de 100.000.000 a 899.999.999.
    private static final long BASE_MIN = 100_000_000L;
    private static final long BASE_RANGE = 800_000_000L;

    // Passo ímpar e sem fator 5, logo coprimo com BASE_RANGE (2^11 * 5^8).
    // Isso garante que (index * STEP) % BASE_RANGE nunca repete para índices
    // menores que BASE_RANGE e espalha os valores, evitando CPFs sequenciais.
    private static final long STEP = 7_919_173L;

    private CpfGenerator() {
    }

    static String fromIndex(int index) {
        /*
         * Calcula o valor de base usando o índice e as constantes:
         * - Multiplica index por STEP.
         * - Aplica % BASE_RANGE para manter o resultado dentro do intervalo definido.
         * - Soma BASE_MIN ao resultado.
         *
         * Exemplo, com BASE_MIN = 100_000_000, STEP = 7_919_173,
         * BASE_RANGE = 800_000_000 e index = 2:
         * - index * STEP = 2 * 7_919_173 = 15_838_346
         * * ==============================================================================
         * * Perguntamos: quantas vezes 800_000_000 cabe em 15_838_346 sem ultrapassar esse valor?
         * * - 800_000_000 * 0 = 0, que é o maior múltiplo de 800_000_000 menor ou igual a 15_838_346.
         * * - 800_000_000 * 1 = 800_000_000, que é maior que 15_838_346, então não serve.
         * * * * Portanto, o resultado de 15_838_346 % 800_000_000 é 15_838_346, pois 800_000_000 não cabe nenhuma vez em 15_838_346.
         * *
         * - 15_838_346 % 800_000_000 = 15_838_346
         * *==========================================================================
         *
         * ** - Finalmente, somamos BASE_MIN: 100_000_000 + 15_838_346 = 115_838_346, que é o valor final de base.
         * - BASE_MIN + 15_838_346 = 115_838_346
         *
         * Portanto, base recebe 115_838_346 nesse exemplo.
         */
        long base = BASE_MIN + (index * STEP) % BASE_RANGE;
        String nineDigits = String.valueOf(base);

        // Cria um array de inteiros com 11 posições, uma para cada dígito do CPF.
        int[] digits = new int[11];

        /*
        1ª transformar essa STRING (String nineDigits = String.valueOf(base);) em um array de inteiros (int[] digits = new int[11];)
        3ª construir a string final do CPF a partir do array digits e retorná-la.
         * A linha digits[i] = nineDigits.charAt(i) - '0';
         * lê o caractere da string "115838346" na posição i,
         * converte o algarismo para um número inteiro
         * e guarda esse número na posição i do array digits.
         *
         * Exemplo, com nineDigits = "115838346" e digits = new int[11]:
         * 1ª volta (i = 0): lê '1' e guarda 1 em digits[0] -> digits = [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
         * 2ª volta (i = 1): lê '1' e guarda 1 em digits[1] -> digits = [1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0]
         * 3ª volta (i = 2): lê '5' e guarda 5 em digits[2] -> digits = [1, 1, 5, 0, 0, 0, 0, 0, 0, 0, 0]
         * 4ª volta (i = 3): lê '8' e guarda 8 em digits[3] -> digits = [1, 1, 5, 8, 0, 0, 0, 0, 0, 0, 0]
         * 5ª volta (i = 4): lê '3' e guarda 3 em digits[4] -> digits = [1, 1, 5, 8, 3, 0, 0, 0, 0, 0, 0]
         * 6ª volta (i = 5): lê '8' e guarda 8 em digits[5] -> digits = [1, 1, 5, 8, 3, 8, 0, 0, 0, 0, 0]
         * 7ª volta (i = 6): lê '3' e guarda 3 em digits[6] -> digits = [1, 1, 5, 8, 3, 8, 3, 0, 0, 0, 0]
         * 8ª volta (i = 7): lê '4' e guarda 4 em digits[7] -> digits = [1, 1, 5, 8, 3, 8, 3, 4, 0, 0, 0]
         * 9ª volta (i = 8): lê '6' e guarda 6 em digits[8] -> digits = [1, 1, 5, 8, 3, 8, 3, 4, 6, 0, 0]
         *
         * Quando i chega a 9, a condição i < 9 é falsa e o loop termina.
         * ou seja o loop vai de 0 a 8 porque 8 e menor que 9 então da false, o loop percorre 9 posições de 0 a 8
         * a posição 9 esta livre
         * Resultado final: [1, 1, 5, 8, 3, 8, 3, 4, 6, 0, 0]
         */
        for (int i = 0; i < 9; i++) {
            digits[i] = nineDigits.charAt(i) - '0';
        }


        /*
         * 2ª etapa: calcular os dois dígitos verificadores do CPF
         * e armazená-los nas posições 9 e 10 do array digits.
         *
         * Esta linha calcula o primeiro dígito verificador e guarda
         * o resultado na posição 9 do array digits.
         *
         * Ela chama o método checkDigit, passando:
         * - digits: o array com os 9 algarismos da base nas posições 0 a 8;
         * - 9: indica que o cálculo deve usar essas 9 posições.
         *
         * O método devolve um número de 0 a 9, que é guardado em digits[9].
         */
        digits[9] = checkDigit(digits, 9);

        /*
         * Essa linha calcula o segundo dígito verificador do CPF
         * e guarda o resultado na posição 10 do array digits.
         *
         * Ela chama o método checkDigit, passando:
         * - digits: o array com os 9 algarismos da base nas posições 0 a 8
         *   e o primeiro dígito verificador na posição 9;
         * - 10: informa que o cálculo deve usar as posições 0 a 9.
         *
         * O método calcula o dígito verificador e devolve um número de 0 a 9.
         * Esse número é guardado em digits[10].
         */
        digits[10] = checkDigit(digits, 10);

        StringBuilder cpf = new StringBuilder(11);
        for (int digit : digits) {
            cpf.append(digit);
        }
        return cpf.toString();
    }

    // Dígito verificador: soma ponderada (pesos decrescentes) e módulo 11.
    /*
     * O método recebe:
     * - digits: um array de números inteiros;
     * - length: quantas posições do array serão usadas no cálculo.
     */
    private static int checkDigit(int[] digits, int length) {
        // Criamos a variável acumuladora.
        int sum = 0;

        for (int i = 0; i < length; i++) {
            /*
             * length = 9: serão usadas as posições 0 a 8.
             *
             * Exemplo, na 1ª volta do loop (i = 0):
             * - digits[0] = 1 (primeiro dígito da base)
             * - digits = [1, 1, 5, 8, 3, 8, 3, 4, 6, 0, 0]
             * ====================================
             * - peso = length + 1 - i
             * - peso = 9 + 1 - 0
             * - peso = 10
             * ================================
             * - sum = sum + digits[0] * peso
             * - sum = 0 + 1 * 10 = 10
             */

            sum += digits[i] * (length + 1 - i);
        }

        /*
         * O número 11 faz parte da regra de cálculo dos dígitos verificadores do CPF.
         * Ele não vem do tamanho do array digits.
         *
         * Exemplo: se sum = 180:
         * - Multiplicamos a soma por 10: 180 * 10 = 1800.
         * - Calculamos o resto da divisão por 11: 1800 % 11 = 7.
         * - Como o resto é 7, o dígito verificador é 7.
         *
         * Se o resto fosse 10, a regra do CPF define que o dígito verificador é 0.
         */

        /*
         * Exemplo, com digits = [1, 1, 5, 8, 3, 8, 3, 4, 6] e length = 9:
         * - sum = 1 * 10 + 1 * 9 + 5 * 8 + 8 * 7 + 3 * 6
         *        + 8 * 5 + 3 * 4 + 4 * 3 + 6 * 2
         * - sum = 180
         * - result = (180 * 10) % 11
         * - result = 1800 % 11
         * - result = 7
         */
        int result = (sum * 10) % 11;

        /*
         * Se result for 10, retorna 0.
         * Caso contrário, retorna o próprio result.
         */
        return result == 10 ? 0 : result;
    }
}
