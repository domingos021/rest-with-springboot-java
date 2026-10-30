# Modelagem do E-commerce

## Sumário
1. [UML das Entidades](#uml-das-entidades)
2. [Relacionamentos](#relacionamentos)
3. [Visão Geral](#visão-geral)
4. [Person (Cliente)](#person-cliente)
5. [Order (Pedido)](#order-pedido)
6. [Product (Produto)](#product-produto)
7. [OrderItem (Item do Pedido)](#orderitem-item-do-pedido)
8. [Por que salvar o preço no OrderItem?](#por-que-salvar-o-preço-no-orderitem)
9. [Por que existe OrderItem?](#por-que-existe-orderitem)
10. [Fluxo de uma compra](#fluxo-de-uma-compra)
11. [Exemplo usando o JSON retornado pela API](#exemplo-usando-o-json-retornado-pela-api)
12. [Resumo](#resumo)

---

# UML das Entidades

## Product

```text
+--------------------------------------+
|              «abstract»              |
|               Product                |
+--------------------------------------+
| - id: Long                           |
| - title: String                      |
| - price: BigDecimal                  |
| - launchDate: LocalDateTime           |
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

# Visão Geral

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
Person (id = 6)
    │
    ├──────── Order 1
    ├──────── Order 2
    └──────── Order 3
```

### Cardinalidade
```text
Person 1 -------- 0..N Order
```
* Um cliente pode não possuir nenhum pedido.
* Um cliente pode possuir vários pedidos.
* Um pedido pertence obrigatoriamente a um único cliente.

---

# Order (Pedido)

Representa uma compra realizada pelo cliente. O pedido **não representa um produto**, mas toda a transação da compra.

Exemplo:

```text
Order
-----------------------------
id = 1
customer = Person 6
status = PENDING
totalAmount = 219.70
orderDate = 27/10/2026
```

O pedido é responsável por armazenar:
* Quem realizou a compra.
* Quando foi realizada.
* O status da compra.
* O valor total.

### Cardinalidade
```text
Order 1 -------- 0..N OrderItem
```

---

# Product (Produto)

Representa qualquer produto vendido pelo sistema. O pedido não precisa saber o tipo concreto do produto, trabalhando apenas com a abstração `Product`.

Exemplo:

```text
Product
------------------------
id = 5
title = Relativity
price = 99.90
```

No futuro, podem existir diversos tipos de produtos sem alterar a entidade `Order`:

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

---

# OrderItem (Item do Pedido)

É a entidade intermediária que representa um produto dentro de um pedido específico.

Cada item guarda informações do exato momento da compra:

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

---

# Por que salvar o preço no OrderItem?

Imagine a seguinte situação:

1. **Hoje:** O `Product` custa `R$ 99,90` e o cliente realiza a compra.
2. **Daqui a uma semana:** O produto sofre reajuste e passa a custar `R$ 129,90`.

O pedido antigo **não pode mudar**. Por isso, o `OrderItem` guarda o `unitPrice = 99.90` referente à data da compra, preservando o histórico financeiro e fiscal corretamente.

---

# Por que existe OrderItem?

Sem ele, teríamos um relacionamento direto de muitos-para-muitos entre `Order` e `Product`:
* Um pedido possui vários produtos.
* Um produto aparece em vários pedidos.

Relacionamentos *muitos-para-muitos* (`N-N`) exigem uma entidade intermediária (`OrderItem`). Além de resolver essa relação, ela armazena atributos que pertencem à transação (como quantidade, preço pago, desconto e subtotal) e que **não** pertencem ao produto em si.

---

# Fluxo de uma compra

O fluxo completo funciona assim:

```text
Cliente (Person)
      │
      ▼
Cria um pedido (Order)
      │
      ▼
Adiciona itens (OrderItem)
      │
      ▼
Cada item referencia um produto (Product)
```

Exemplo prático:

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
# Exemplo mais amplo e real da api

```json
{
  "id": 1,
  "customer_id": 6,
  "order_date": "2026-10-27T16:59:01.462569",
  "status": "PENDING",
  "total_amount": 219.70,
  "items": [
    {
      "id": 1,
      "product_id": 5,
      "quantity": 1,
      "unit_price": 99.90,
      "discount": 0.00,
      "subtotal": 99.90,
      "created_at": "2026-10-27T16:59:01.627729",
      "_links": {
        "self": {
          "href": "http://localhost:8080/order/1/item/1"
        },
        "items": {
          "href": "http://localhost:8080/order/1/item"
        },
        "order": {
          "href": "http://localhost:8080/order/1"
        }
      }
    },
    {
      "id": 2,
      "product_id": 2,
      "quantity": 2,
      "unit_price": 59.90,
      "discount": 0.00,
      "subtotal": 119.80,
      "created_at": "2026-10-27T16:59:01.636724",
      "_links": {
        "self": {
          "href": "http://localhost:8080/order/1/item/2"
        },
        "items": {
          "href": "http://localhost:8080/order/1/item"
        },
        "order": {
          "href": "http://localhost:8080/order/1"
        }
      }
    }
  ],
  "created_at": "2026-10-27T16:59:01.499573",
  "updated_at": "2026-10-27T16:59:01.499573",
  "_links": {
    "self": {
      "href": "http://localhost:8080/order/1"
    },
    "orders": {
      "href": "http://localhost:8080/order"
    },
    "items": {
      "href": "http://localhost:8080/order/1/item"
    }
  }
}
```

Interpretando o JSON:

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

Cada entidade possui uma única responsabilidade:

| Entidade | Responsabilidade |
| :--- | :--- |
| **Person** | Representa quem realizou a compra (o cliente). |
| **Order** | Representa a transação/pedido realizado. |
| **OrderItem** | Representa o produto dentro da compra com os valores congelados. |
| **Product** | Representa o catálogo de produtos vendidos pelo sistema. |

Essa modelagem segue um padrão amplamente utilizado em arquiteturas de e-commerce corporativas, garantindo separação de responsabilidades e integridade histórica.