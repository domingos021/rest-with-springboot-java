/*
================================================================================
        UML - RELACIONAMENTO ENTRE ESTOQUE E PRODUTOS
================================================================================


Cenário:

Temos um sistema de uma pizzaria/restaurante.


Os produtos armazenados são:

- Sobremesa
- Bebida
- Ingrediente


A regra de negócio explicada é:


"Dentro do estoque existem sobremesas, bebidas e ingredientes."



Portanto, o conceito principal é:


ESTOQUE contém PRODUTOS.



================================================================================
1) RELAÇÃO / RELACIONAMENTO
   ================================================================================


O conceito mais geral é:

RELACIONAMENTO


Um relacionamento representa que duas classes possuem uma ligação.


Exemplo:


Estoque ---------------- Produto


Significa:


"Existe uma ligação entre Estoque e Produto."



Na UML, essa ligação é representada por uma associação.



================================================================================
2) ASSOCIAÇÃO
   ================================================================================


Associação é o relacionamento entre duas classes.


Exemplo:


Estoque ---------------- Produto


A associação representa:


"Uma classe conhece ou possui relação com outra classe."


No mundo real:


Um estoque possui produtos.



Exemplo em Java:


*/


class Estoque {

    private Produto produto;

}


class Produto {


}


/*
Aqui existe uma associação:

Estoque está associado a Produto.



================================================================================
3) AGREGAÇÃO (LOSANGO BRANCO ◇)
   ================================================================================


O símbolo:


◇


representa uma agregação.



Agregação é um tipo especial de associação.


Ela representa:


"Um objeto possui outro objeto como parte,
mas a parte pode existir independentemente."



Exemplo:


Estoque ◇──────── Produto



Leitura:


"Estoque possui Produtos."



No mundo real:


Um produto pode existir mesmo antes de entrar no estoque.



================================================================================
4) MODELO UML DO DOMÍNIO
   ================================================================================


A representação mais coerente para este cenário é:


                 0..n
Estoque ◇────────────────→ Sobremesa



                 0..n
Estoque ◇────────────────→ Bebida



                 0..n
Estoque ◇────────────────→ Ingrediente



Leitura:


Um Estoque pode possuir:

- zero ou muitas sobremesas;
- zero ou muitas bebidas;
- zero ou muitos ingredientes.



================================================================================
5) MULTIPLICIDADE 0..n
   ================================================================================


Exemplo:


                 0..n
Estoque ◇──────────── Produto



O significado:


Para um Estoque:


Pode existir:


0 Produtos

ou

1 Produto

ou

muitos Produtos.



Exemplo:


Estoque Principal


|
|---- Refrigerante
|
|---- Queijo
|
|---- Farinha
|
|---- Pudim



================================================================================
6) NAVEGABILIDADE (SETA →)
   ================================================================================


A seta indica quem consegue acessar quem.


Exemplo:


Estoque ─────────→ Produto



Significa:


"O Estoque consegue acessar seus Produtos."



A seta NÃO significa:


❌ Produto cria o Estoque.

❌ Produto é dono do Estoque.

❌ Produto vem do Estoque.



Ela significa:


✅ Existe uma referência de Estoque para Produto.



================================================================================
7) DIFERENÇA ENTRE AGREGAÇÃO E COMPOSIÇÃO
   ================================================================================


AGREGAÇÃO:

Símbolo:

◇


Exemplo:


Estoque ◇──────── Produto



Ideia:


O Produto faz parte do Estoque,
mas pode existir separado.






COMPOSIÇÃO:

Símbolo:

◆


Exemplo:


Pizza ◆──────── Massa



Ideia:


A parte depende do todo para existir.






Resumo:


◇ = parte independente

◆ = parte dependente



================================================================================
8) REPRESENTAÇÃO EM JAVA
   ================================================================================


Como a regra de negócio diz:


"O estoque contém produtos."



O código mais natural:


*/


import java.util.List;


class Estoque {


    private List<Sobremesa> sobremesas;

    private List<Bebida> bebidas;

    private List<Ingrediente> ingredientes;


}



class Sobremesa {

    private String nome;

}



class Bebida {

    private String nome;

}



class Ingrediente {

    private String nome;

}



/*
================================================================================
9) EXPLICANDO O CÓDIGO
   ================================================================================


Classe:


Estoque


possui listas de:


- Sobremesa
- Bebida
- Ingrediente



Isso representa:


Estoque ◇──────── Produto



Porque:


Estoque é o todo.

Produtos são as partes.



================================================================================
10) OBSERVAÇÃO SOBRE O DIAGRAMA DO PROFESSOR
    ================================================================================


Caso o desenho apareça assim:


Produto ◇────────→ Estoque



Tecnicamente:

- O diamante está indicando Produto como agregador.
- A seta indica Produto navegando para Estoque.


Porém, essa leitura não combina com a regra de negócio:


"Dentro do estoque existem produtos."


Para esse domínio, o modelo mais coerente é:


Estoque ◇────────→ Produto



================================================================================
RESUMO FINAL
================================================================================


Relacionamento:

É a ligação entre classes.



Associação:

Relacionamento entre objetos/classes.



Agregação:

Associação especial representada por:

◇


Indica:


"Todo e parte."



Navegabilidade:

Representada pela seta:

→


Indica quem conhece quem.



Multiplicidade:


1

Exatamente um.


0..1

Zero ou um.


0..n

Zero até muitos.



No exemplo:


Estoque ◇────────→ Produto


Significa:


"Um Estoque pode possuir zero ou muitos Produtos."



================================================================================
*/


/*
================================================================================
        EXEMPLO JAVA - ESTOQUE E PRODUTOS
================================================================================


Regra de negócio:

Um Estoque contém produtos.

Relacionamento UML:

                 0..n
Estoque ◇────────────────→ Produto


Significado:

- Estoque é o todo.
- Produtos são as partes.
- Um estoque pode possuir vários produtos.

================================================================================
*/


import java.util.List;


// Classe que representa o estoque
class Estoque {


    private List<Sobremesa> sobremesas;

    private List<Bebida> bebidas;

    private List<Ingrediente> ingredientes;


    // O estoque conhece seus produtos.


}



// Produto específico
class Sobremesa {


    private String nome;


}



// Produto específico
class Bebida {


    private String nome;


}



// Produto específico
class Ingrediente {


    private String nome;


}

/*
================================================================================
        UML - RELAÇÃO MUITOS PARA UM (N:1)
        ESTOQUE E PRODUTOS
================================================================================


Representação UML:


                 0..n              1
Estoque ◇──────────────── Produto



Leitura correta:


Um Estoque pode possuir zero até muitos Produtos.


Cada Produto pertence a exatamente um Estoque.



================================================================================
EXPLICANDO A MULTIPLICIDADE
================================================================================


O número colocado do lado de uma classe indica:

"Quantas instâncias dessa classe podem existir
em relação a uma instância da outra classe."



Exemplo:


                 0..n              1
Estoque ◇──────────────── Produto



O 0..n do lado de Produto significa:


"Um Estoque pode ter muitos Produtos."


O 1 do lado de Estoque significa:


"Cada Produto pertence a um único Estoque."



================================================================================
RELAÇÃO NO MUNDO REAL
================================================================================


Exemplo:


Estoque Principal


    |
    |---- Queijo
    |---- Farinha
    |---- Refrigerante
    |---- Pudim



Temos:


1 Estoque

possui

Muitos Produtos



Por isso:


Produto N : 1 Estoque



================================================================================
EXEMPLO EM JAVA
================================================================================
*/


import java.util.List;


class Estoque {


    private List<Produto> produtos;

    /*
     * Um estoque possui uma coleção de produtos.
     *
     * List representa a ideia de:
     *
     * 0..n produtos
     */


}



class Produto {


    private Estoque estoque;

    /*
     * Cada produto conhece o estoque ao qual pertence.
     *
     * Representa:
     *
     * Muitos produtos ligados a um único estoque.
     */


}


/*
================================================================================
RESUMO
================================================================================


Relacionamento:

N : 1


Significa:


Muitos Produtos pertencem a Um Estoque.


UML:


Estoque 1 ---------------- 0..n Produto


Java:


Estoque

    List<Produto>


Produto

    Estoque estoque



================================================================================
*/

◇  → agregação
◆  → composição
△  → herança/generalização
→  → navegabilidade
0..n → multiplicidade


/*
================================================================================
                    RELAÇÕES UML - COMPOSIÇÃO E AGREGAÇÃO
================================================================================



================================================================================
1ª RELAÇÃO - COMPOSIÇÃO
================================================================================


Símbolo:

◆


Composição representa uma relação mais forte entre as partes envolvidas.
O objeto/classe que possui o diamante preto representa o:
TODO 
A outra classe representa uma:
PARTE

Exemplo:
Pizza ◆──────── Sabor

Leitura:
"A Pizza é composta por um Sabor."

Significa que o Sabor faz parte da estrutura da Pizza
e possui seu ciclo de vida ligado à Pizza dentro daquele modelo.

A ideia principal:

Se o TODO deixa de existir, a PARTE também deixa de existir.

Exemplo:
Pizza
|
└── Sabor

A existência do Sabor depende daquela Pizza específica.

class Pizza {

    private Sabor sabor;

       public Pizza() {
        this.sabor = new Sabor();
    }

}

/*
================================================================================
1ª RELAÇÃO - COMPOSIÇÃO
================================================================================


Símbolo:

◆


Composição representa uma relação mais forte entre as partes envolvidas.


O objeto/classe que possui o diamante preto representa o:

TODO


A outra classe representa uma:

PARTE



Exemplo:


Pedido ◆──────── ItemPedido



Leitura:


"Um Pedido é composto por um ou vários Itens de Pedido."



Significa que o ItemPedido faz parte da estrutura do Pedido
e possui seu ciclo de vida ligado ao Pedido dentro daquele modelo.



A ideia principal:


Se o TODO deixa de existir, a PARTE também deixa de existir.



Exemplo:


Pedido 1001

      ◆
      |
      ├── ItemPedido
      |
      ├── ItemPedido
      |
      └── ItemPedido



O Pedido representa o todo.


Os ItemPedido representam as partes.



Se o Pedido 1001 for excluído:


Pedido 1001 ❌


Os ItemPedido ligados a ele também deixam de existir.



================================================================================
EXEMPLO EM JAVA
================================================================================
*/


import java.util.List;


class Pedido {


    private List<ItemPedido> itens;


    /*
     * O Pedido controla seus ItensPedido.
     *
     * Os itens fazem parte da composição do Pedido.
     *
     * Um Pedido pode possuir vários itens.
     */

}



class ItemPedido {


    private Produto produto;


    private int quantidade;


    /*
     * O ItemPedido representa um item específico
     * dentro de uma venda.
     *
     * Ele guarda qual Produto foi comprado
     * e a quantidade.
     *
     * O ItemPedido depende do Pedido para existir.
     */

}



class Produto {


    private String nome;


    private double preco;


    /*
     * O Produto possui existência própria.
     *
     * Ele pode existir antes do Pedido ser criado.
     *
     * Exemplo:
     *
     * Pizza Calabresa
     * Refrigerante
     * Pudim
     */

}



/*
================================================================================
2ª RELAÇÃO - ASSOCIAÇÃO
================================================================================
Entre:
ItemPedido ───────── Produto
Não existe composição.
É uma associação simples.

class Produto {


    private String nome;

    private double preco;


    /*
     * O Produto não precisa conhecer ItemPedido.
     *
     * Ele existe independente das vendas.
     *
     * Exemplos:
     *
     * Pizza Calabresa
     * Refrigerante
     * Pudim
     */


}

Leitura:
"Um ItemPedido possui uma referência para um Produto."

class ItemPedido {


    private Produto produto;


}

O Produto não depende do ItemPedido.

Exemplo:
1º-Produto:
Pizza Calabresa

Depois:
2º-Pedido:

ItemPedido
|
└── Pizza Calabresa

A Pizza continua existindo mesmo depois
que o Pedido termina.

/*
================================================================================
        COMPOSIÇÃO - PEDIDO E ITEMPEDIDO
================================================================================


UML:

Pedido ◆──────── ItemPedido

Significado:

Um Pedido é composto por vários ItensPedido.

O Pedido é o TODO.
O ItemPedido é a PARTE.

O ItemPedido existe dentro do contexto de um Pedido.

================================================================================
*/


import java.util.List;


class Pedido {

    private List<ItemPedido> itens;


    /*
     * O Pedido possui uma coleção de ItemPedido.
     *
     * Representa:
     *
     * Pedido 1 -------- 0..n ItemPedido
     *
     * Um pedido pode ter:
     *
     * - Pizza
     * - Bebida
     * - Sobremesa
     *
     * Cada um representado por um ItemPedido.
     */


}

O ItemPedido funciona como uma classe intermediária entre Pedido e Produto.

Ele representa cada item vendido dentro de um Pedido.

Além de ligar um Produto ao Pedido, ele também encapsula
informações específicas daquela venda, como:

- quantidade;
- preço no momento da venda;
- desconto (se houver);
- observações;
- subtotal.

Para criar pedido precisa saber quem e o cliente

class Pedido {

    private Cliente cliente;

}
================================================================================
RESUMO FINAL
================================================================================


Pedido ◆──────── ItemPedido ───────── Produto



Pedido e ItemPedido:


◆ Composição


"O Pedido é formado por ItensPedido."



ItemPedido e Produto:


Associação


"O ItemPedido conhece qual Produto foi vendido."



Regra mental:


◆ Composição:

A parte depende do todo.


Exemplo:

Pedido → ItemPedido



──────── Associação:

Um objeto apenas conhece outro.


Exemplo:

ItemPedido → Produto



================================================================================
*/

================================================================================
3ª RELAÇÃO - AGREGAÇÃO
================================================================================
Símbolo:
◇
Agregação representa uma relação mais fraca entre as partes envolvidas.
O objeto/classe que possui o diamante branco representa o:
TODO
A outra classe representa uma:
PARTE
Exemplo:
Estoque 1 ◇──────── Produto 0..n
Leitura:
"Um Estoque possui zero até muitos Produtos."
Significa:
- Um Estoque pode existir sem nenhum Produto.
- Um Produto pode existir independentemente do Estoque.
Exemplo:
Estoque vazio:
Estoque
(nenhum produto)
Depois:
Estoque

|
|── Refrigerante
|── Queijo
|── Farinha

O Estoque continua existindo mesmo sem produtos.

class Estoque {

    private Produto produto;


    public Estoque(Produto produto) {
        this.produto = produto;
    }

}
================================================================================
DIFERENÇA PRINCIPAL
================================================================================
COMPOSIÇÃO:
◆
Relação forte.
O TODO controla a existência da PARTE.
Exemplo:
Pedido ◆──── ItemPedido
Se o Pedido for excluído:
Os ItensPedido também deixam de existir.

AGREGAÇÃO:
◇
Relação mais fraca.
A PARTE possui existência independente.
Exemplo:
Estoque ◇──── Produto

Se o Estoque for excluído:
O Produto continua existindo.

================================================================================
REGRA MENTAL
================================================================================
Pergunte:
"A parte consegue existir sozinha?"
SIM:

Pode ser agregação.
◇
NÃO:

Pode ser composição.
◆
================================================================================
*/



