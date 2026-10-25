# Relacionamentos em Orientação a Objetos, UML e Banco de Dados

## Objetivo
Entender os principais tipos de relacionamentos, sua representação na UML, impacto no banco de dados, localização da chave estrangeira e exemplos práticos.

## Índice
1. Associação
2. Associação Unidirecional
3. Associação Bidirecional
4. Agregação
5. Composição
6. Dependência
7. Herança (IS-A)
8. Interfaces
9. Relacionamentos do Banco de Dados (1:1, 1:N, N:1, N:N)
10. Resumo Geral
11. Regras Importantes

---

# 1. Associação

## Conceito
É um relacionamento onde uma classe conhece ou possui referência para outra. Não existe dependência forte; os objetos podem existir separadamente.

## Exemplo
Pessoa e Carro. Uma pessoa pode possuir um carro, mas ambos continuam existindo separadamente.

```text
Pessoa ---------------- Carro
```

## Banco de Dados
Exemplo: `Pessoa 1 -------- N Carro`  
A chave estrangeira fica no lado N.

### PERSON
```text
id
nome
```

### CAR
```text
id
modelo
person_id FK
```
*(A tabela Person não recebe uma coluna `car_id`)*

---

# 2. Associação Unidirecional

Somente uma classe conhece a outra.

```text
Pedido ----------> Cliente
```
* Pedido conhece Cliente.
* Cliente não conhece Pedido.

## Banco
A FK depende da cardinalidade e não da seta da UML.

---

# 3. Associação Bidirecional

As duas classes conhecem uma à outra.

```text
Autor ------------ Livro
```
Mesmo sendo bidirecional no código, no banco existe apenas uma FK.

### BOOK
```text
id
title
author_id FK
```

---

# 4. Agregação

## Conceito
Relacionamento **HAS-A** com dependência fraca.  
Representação: `Departamento ◇------ Funcionário` (Diamante branco).

## Exemplo
Universidade possui professores. Se a universidade fechar, o professor continua existindo.

## Banco
Não possui regra especial. A FK continua dependendo da cardinalidade.

### EMPLOYEE
```text
id
name
department_id FK
```

---

# 5. Composição

## Conceito
Relacionamento **HAS-A** com dependência forte.  
Representação: `Pedido ◆------ ItemPedido` (Diamante preto).

## Exemplo
Pedido e ItemPedido. Um item não faz sentido sem o pedido.

## Banco
A FK fica no objeto dependente (e normalmente utiliza `Cascade`).

### ORDER
```text
id
```

### ORDER_ITEM
```text
id
order_id FK
```

---

# 6. Dependência

## Conceito
Uma classe apenas utiliza outra temporariamente.

```text
PagamentoService ---> EmailService
```

## Banco
* Não cria tabela.
* Não cria FK.  
  *(É estritamente um relacionamento de código).*

---

# 7. Herança (IS-A)

## Conceito
Representa "é um". Exemplo: `Livro É UM Produto`.

```text
    Produto
       ▲
       |
     Livro
```

## Banco
Pode utilizar diferentes estratégias no ORM:
* `SINGLE_TABLE`
* `JOINED`
* `TABLE_PER_CLASS`

---

# 8. Interfaces

Uma classe implementa um contrato.

```text
    Pagamento
       ▲
       |
     Cartao
```

## Banco
* Não cria tabela.

---

# 9. Relacionamentos do Banco de Dados

## One-to-One (1:1)
Uma entidade possui uma única entidade relacionada. Exemplo: *Pessoa e Passaporte*.
* FK pode ficar em qualquer lado, normalmente na entidade dependente.
* **Importante:** Exige o uso de `UNIQUE` na coluna da FK para garantir a regra de um para um.

## One-to-Many (1:N)
Um possui muitos. Exemplo: *Autor possui vários Livros*.
* A FK **sempre** fica no lado `MANY`.

## Many-to-One (N:1)
É o mesmo relacionamento visto pelo lado N. A FK continua no lado `MANY`.

## Many-to-Many (N:N)
Muitos para muitos. Exemplo: *Aluno e Curso*.
* Exige a criação de uma **tabela intermediária**:
```text
ALUNO_ID | CURSO_ID (ambas FKs)
```

---

# 10. Resumo Geral

| Relacionamento | Impacto no Banco | Onde fica a FK? |
| :--- | :--- | :--- |
| **Associação** | Depende da cardinalidade | Normalmente no lado N |
| **Agregação** | Igual associação | Lado dependente |
| **Composição** | Igual associação | Lado filho |
| **Dependência** | Não altera o banco | Não existe |
| **Herança** | Depende da estratégia | Varia conforme o mapeamento |
| **1:1** | Duas tabelas | Entidade dependente (com `UNIQUE`) |
| **1:N / N:1** | Duas tabelas | Sempre no lado N |
| **N:N** | Três tabelas | Tabela intermediária |

---

# 11. Regras Importantes

1. UML representa objetos, navegabilidade e responsabilidades.
2. Banco de dados representa armazenamento e integridade física.
3. O diamante (preto ou branco) **não define** a localização da FK.
4. A **cardinalidade** é quem manda na localização da FK.
5. Em relacionamentos N:N, sempre existirá uma tabela intermediária.