# Modelagem do E-commerce

1. UML das Entidades
2. Relacionamentos (UML)
3. Visão Geral
4. Person
5. Product
6. Order
7. OrderItem
8. Fluxo da compra
9. Exemplo usando JSON
10. Resumo

# UML das Entidades

## Product

```text
+--------------------------------------+
|               Product                |
+--------------------------------------+
| - id: Long                           |
| - title: String                      |
| - price: BigDecimal                  |
| - launchDate: LocalDate              |
| - createdAt: LocalDateTime           |
| - updatedAt: LocalDateTime           |
+--------------------------------------+
```

---

## Order

```text
+--------------------------------------+
|                Order                 |
+--------------------------------------+
| - id: Long                           |
| - customer: Person                   |
| - orderDate: LocalDateTime           |
| - status: OrderStatus                |
| - totalAmount: BigDecimal            |
| - items: List<OrderItem>             |
| - createdAt: LocalDateTime           |
| - updatedAt: LocalDateTime           |
+--------------------------------------+
```

---

## OrderItem

```text
+--------------------------------------+
|             OrderItem                |
+--------------------------------------+
| - id: Long                           |
| - order: Order                       |
| - product: Product                   |
| - quantity: Integer                  |
| - unitPrice: BigDecimal              |
| - discount: BigDecimal               |
| - subtotal: BigDecimal               |
| - createdAt: LocalDateTime           |
+--------------------------------------+
```

---

# Relacionamentos

```text
                 1
Person ----------------------- 0..N Order
                                 |
                                 |
                                 | 1
                                 |
                                 | 0..N
                             OrderItem
                              /      \
                             /        \
                         N  /          \ 1
                           /            \
                      Order              Product
```

Ou de forma mais organizada:

```text
                     +-----------+
                     |  Person   |
                     +-----------+
                           1
                           │
                           │
                           │
                          0..N
                     +-----------+
                     |   Order   |
                     +-----------+
                           1
                           │
                           │
                           │
                          0..N
                  +----------------+
                  |   OrderItem    |
                  +----------------+
                     │          │
                  N  │          │ 1
                     │          │
                     ▼          ▼
                 +--------+  +---------+
                 | Order  |  | Product |
                 +--------+  +---------+
```

---

# Visão simplificada

```text
Product
-----------------------------
id
title
price
launchDate
createdAt
updatedAt


Order
-----------------------------
id
customer
orderDate
status
totalAmount
items
createdAt
updatedAt


OrderItem
-----------------------------
id
order
product
quantity
unitPrice
discount
subtotal
createdAt
```

## Responsabilidade de cada entidade

### Product

```text
Representa o produto vendido.

Exemplos:

Livro
Curso
Eletrônico
Notebook
Celular
```

---

### Order

```text
Representa uma compra.

Não representa produtos.

Representa:

• quem comprou
• quando comprou
• situação da compra
• valor total
```

---

### OrderItem

```text
Representa um produto dentro de uma compra.

Guarda informações que pertencem ao momento da compra:

• produto
• quantidade
• preço pago
• desconto
• subtotal
```

# Modelagem do E-commerce

## Visão geral

Nosso e-commerce possui quatro entidades principais relacionadas entre si:

```text
Person
   │ 1
   │
   │ N
Order
   │ 1
   │
   │ N
OrderItem
   │ N
   │
   │ 1
Product
```

Cada entidade possui uma responsabilidade específica.

---

# Person (Cliente)

Representa a pessoa que realiza uma compra.

Exemplo:

```text
Person
--------------------------
id = 6
name = "Ada Lovelace"
email = "ada@email.com"
```

Um cliente pode realizar diversos pedidos durante sua vida.

Exemplo:

```text
Person
 id = 6
    │
    ├──────── Order 1
    ├──────── Order 2
    └──────── Order 3
```

### Cardinalidade

```text
Person 1 -------- 0..N Order
```

Isso significa:

- Um cliente pode não possuir nenhum pedido.
- Um cliente pode possuir vários pedidos.
- Um pedido pertence obrigatoriamente a um único cliente.

---

# Order (Pedido)

Representa uma compra realizada pelo cliente.

O pedido **não representa um produto**.

Ele representa toda a transação de compra.

Exemplo:

```text
Order
-----------------------------
id = 1
customer = Person 6
status = PENDING
total = 219.70
orderDate = 27/10/2026
```

O pedido é responsável por armazenar:

- Quem realizou a compra.
- Quando foi realizada.
- O status da compra.
- O valor total.

Um pedido possui vários itens.

```text
Order
  │
  ├──────── OrderItem
  ├──────── OrderItem
  └──────── OrderItem
```

### Cardinalidade

```text
Order 1 -------- 0..N OrderItem
```

---

# Product (Produto)

Representa qualquer produto vendido pelo sistema.

O pedido não precisa saber qual é o tipo concreto do produto.

Ele trabalha apenas com a abstração `Product`.

Exemplo:

```text
Product
------------------------
id = 5
title = Relativity
price = 99.90
```

No futuro podem existir diversos tipos de produtos.

```text
           Product
              │
      ┌───────┴────────┐
      │                │
     Book           Course
      │                │
autor            instrutor
isbn             duração
```

O sistema continua funcionando sem alterar a entidade `Order`.

---

# OrderItem (Item do Pedido)

É a entidade que representa um produto dentro de um pedido.

Ela liga um pedido a um produto.

```text
Order
   │
   │
OrderItem
   │
   │
Product
```

Cada item guarda informações da compra.

Exemplo:

```text
OrderItem
----------------------------
product = Product 5
quantity = 2
unitPrice = 59.90
discount = 0.00
subtotal = 119.80
```

Observe que o preço fica salvo no momento da compra.

Isso é extremamente importante.

---

# Por que salvar o preço no OrderItem?

Imagine a seguinte situação.

Hoje:

```text
Product

Preço = R$ 99,90
```

O cliente realiza uma compra.

Depois de uma semana o produto muda de preço.

```text
Product

Preço = R$ 129,90
```

O pedido antigo **não pode mudar**.

Por isso o `OrderItem` guarda:

```text
unitPrice = 99.90
```

Assim o histórico da compra permanece correto.

---

# Por que existe OrderItem?

Sem ele teríamos uma relação direta:

```text
Order -------- Product
```

Mas isso não funciona.

Um pedido pode possuir vários produtos.

Um produto também pode aparecer em vários pedidos.

Na prática teríamos:

```text
Order N -------- N Product
```

Relacionamentos muitos-para-muitos normalmente precisam de uma entidade intermediária.

Essa entidade é:

```text
Order
   │
   │
OrderItem
   │
   │
Product
```

Além disso, o `OrderItem` guarda informações que pertencem à compra:

- quantidade;
- preço pago;
- desconto;
- subtotal.

Essas informações não pertencem ao produto.

---

# Fluxo de uma compra

O fluxo completo fica assim:

```text
Cliente
(Person)
      │
      ▼
Cria um pedido
(Order)
      │
      ▼
Adiciona itens
(OrderItem)
      │
      ▼
Cada item referencia um produto
(Product)
```

Exemplo:

```text
Person (Ada)
      │
      ▼
Order #1
      │
      ├──────────────┐
      ▼              ▼
OrderItem 1      OrderItem 2
      │              │
      ▼              ▼
Product 5        Product 2
```

---

# Exemplo usando o JSON retornado pela API

```json
{
  "id": 1,
  "customer_id": 6,
  "total_amount": 219.70,
  "items": [
    {
      "product_id": 5,
      "quantity": 1,
      "unit_price": 99.90,
      "subtotal": 99.90
    },
    {
      "product_id": 2,
      "quantity": 2,
      "unit_price": 59.90,
      "subtotal": 119.80
    }
  ]
}
```

Podemos interpretar assim:

```text
Person 6
    │
    ▼
Order 1
    │
    ├───────────────┐
    ▼               ▼
OrderItem 1     OrderItem 2
    │               │
    ▼               ▼
Product 5       Product 2

1 × 99.90 = 99.90

2 × 59.90 = 119.80

------------------------

Total = 219.70
```

---

# Resumo

Cada entidade possui uma única responsabilidade.

| Entidade | Responsabilidade |
|----------|------------------|
| **Person** | Quem realizou a compra. |
| **Order** | Representa a compra realizada. |
| **OrderItem** | Representa cada produto dentro da compra. |
| **Product** | Representa o produto vendido pelo sistema. |

Relacionamentos:

```text
Person
   │ 1
   │
   │ N
Order
   │ 1
   │
   │ N
OrderItem
   │ N
   │
   │ 1
Product
```

Essa modelagem segue um padrão amplamente utilizado em sistemas de e-commerce, pois separa claramente as responsabilidades de cada entidade e preserva corretamente o histórico das compras.