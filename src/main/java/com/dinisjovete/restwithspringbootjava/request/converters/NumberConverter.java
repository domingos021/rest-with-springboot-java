package com.dinisjovete.restwithspringbootjava.request.converters;
import com.dinisjovete.restwithspringbootjava.exception.UnsupportedMathOperationException;

public class NumberConverter {

    /*
     * MÉTODO AUXILIAR
     * Converte uma String recebida pela URL
     * para o tipo Double.
     *
     * Exemplo:
     *
     * "10" -> 10.0
     */
    public static Double convertToDouble(String strNumber) {
        /*
         * Recebe uma String enviada pela URL e converte
         * para o tipo Double.
         *
         * O return realiza a conversão da String
         * para um número decimal (Double).
         */

        // Verifica se a String é nula ou vazia.
        if (strNumber == null || strNumber.isEmpty()) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }

        // Permite números usando vírgula ou ponto.
        // Exemplo:
        // "10,5" -> "10.5"
        String number = strNumber.replace(",", ".");

        // Converte a String para Double.
        return Double.parseDouble(number);
    }

    /*
     * MÉTODO AUXILIAR
     * Verifica se uma String representa um número válido.
     *
     * Exemplos:
     *
     * "10"    -> true
     * "10.5"  -> true
     * "abc"   -> false
     */
    public static boolean isNumeric(String strNumber) {
        // Verifica se a String é nula ou vazia.
        if (strNumber == null || strNumber.isEmpty()) {
            return false;
        }

        // Permite números usando vírgula ou ponto.
        String number = strNumber.replace(",", ".");

        // Verifica se o valor possui formato numérico.
        return number.matches("[+-]?[0-9]*\\.?[0-9]+");
    }
}


/*
 * ==========================================================================
 * SOBRE O USO DA PALAVRA-CHAVE `static`: QUANDO USAR E QUANDO NÃO USAR
 * ==========================================================================
 *
 * 1. O CONCEITO BÁSICO:
 *    Normalmente, quando criamos uma classe, precisamos instanciar um objeto
 *    dela usando a palavra `new` (ex: `NumberConverter converter = new NumberConverter();`)
 *    para conseguir usar seus métodos. Cada objeto criado ocupa um espaço
 *    separado na memória RAM do computador.
 *
 * 2. O QUE O `static` MUDA?
 *    Quando tornamos um método `static`, ele passa a pertencer **diretamente à classe**
 *    e não a uma instância (objeto) específica.
 *
 * 3. QUANDO USAR:
 *    - **Classes Utilitárias (*Helpers*):** Funções matemáticas puras, formatadores de texto,
 *      validadores e conversores (como o `NumberConverter`).
 *    - **Independência de Estado:** Quando o método só precisa dos dados que recebeu nos
 *      parâmetros para funcionar, sem depender de atributos da classe.
 *    - **Vantagem:** Acesso direto pelo nome da classe (`NumberConverter.convertToDouble`)
 *      sem precisar dar `new`, economizando memória.
 *
 * 4. QUANDO NÃO USAR (⚠️ MUITO IMPORTANTE):
 *    - **Serviços do Spring (`@Service`):** Se a sua classe lida com regras de negócio complexas,
 *      precisa se conectar a um Banco de Dados (`Repository`), ou gerencia transações, **NUNCA**
 *      use `static`. O Spring precisa gerenciar o ciclo de vida dessas classes através de
 *      Injeção de Dependências (`@Autowired` ou construtor). Métodos estáticos quebram a
 *      injeção de dependência e dificultam muito a criação de testes unitários.
 *    - **Mutação de Estado:** Se o método precisa alterar ou salvar o valor de um atributo
 *      interno da classe, ele não deve ser estático (pois o `static` é compartilhado globalmente
 *      e pode causar graves problemas de concorrência — *thread-safety*).
 * ==========================================================================
 */