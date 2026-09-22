package com.dinisjovete.restwithspringbootjava.services;
import com.dinisjovete.restwithspringbootjava.math.SimpleMath;
import org.springframework.stereotype.Service;

/*
 * @Service
 *
 * Anotação do Spring que define esta classe como um Componente de Serviço.
 * Ela armazena a regra de negócio da aplicação e pode ser injetada
 * em outras classes (como nos Controllers).
 */
@Service
public class MathService {

    /*
     *simpleMath -> variável que guarda a referência do object (|new SimpleMath()|)
     * essa class e responsável pelos calculos, porem para a conversão de dados recebidos pelo controller
     * que vem como uma  string, ele delega essa função a NumberConverter
     * que que valida para saber se um numero, se for converte para Double, se não laça uma exceção
     */

    // Instância da classe responsável pelos cálculos matemáticos puros
    private final SimpleMath simpleMath = new SimpleMath();


    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SOMA (+)
     * ==========================================================
     */
    public Double sum(String numberOne, String numberTwo) {
        /*
         * Delega a execução da lógica matemática
         * para a classe SimpleMath.
         */
        return simpleMath.sum(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: MULTIPLICAÇÃO (×)
     * ==========================================================
     */
    public Double multiplication(String numberOne, String numberTwo) {
        return simpleMath.multiplication(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SUBTRAÇÃO (-)
     * ==========================================================
     */
    public Double subtraction(String numberOne, String numberTwo) {
        return simpleMath.subtraction(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: DIVISÃO (/)
     * ==========================================================
     */
    public Double division(String numberOne, String numberTwo) {
        return simpleMath.division(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: MÉDIA
     * ==========================================================
     */
    /*
     * ==========================================================
     * CONCEITO: VARARGS (Variable Arguments) -> String... numbers
     * ==========================================================
     *
     * O uso de reticências (...) antes do nome do parâmetro
     * indica um "Varargs" (Argumentos Variáveis).
     *
     * O que ele faz?
     * Permite que o método receba zero, um ou vários valores
     * do mesmo tipo (neste caso, String) sem a necessidade
     * de declarar parâmetros fixos um por um.
     *
     * Por baixo dos panos:
     * O Java transforma automaticamente esses múltiplos valores
     * enviados pela URL em um **Array** (String[]).
     *
     * Vantagem:
     * Traz flexibilidade total, permitindo calcular a média
     * de 2, 3, 10 ou quantos números o cliente enviar,
     * utilizando a propriedade `.length` para saber
     * exatamente quantos elementos foram recebidos.
     * *
     * *(numbers) representa o array com todos os valores enviados pelo usuário
     */
    public Double media(String... numbers) {
        return simpleMath.media(numbers);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: RAIZ QUADRADA (√)
     * ==========================================================
     */
    public Double squareRoot(String number) {
        return simpleMath.squareRoot(number);
    }
}