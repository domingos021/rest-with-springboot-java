/*
================================================================================
        UML - RELACIONAMENTO ENTRE PRODUTOS E ESTOQUE
================================================================================


Cenário:

Temos três tipos de produtos:

- Sobremesa
- Bebida
- Ingrediente


Todos eles possuem relação com Estoque.



================================================================================
1) DIAGRAMA APRESENTADO PELO PROFESSOR
================================================================================


O professor representou algo parecido com:


        0..n
Sobremesa ◇──────────────→ Estoque


        0..n
Bebida ◇────────────────→ Estoque


        0..n
Ingrediente ◇───────────→ Estoque



Analisando o desenho:

◇

Representa agregação.


Linha:

Representa uma associação (relacionamento).


→

Representa navegabilidade.


0..n:

Representa multiplicidade.



================================================================================
2) ASSOCIAÇÃO (RELACIONAMENTO)
================================================================================


A associação é o relacionamento entre duas classes.


Exemplo:


Sobremesa ---------------- Estoque


Significa:


"Existe uma ligação entre Sobremesa e Estoque."


Em Java, uma associação é representada por uma referência:


*/


class Sobremesa {

    private Estoque estoque;

}


/*
Aqui existe:


Sobremesa está associada a Estoque.



================================================================================
3) NAVEGABILIDADE (SETA →)
================================================================================


No desenho:


Sobremesa ─────────────→ Estoque



A seta indica:


"A Sobremesa consegue acessar o Estoque."


Em Java:


*/


class SobremesaComNavegacao {

    private Estoque estoque;

}


/*
A referência:


private Estoque estoque;


representa a navegabilidade.


IMPORTANTE:


A seta NÃO significa:


❌ A sobremesa vem do estoque.

❌ A sobremesa cria o estoque.

❌ O estoque pertence à sobremesa.



A seta significa apenas:


✅ A Sobremesa conhece o Estoque.



================================================================================
4) AGREGAÇÃO (LOSANGO BRANCO ◇)
================================================================================


O símbolo:


◇


representa agregação.


Agregação é um tipo especial de associação.


Ela representa a ideia:


"Um objeto possui outro objeto como parte."



Exemplo:


Estoque ◇──────── Produto


Leitura:


"Estoque possui produtos."



A parte pode existir independentemente do todo.


Exemplo:


Um produto pode existir mesmo que não esteja em um estoque.



================================================================================
5) INTERPRETANDO A REGRA DE NEGÓCIO DO PROFESSOR
================================================================================


O professor explicou:


"Dentro do estoque existem:

- Sobremesas;
- Bebidas;
- Ingredientes."



A interpretação do domínio é:


                    Estoque

                       |
        --------------------------------
        |              |               |
   Sobremesa        Bebida       Ingrediente



Ou seja:


Estoque é o todo.

Produtos são as partes.



Então o modelo conceitual mais natural é:


             0..n
Estoque ◇────────────→ Sobremesa


             0..n
Estoque ◇────────────→ Bebida


             0..n
Estoque ◇────────────→ Ingrediente



Leitura:


"Um estoque pode possuir vários produtos."



================================================================================
6) MULTIPLICIDADE 0..n
================================================================================


Exemplo:


             0..n
Estoque ◇──────── Produto



O significado:


Um Estoque pode possuir:

0 produtos

ou

1 produto

ou

muitos produtos.



Exemplo:


Estoque Principal

|
|---- Pizza congelada
|
|---- Refrigerante
|
|---- Queijo
|
|---- Farinha



================================================================================
7) QUANDO EXISTE O 1
================================================================================


Exemplo:


             0..n
Estoque ◇──────── Produto
       1



Significa:


Cada Produto pertence a exatamente 1 Estoque.


Exemplo:


Pizza congelada

        |
        ↓

Estoque Principal



Um produto não pertence a vários estoques ao mesmo tempo.



================================================================================
8) MODELO JAVA REPRESENTANDO A REGRA DO NEGÓCIO
================================================================================


Como o professor explicou:


"O estoque contém produtos."



O código mais fiel seria:


*/


import java.util.List;


class Estoque {


    private List<Sobremesa> sobremesas;

    private List<Bebida> bebidas;

    private List<Ingrediente> ingredientes;


}



/*
O Estoque possui coleções dos produtos.



================================================================================
9) CLASSES DOS PRODUTOS
================================================================================
*/


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
Aqui temos:


Estoque

TEM:

- Sobremesas
- Bebidas
- Ingredientes



Isso representa:


Estoque ◇──────── Produto



================================================================================
10) COMPARAÇÃO ENTRE O DESENHO E O DOMÍNIO
================================================================================


DESENHO DO PROFESSOR:


Produto ◇────────→ Estoque


Mostra:


- Produto possui uma referência para Estoque.
- Existe navegabilidade Produto → Estoque.



REGRA DE NEGÓCIO EXPLICADA:


Estoque contém produtos.


Modelo mais natural:


Estoque ◇────────→ Produto


Mostra:


- Estoque é o agregador.
- Produtos são partes do estoque.



================================================================================
RESUMO FINAL
================================================================================


RELACIONAMENTO:

É a ligação entre classes.



ASSOCIAÇÃO:

É a ligação entre objetos/classes.



AGREGAÇÃO:

É uma associação especial.

Símbolo:

◇


Representa:


"Todo e parte"



NAVEGABILIDADE:

É a direção da seta.


→


Mostra quem conhece quem.



MULTIPLICIDADE:


1

Exatamente um.


0..n

Zero até muitos.



NO CASO DO ESTOQUE:


A regra de negócio é:


Estoque possui produtos.


Portanto:


Estoque ◇──────── Produto


é a representação mais coerente do domínio.


================================================================================
*/