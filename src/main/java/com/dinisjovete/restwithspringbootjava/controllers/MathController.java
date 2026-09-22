package com.dinisjovete.restwithspringbootjava.controllers;
/*
 * controller chama o service para aplicar a logica dos dados enviados pelo usuario via endpoint
 * service -> pega esses dados e delega a função de calculo para a classe SimpleMath
 * SimpleMath -> pega esses dados e, antes de fazer o calculo, aciona NumberConverter.isNumeric()
 *              (se não for numérico, lança a exceção UnsupportedMathOperationException).
 *              Se for válido, aciona NumberConverter.convertToDouble() para converter a String em Double,
 *              faz o cálculo e retorna o resultado para o service.
 *
 * Apos isso:
 * service -> recebe o resultado do calculo e retorna para o controller
 * controller -> recebe o resultado do calculo e retorna para o usuário via endpoint
 */

/*
 * ==========================================================================
 * DIAGRAMA DE FLUXO: DA REQUISIÇÃO DO USUÁRIO AO CÁLCULO
 * ==========================================================================
 *
 *   [ 1. Usuário / Client ]
 *          │
 *          │ (Envia dados via Endpoint HTTP)
 *          ▼
 *   [ 2. MathController ]
 *          │
 *          │ (Chama o Service para aplicar a regra de negócio)
 *          ▼
 *   [ 3. MathService ]
 *          │
 *          │ (Delega os dados para a classe de lógica pura)
 *          ▼
 *   [ 4. SimpleMath ]
 *          │
 *          ├──────► Aciona NumberConverter.isNumeric()
 *          │        (Se não for número, lança UnsupportedMathOperationException)
 *          │
 *          ├──────► Aciona NumberConverter.convertToDouble()
 *          │        (Se passar na validação, converte String para Double)
 *          │
 *          │ (Executa o cálculo matemático)
 *          │
 *          │ (Retorna o resultado)
 *          ▼
 *   [ 5. MathService ]
 *          │
 *          │ (Recebe o resultado e repassa para o controller)
 *          ▼
 *   [ 6. MathController ]
 *          │
 *          │ (Retorna o resultado final via endpoint)
 *          ▼
 *   [ 7. Usuário / Client ]
 *
 * ==========================================================================
 */

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

import com.dinisjovete.restwithspringbootjava.services.MathService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * Injeção de Dependência
     *
     * O Spring injeta automaticamente uma instância do MathService
     * para que o Controller possa utilizar as regras de negócio.
     */
    @Autowired
    private MathService mathService;

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SOMA (+)
     * ==========================================================
     * GET http://localhost:8080/math/sum/10/5
     */
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
         * Delega a execução da lógica matemática
         * para a camada de serviço (MathService).
         */
        return mathService.sum(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: MULTIPLICAÇÃO (×)
     * ==========================================================
     * GET http://localhost:8080/math/mult/10/5
     */
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
         * Delega a execução da lógica matemática
         * para a camada de serviço (MathService).
         */
        return mathService.multiplication(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: SUBTRAÇÃO (-)
     * ==========================================================
     * GET http://localhost:8080/math/sub/10/5
     */
    @RequestMapping("/sub/{numberOne}/{numberTwo}")
    public Double sub(
            /*
             * @PathVariable
             *
             * Captura um valor que está dentro do caminho da URL.
             *
             * Exemplo:
             *
             * URL:
             * /math/sub/10/5
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
         * Delega a execução da lógica matemática
         * para a camada de serviço (MathService).
         */
        return mathService.subtraction(numberOne, numberTwo);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: DIVISÃO (/)
     * ==========================================================
     * GET http://localhost:8080/math/div/10/5
     */
    @RequestMapping("/div/{numberOne}/{numberTwo}")
    public Double div(
            /*
             * @PathVariable
             *
             * Captura um valor que está dentro do caminho da URL.
             *
             * Exemplo:
             *
             * URL:
             * /math/div/10/5
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
         * Delega a execução da lógica matemática
         * para a camada de serviço (MathService).
         */
        return mathService.division(numberOne, numberTwo); // envia para o service e recebe o calculo no retorno
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: MÉDIA (Com Varargs dinâmico)
     * ==========================================================
     * GET http://localhost:8080/math/media/10/5/15/20
     */
    @RequestMapping("/media/{*numbersPath}")
    public Double media(
            /*
             * @PathVariable
             *
             * Captura todo o restante da URL após /media/ como um texto único
             * e depois o ".split("/")" quebra os valores em um array de Strings,
             * alimentando perfeitamente o Varargs (String... numbers) do Service!
             */
            @PathVariable("numbersPath") String numbersPath
    ) {
        /*
         * O path vem no formato "/10/5/15/20".
         * O replace(/^\//, "") remove a primeira barra e o .split("/") divide em um array.
         * String array porque agora aceita quantidade infinita de numero para calculo da media
         * *
         * Exemplo:
         * URL: /math/media/10/5/15/20
         * numbersPath: "/10/5/15/20"
         * numbers: ["10", "5", "15", "20"]
         * o meto no service pega esses valores, acumula na variavel sum atravez do for each e depois sum / numbers.length para calcular a media
         */
        String[] numbers = numbersPath.replaceAll("^/+", "").split("/");

        /*
         * Delega o array dinâmico para a camada de serviço.
         * esse método no service recebe apenas (numbers) que e um array com todos os valores enviado pelo usuário
         *  o método media no service {
         *     *  double sum = 0;
         *     *  for (String number : numbers) {
         *     *      if (!isNumeric(number)) {
         *     *          throw new UnsupportedMathOperationException("Please set a numeric value!");
         *     *      }
         *     *      sum += convertToDouble(number); // incrementa os valores do |numbers-> array(que veio do controller)| na variável sum
         *     *  }
         *     *  return sum / numbers.length; // calcula a média dividindo a soma
         *
         */
        return mathService.media(numbers);
    }

    /*
     * ==========================================================
     * OPERAÇÃO MATEMÁTICA: RAIZ QUADRADA (√)
     * ==========================================================
     * GET http://localhost:8080/math/squareRoot/16
     */
    @RequestMapping("/squareRoot/{number}")
    public Double squareRoot(
            /*
             * @PathVariable
             *
             * Captura um valor que está dentro do caminho da URL.
             *
             * Exemplo:
             *
             * URL:
             * /math/squareRoot/16
             *
             * number recebe:
             * 16
             */
            @PathVariable("number") String number
    ) {
        /*
         * Delega a execução da lógica matemática
         * para a camada de serviço (MathService).
         */
        return mathService.squareRoot(number);
    }
}


/*
 * ==========================================================================
 * CONCEITO DE ARQUITETURA: FLUXO SÍNCRONO E BIDIRECIONAL (O "ELEVADOR")
 * ==========================================================================
 *
 * Pense no caminho das chamadas como uma viagem de elevador (ida e volta):
 *
 * 1. A IDA (Request / Requisição):
 *    - O pedido nasce no Cliente (Usuário).
 *    - Passa pelo Controller (Porta de entrada da API).
 *    - Desce para o Service (Gerencia a regra de negócio).
 *    - Chega ao SimpleMath e NumberConverter (Validação e cálculo puro).
 *
 * 2. A VOLTA (Response / Resposta):
 *    - O resultado faz o caminho inverso, subindo de volta.
 *    - Sobe do SimpleMath/NumberConverter para o Service.
 *    - Do Service, sobe para o Controller.
 *    - Do Controller, o resultado final é entregue ao Cliente.
 *
 * POR QUE ISSO ACONTECE?
 * Em arquitetura de software em camadas, chamamos essa estrutura de
 * *fluxo síncrono e bidirecional*.
 *
 * Regra de ouro: Cada camada que o seu código "atravessa" na ida para
 * executar uma tarefa, ela precisa obrigatoriamente atravessar de volta
 * para conseguir entregar o resultado obtido.
 * ==========================================================================
 */