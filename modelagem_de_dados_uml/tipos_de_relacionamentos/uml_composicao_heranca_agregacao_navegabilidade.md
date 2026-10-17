/*
================================================================================
        UML - COMPOSIÇÃO, HERANÇA, AGREGAÇÃO E NAVEGABILIDADE
        EXEMPLO: PIZZA, SABOR, INGREDIENTE E ESTOQUE
================================================================================


Neste exemplo temos vários tipos de relacionamento UML juntos:

- Composição (diamante preto ◆)
- Generalização / Herança (parent e child)
- Agregação (diamante branco ◇)
- Navegabilidade (seta →)
- Associação (linha de relacionamento)



================================================================================
1) COMPOSIÇÃO (DIAMANTE PRETO ◆)
   ================================================================================


Representação:


Pizza ◆──────── Sabor



Símbolo:

◆ = composição



A composição representa uma relação:

"É formado por"

ou

"Faz parte de"



A ideia principal:

A parte depende do todo para existir.



Leitura:


Pizza é composta por um Sabor.



Exemplo:


Uma pizza existe com um sabor associado.


Em Java:


*/


class Pizza {

    private Sabor sabor;

}


/*
Aqui temos:


Pizza ◆──── Sabor


A classe Pizza possui um Sabor.



================================================================================
DIFERENÇA ENTRE COMPOSIÇÃO E AGREGAÇÃO
================================================================================


AGREGAÇÃO:

Símbolo:

◇


Exemplo:


Estoque ◇──────── Produto


Ideia:


Produto pertence ao Estoque,
mas o Produto pode existir sozinho.



COMPOSIÇÃO:

Símbolo:

◆


Exemplo:


Pizza ◆──────── Sabor


Ideia:


O Sabor faz parte da Pizza
e depende dessa relação.



Resumo:


◇ = parte independente

◆ = parte dependente



================================================================================
2) GENERALIZAÇÃO / HERANÇA (PARENT E CHILD)
   ================================================================================


Quando temos:


        Sabor
          △
          |
     Ingrediente



Temos uma relação de:

GENERALIZAÇÃO


Também chamada de:

- Herança
- Is-a



A pergunta é:


"Ingrediente é um Sabor?"



Se a resposta for SIM:

Ingrediente herda de Sabor.



Em Java:


*/


class Sabor {

}


class Ingrediente extends Sabor {

}


/*
O conceito é:

Sabor = classe pai (parent)

Ingrediente = classe filha (child)



A seta da herança aponta para a classe mais geral.


Exemplo:


Pizza ─────▷ Produto


Significa:


Pizza é um Produto.



================================================================================
3) ASSOCIAÇÃO
   ================================================================================


Toda ligação entre classes representa uma associação.


Exemplo:


Ingrediente ---------------- Estoque



Significa:


Existe uma relação entre Ingrediente e Estoque.



Em Java:


*/


class IngredienteExemplo {

    private Estoque estoque;

}


class Estoque {


}


/*
Aqui existe uma associação.


================================================================================
4) AGREGAÇÃO DO INGREDIENTE COM ESTOQUE
   ================================================================================


Representação:


Ingrediente ◇────────→ Estoque



Elementos:


◇

Agregação



Linha:

Associação



→

Navegabilidade



Interpretação:


Ingrediente possui uma relação com Estoque
e consegue acessar Estoque.



Código:


*/


class IngredienteComEstoque {

    private Estoque estoque;

}


/*
A seta:


Ingrediente ───────→ Estoque


significa:


"Ingrediente conhece Estoque."



IMPORTANTE:

A seta não significa:


❌ Ingrediente vem do Estoque

❌ Estoque cria Ingrediente


Ela significa:


✅ Ingrediente possui uma referência para Estoque.



================================================================================
5) MULTIPLICIDADE
   ================================================================================


Exemplo:


Ingrediente ◇────────→ Estoque
0..n



O 0..n indica:


Mínimo: 0

Máximo: muitos



Como está do lado do Ingrediente:


Um Estoque pode possuir zero até muitos Ingredientes.



Exemplo:


Estoque


|
|---- Farinha
|
|---- Queijo
|
|---- Molho



================================================================================
6) JUNTANDO TUDO
   ================================================================================


Um possível modelo:


                    Produto
                       △
                       |
        --------------------------------
        |              |               |
     Pizza          Bebida        Sobremesa



Pizza ◆──────── Sabor



Sabor ───────▷ Ingrediente



Ingrediente ◇────────→ Estoque




================================================================================
7) LEITURA COMPLETA DO MODELO
   ================================================================================


Pizza:

"Pizza possui um Sabor através de composição."



Sabor:

"Pode possuir relação com Ingredientes."



Ingrediente:

"Está relacionado com Estoque através de agregação
e consegue navegar até Estoque."



Produto:

"É uma classe geral que possui tipos específicos."



================================================================================
8) RESUMO DOS SÍMBOLOS
   ================================================================================


ASSOCIAÇÃO:

Símbolo:

────────


Significado:

Existe uma relação entre classes.






AGREGAÇÃO:

Símbolo:

◇────────


Significado:

"Tem um"

Parte independente.






COMPOSIÇÃO:

Símbolo:

◆────────


Significado:

"É formado por"

Parte dependente.






GENERALIZAÇÃO / HERANÇA:

Símbolo:

△


Significado:

"É um"

Exemplo:


Pizza é um Produto.






NAVEGABILIDADE:

Símbolo:

→


Significado:

Indica quem conhece quem.






================================================================================
REGRA MENTAL PARA NÃO CONFUNDIR
================================================================================


Pergunte:


1) "É um?"

Exemplo:

Pizza é um Produto?

SIM

Usa HERANÇA.






2) "Tem um?"

Exemplo:

Estoque tem Produtos?

SIM

Usa ASSOCIAÇÃO/AGREGAÇÃO.






3) "Faz parte e depende?"

Exemplo:

Pizza tem Sabor?

SIM

Pode ser COMPOSIÇÃO.



================================================================================
*/