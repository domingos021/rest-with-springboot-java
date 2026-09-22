package com.dinisjovete.restwithspringbootjava.math;
import com.dinisjovete.restwithspringbootjava.exception.UnsupportedMathOperationException;
import com.dinisjovete.restwithspringbootjava.request.converters.NumberConverter;

public class SimpleMath {

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SOMA (+)
     * ==========================================================
     */
    public Double sum(String numberOne, String numberTwo) {
        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return NumberConverter.convertToDouble(numberOne) + NumberConverter.convertToDouble(numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: MULTIPLICAÇÃO (×)
     * ==========================================================
     */
    public Double multiplication(String numberOne, String numberTwo) {
        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return NumberConverter.convertToDouble(numberOne) * NumberConverter.convertToDouble(numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SUBTRAÇÃO (-)
     * ==========================================================
     */
    public Double subtraction(String numberOne, String numberTwo) {
        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return NumberConverter.convertToDouble(numberOne) - NumberConverter.convertToDouble(numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: DIVISÃO (/)
     * ==========================================================
     */
    public Double division(String numberOne, String numberTwo) {
        if (!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return NumberConverter.convertToDouble(numberOne) / NumberConverter.convertToDouble(numberTwo);
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
        if (numbers == null || numbers.length == 0) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }

        double sum = 0;

        /*
         * string number -> variável do tipo string (os valores chegam como string, somente aqui ele é convertido em número)
         * para cada string number dentro de numbers (String[] numbers) ele vai fazer a validação
         * se é numérico e depois converter para double e somar na variável sum
         */
        for (String number : numbers) {
            if (!NumberConverter.isNumeric(number)) {
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            sum += NumberConverter.convertToDouble(number); // pega a string, converte em double, e incrementa na variável sum
        }

        // agora o sum tem números double / a quantidade de valores dentro do array numbers
        return sum / numbers.length;
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: RAIZ QUADRADA (√)
     * ==========================================================
     */
    public Double squareRoot(String number) {
        if (!NumberConverter.isNumeric(number)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }
        return Math.sqrt(NumberConverter.convertToDouble(number));
    }
}