# Fluxo de criação de Order e OrderItem

## 1. Pré-requisito: existir um Customer (Person)

Antes de criar um pedido (`Order`), é necessário existir um cliente (`Customer`).

No modelo atual, o cliente é representado pela entidade `Person`.

O usuário **não é criado junto com o Order**.
Primeiro a pessoa precisa existir no banco.

Exemplo:

```text
Person (usuário já existente)

id = 4
nome = João Silva

        |
        |
        v

Pode criar um novo Order usando esse ID
```

---

# 2. Criar um Order usando um Customer existente

## Requisição HTTP

```http
POST http://localhost:8080/order

Content-Type: application/json
Accept: application/json
```

### Body enviado:

```json
{
    "customer_id": 3
}
```

```json
{
  "id": 6,
  "customer_id": 3,
  "order_date": "2026-10-30T15:23:35.7340268",
  "status": "PENDING",
  "total_amount": 0,
  "items": [],
  "created_at": "2026-10-30T15:23:35.8048156",
  "updated_at": "2026-10-30T15:23:35.8048156",
  "_links": {
    "self": {
      "href": "http://localhost:8080/order/6"
    },
    "orders": {
      "href": "http://localhost:8080/order"
    },
    "items": {
      "href": "http://localhost:8080/order/6/item"
    }
  }
}
```

---

## Fluxo interno da aplicação

```text
POST /order

/*
 * O cliente envia apenas o ID da pessoa que realizará o pedido.
 *
 * Exemplo:
 *
 * {
 *     "customer_id": 4
 * }
 *
 * O customer_id representa uma Person que já existe no banco.
 */


        |
        v


OrderController

/*
 * Recebe o customer_id enviado no JSON.
 */


        |
        v


OrderService

/*
 * Responsável pela regra de negócio:
 *
 * 1. Busca a Person pelo id informado:
 *
 *    Person id = 4
 *
 * 2. Cria o Order relacionando:
 *
 *    Order + Customer(Person)
 *
 * 3. Define valores controlados pelo sistema:
 *
 *    status      = PENDING
 *    order_date  = data atual
 *    totalAmount = calculado pelos itens
 *
 * 4. Salva o Order no banco.
 */


        |
        v


Order criado

/*
 * Exemplo de retorno:
 *
 * Order
 *  id = 5
 *  customer_id = 4
 *  status = PENDING
 *  total_amount = 0.00
 *  items = []
 *
 * O ID do Order foi gerado automaticamente pelo banco.
 */
```

---

# 3. Adicionar itens ao Order criado

Agora que o `Order` existe, podemos adicionar produtos através de `OrderItem`.

Exemplo:

```text
Order criado:

id = 6
customer_id = 3

Agora vamos adicionar produtos dentro desse pedido.
```

---

## Requisição HTTP

O  `order/6` e o pedido que foi criado associado ao customer com id 3

```http
POST http://localhost:8080/order/6/item

Content-Type: application/json
Accept: application/json
```

### Body enviado:

O `product_id` deve ser um produto que já existe no banco.
Neste exemplo, o ID 3 representa um Book cadastrado,
o repository busca no banco o produto concreto com esse id.

#### Entrada no body da requisição OrderItemInsertDto

1. controller -> envia para o service
2. service aciona o repository que aciona o banco
3. repository retorna a entidade orderItem
4. Server pega a entidade manda o mapper converter para DTO
5. server pega esse dado convertido mando de volta como resposta ao controller(dto)
```json
{
    "product_id": 3,
    "quantity": 5
}
```
#### saida ou resposta da requisição OrderItemDTO
6. controller exibe como resposta da requisição a dto que veio do service
```json
{
  "product_id": 3,
  "quantity": 5,
  "unit_price": 89.90,
  "discount": 0,
  "subtotal": 449.50,
  "_links": {
    "self": {
      "href": "http://localhost:8080/order/6/item/{id}",
      "templated": true
    },
    "items": {
      "href": "http://localhost:8080/order/6/item"
    },
    "order": {
      "href": "http://localhost:8080/order/6"
    }
  }
}
```

---
## Agora quando buscamos o order 6 que antes estava vazio ele esta preenchido
## Requisição HTTP

O  `order/6` e o pedido que foi criado associado ao customer com id 3

```http
GET http://localhost:8080/order/6

Content-Type: application/json
Accept: application/json
```

Resposta da requisição

```json
{
  "id": 6,
  "customer_id": 3,
  "order_date": "2026-10-30T15:23:35.734027",
  "status": "PENDING",
  "total_amount": 449.50,
  "items": [
    {
      "id": 8,
      "product_id": 3,
      "quantity": 5,
      "unit_price": 89.90,
      "discount": 0.00,
      "subtotal": 449.50,
      "created_at": "2026-10-30T15:44:26.673686",
      "_links": {
        "self": {
          "href": "http://localhost:8080/order/6/item/8"
        },
        "items": {
          "href": "http://localhost:8080/order/6/item"
        },
        "order": {
          "href": "http://localhost:8080/order/6"
        }
      }
    }
  ],
  "created_at": "2026-10-30T15:23:35.804816",
  "updated_at": "2026-10-30T15:44:26.879683",
  "_links": {
    "self": {
      "href": "http://localhost:8080/order/6"
    },
    "orders": {
      "href": "http://localhost:8080/order"
    },
    "items": {
      "href": "http://localhost:8080/order/6/item"
    }
  }
}
```
## Fluxo interno da aplicação

```text
POST /order/6/item

/*
 * URL recebida pela API:
 *
 * /order
 * -> Recurso principal: representa a entidade Order.
 *
 * /5
 * -> Path Variable {orderId}.
 * -> Identifica qual pedido receberá o item.
 *
 * /item
 * -> Sub-recurso: representa a entidade OrderItem.
 */


        |
        v


OrderItemController

/*
 * Recebe os dados necessários:
 *
 * orderId = 6
 * -> Veio da URL:
 *
 *    /order/{orderId}/item
 *
 *
 * productId = 3
 * -> Veio do JSON enviado no Body.
 *
 *
 * quantity = 5
 * -> Veio do JSON enviado no Body.
 */


        |
        v


OrderItemService

/*
 * Responsável pela regra de negócio:
 *
 * 1. Busca o Order:
 *
 *    Order id = 6
 *
 * 2. Busca o Product:
 *
 *    Product id = 3
 *
 * 3. Cria o OrderItem relacionando:
 *
 *    Order + Product + Quantity
 *
 * 4. Calcula:
 *
 *    subtotal = (unitPrice * quantity) - discount
 *
 * 5. Salva o OrderItem no banco.
 */
```

---

# Relacionamento final

```text
Person (Customer)
        |
        | 1:N
        |
        v

Order
        |
        | 1:N
        |
        v

OrderItem
        |
        | N:1
        |
        v

Product
```

---

# Resumo do fluxo completo

```text
1 - Person já existe
        |
        | customer_id = 4
        v

2 - POST /order

        |
        | cria
        v

3 - Order criado
        |
        | id = 5
        v

4 - POST /order/5/item

        |
        | product_id = 3
        | quantity = 2
        v

5 - OrderItem criado
```