package com.dinisjovete.restwithspringbootjava.controllers;


/*
 * Path Parameter (Path Param)
 *
 * Recebe um valor diretamente pelo caminho (path) da URL.
 * É usado geralmente para identificar um recurso específico
 * ou enviar valores obrigatórios para uma operação.
 *
 * Exemplo:
 *
 * URL:
 * http://localhost:8080/users/10
 *
 * Nesse caso:
 *
 * /users -> recurso
 * /10    -> Path Parameter (id do usuário)
 */


import com.dinisjovete.restwithspringbootjava.exception.UnsupportedMathOperationException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
 * @RestController
 *
 * Define a classe como um Controller REST.
 * Permite que os métodos retornem diretamente os dados
 * da resposta para o cliente (normalmente em formato JSON).
 *
 *
 * @RequestMapping aplicado na classe
 *
 * Define um caminho base (prefixo) para todos os endpoints
 * existentes dentro desta classe.
 *
 * Exemplo:
 *
 * Classe:
 * @RequestMapping("/math")
 *
 * Método:
 * @RequestMapping("/sum/{numberOne}/{numberTwo}")
 *
 * URL final:
 *
 * http://localhost:8080/math/sum/10/5
 *
 * Neste caso:
 *
 * /math                    -> prefixo definido na classe
 * /sum                     -> caminho definido no método
 * /{numberOne}/{numberTwo} -> valores recebidos pelo Path Param
 *
 */
@RestController
@RequestMapping("/math")
public class MathController {


    /*
     * Variáveis utilizadas como exemplo.
     *
     * Observação:
     * Neste caso elas não são necessárias, pois os valores
     * são recebidos diretamente através do @PathVariable.
     */
    String numberOne = "0";
    String numberTwo = "0";


    /*
     * @RequestMapping aplicado no método
     *
     * Define o endpoint responsável pela operação de soma.
     *
     * Exemplo de requisição:
     *
     * http://localhost:8080/math/sum/10/5
     *
     * O Spring captura os valores:
     *
     * {numberOne} = 10
     * {numberTwo} = 5
     *
     * e envia esses valores para os parâmetros do método
     * através do @PathVariable.
     */
    @RequestMapping("/sum/{numberOne}/{numberTwo}")
    public Double sum(

            /*
             * @PathVariable
             *
             * Captura um valor que está dentro do caminho da URL.
             *
             * Exemplo:
             *
             * URL:
             * /math/sum/10/5
             *
             * numberOne recebe:
             * 10
             *
             * numberTwo recebe:
             * 5
             */
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo

    ) {

        /*
         * Validação dos valores recebidos.
         *
         * Caso algum valor não seja numérico,
         * uma exceção será lançada.
         */
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }


        /*
         * Realiza a soma após converter
         * os valores String para Double.
         */
        return convertToDouble(numberOne) + convertToDouble(numberTwo);
    }

    @RequestMapping("/mult/{numberOne}/{numberTwo}")
    public Double mult(

            /*
             * @PathVariable
             *
             * Captura um valor que está dentro do caminho da URL.
             *
             * Exemplo:
             *
             * URL:
             * /math/mult/10/5
             *
             * numberOne recebe:
             * 10
             *
             * numberTwo recebe:
             * 5
             */
            @PathVariable("numberOne") String numberOne,
            @PathVariable("numberTwo") String numberTwo

    ) {

        /*
         * Validação dos valores recebidos.
         *
         * Caso algum valor não seja numérico,
         * uma exceção será lançada.
         */
        if (!isNumeric(numberOne) || !isNumeric(numberTwo)) {
            throw new UnsupportedMathOperationException("Please set a numeric value!");
        }


        /*
         * Realiza a multiplicação após converter
         * os valores String para Double.
         */
        return convertToDouble(numberOne) * convertToDouble(numberTwo);
    }


    // Métodos auxiliares (helper methods)
    //
    // Responsáveis por realizar conversões e validações
    // utilizadas pelo método principal.


    /*
    MÉTODO AUXILIAR
     * Converte uma String recebida pela URL
     * para o tipo Double.
     *
     * Exemplo:
     *
     * "10" -> 10.0
     */
    private Double convertToDouble(String strNumber) {

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
     MÉTODO AUXILIAR
     * Verifica se uma String representa um número válido.
     *
     * Exemplos:
     *
     * "10"    -> true
     * "10.5"  -> true
     * "abc"   -> false
     */
    private boolean isNumeric(String strNumber) {

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
    Cliente
      |
      | GET /math/sum/abc/5
      |
      ↓
    MathController

    @PathVariable recebe:
    numberOne = "abc"
    numberTwo = "5"

      |
      ↓

    isNumeric("abc")
    retorna false

      |
      ↓

    lança:

    UnsupportedMathOperationException

      |
      ↓

    @ControllerAdvice captura

      |
      ↓

    Retorna resposta HTTP 400
    BAD_REQUEST
     */