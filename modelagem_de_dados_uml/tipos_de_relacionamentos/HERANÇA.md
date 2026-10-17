/*
================================================================================
        UML - GENERALIZAÇÃO / HERANÇA
        BEBIDA, PIZZA E SOBREMESA COMO PRODUTOS
================================================================================


Cenário:

Temos três classes:

- Bebida
- Pizza
- Sobremesa


Todas possuem características em comum e representam tipos de Produto.


Representação UML:


                 Produto
                    ▲
                    |
        ---------------------------
        |             |           |
     Bebida         Pizza     Sobremesa



================================================================================
1) TIPO DE RELACIONAMENTO
================================================================================


Neste caso o relacionamento é:

GENERALIZAÇÃO


Também conhecido como:

- Herança
- Relacionamento "é um" (is-a)


Exemplo:


Bebida é um Produto.

Pizza é um Produto.

Sobremesa é um Produto.



================================================================================
2) SIGNIFICADO DA SETA
================================================================================


Na UML, a seta com triângulo vazio:


        △


aponta para a classe mais geral.


Exemplo:


Bebida ─────────▷ Produto


Significa:


"Bebida herda de Produto."


Ou seja:

Produto é a classe pai.

Bebida é uma classe filha.



IMPORTANTE:

A seta não significa associação.

Ela significa:

HERANÇA / GENERALIZAÇÃO.



================================================================================
3) DIFERENÇA ENTRE ASSOCIAÇÃO E GENERALIZAÇÃO
================================================================================


ASSOCIAÇÃO:


Cliente ───────── Pedido


Leitura:

"Cliente possui relação com Pedido."


É uma relação:

TEM UM (has-a)



--------------------------------



GENERALIZAÇÃO:


Bebida ─────────▷ Produto


Leitura:

"Bebida é um Produto."


É uma relação:

É UM (is-a)



================================================================================
4) EXEMPLO EM JAVA
================================================================================
*/


class Produto {

    protected String nome;
    protected double preco;

}



class Bebida extends Produto {

    private String tamanho;

}



class Pizza extends Produto {

    private String sabor;

}



class Sobremesa extends Produto {

    private boolean gelada;

}



/*
================================================================================
EXPLICANDO O CÓDIGO
================================================================================


A classe:

Produto


é a classe geral.


Ela possui características comuns:


nome

preço



As classes:


Bebida

Pizza

Sobremesa


herdam essas características.



Por exemplo:


*/


Pizza pizza = new Pizza();

pizza.nome = "Pizza Calabresa";
pizza.preco = 45.90;


/*
A Pizza possui nome e preço porque herdou de Produto.



================================================================================
5) POLIMORFISMO
================================================================================


Como todos são Produtos:


*/


Produto produto1 = new Pizza();

Produto produto2 = new Bebida();

Produto produto3 = new Sobremesa;


/*
Isso é polimorfismo.


A variável é do tipo Produto,
mas o objeto real pode ser:


Pizza

Bebida

Sobremesa



================================================================================
6) COMO IDENTIFICAR NO DIAGRAMA
================================================================================


Quando aparecer:


        Produto
           △
           |
     -------------
     |     |     |
 Pizza Bebida Sobremesa



Procure:


- Triângulo vazio apontando para cima
        =
        Generalização


- Classe superior
        =
        Classe pai / superclasse


- Classes inferiores
        =
        Classes filhas / subclasses



================================================================================
7) COMPARANDO COM O ESTOQUE
================================================================================


CASO 1:


Produto ◇────────→ Estoque


Tipo:

Associação de agregação


Ideia:

"Produto possui relação com Estoque."



CASO 2:


Bebida ───────▷ Produto


Tipo:

Generalização / Herança


Ideia:

"Bebida é um Produto."



================================================================================
RESUMO FINAL
================================================================================


Bebida, Pizza e Sobremesa ligados a Produto:


Tipo de relacionamento:

GENERALIZAÇÃO (HERANÇA)


Representa:

"is-a"


Exemplo:

Pizza é um Produto.


Seta:

△ aponta para a classe mais geral.


Código Java:


class Pizza extends Produto


class Bebida extends Produto


class Sobremesa extends Produto(filho)


ex:
produdo e o pai
class Produto {

       String nome;
       double preco;

   }
================================================================================
*/