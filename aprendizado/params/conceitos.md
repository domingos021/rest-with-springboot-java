# Query Params e Path Params

## O que são parâmetros de URL?

Parâmetros de URL são valores enviados junto com uma requisição para indicar qual recurso queremos acessar ou como queremos consultá-lo. Em APIs HTTP, os dois formatos comuns são **path params** e **query params**.

## Path params

Path params (parâmetros de caminho) fazem parte do caminho da URL. Eles costumam identificar um recurso específico, como um usuário pelo seu ID.

```http
GET /users/42
```

Nesse exemplo, `42` é o ID do usuário. Uma rota pode ser descrita com um espaço reservado:

```text
GET /users/{userId}
```

O valor real substitui `{userId}` na requisição:

```text
GET /users/42
```

### Quando usar

Use path params quando o valor identifica qual recurso ou sub-recurso será acessado.

```http
GET    /users/42          # consultar o usuário 42
PUT    /users/42          # atualizar o usuário 42
DELETE /users/42          # remover o usuário 42
GET    /users/42/orders   # consultar os pedidos do usuário 42
```

## Query params

Query params (parâmetros de consulta) aparecem depois de `?` na URL. Eles geralmente filtram, ordenam ou controlam a apresentação dos resultados. Pares adicionais são separados por `&`.

```http
GET /users?status=active&role=admin
```

Nesse exemplo, `status=active` e `role=admin` são query params.

### Quando usar

Use query params para opções de consulta que podem variar sem mudar o recurso principal solicitado.

```http
GET /users?page=2&size=20              # paginação
GET /products?category=books            # filtro
GET /products?sort=price&direction=asc  # ordenação
GET /users?name=Ana&status=active       # combinação de filtros
```

## Diferenças principais

| Aspecto | Path param | Query param |
|---|---|---|
| Onde aparece | No caminho da URL | Depois de `?` |
| Formato | `/users/42` | `/users?status=active` |
| Uso comum | Identificar um recurso | Filtrar, ordenar ou paginar resultados |
| Pode haver vários? | Sim, em partes diferentes do caminho | Sim, separados por `&` |

## Usando os dois juntos

Uma requisição pode combinar os formatos. O path identifica o usuário e os query params controlam os pedidos retornados:

```http
GET /users/42/orders?status=paid&page=1&size=10
```

- `42` é o path param `userId`.
- `status=paid`, `page=1` e `size=10` são query params.

Uma definição de rota poderia ser:

```text
GET /users/{userId}/orders?status={status}&page={page}&size={size}
```

## Exemplos em código

### JavaScript: ler os valores de uma URL

```javascript
const url = new URL("https://api.example.com/users/42?status=active&page=2");

// Query params são fornecidos pela API URLSearchParams.
const status = url.searchParams.get("status"); // "active"
const page = url.searchParams.get("page");     // "2"

// Para obter o path param, a aplicação interpreta o caminho da rota.
const segments = url.pathname.split("/");
const userId = segments[2]; // "42", para o caminho /users/42
```

### Spring Boot: declarar path e query params

```java
@GetMapping("/users/{userId}")
public User getUser(
        @PathVariable Long userId,
        @RequestParam(required = false) String status) {
    // userId vem do caminho; status vem da query string.
    return userService.findUser(userId, status);
}
```

Exemplo de chamada:

```http
GET /users/42?status=active
```

- `@PathVariable` recebe `42`.
- `@RequestParam` recebe `active`.

Para paginação, valores padrão podem ser definidos assim:

```java
@GetMapping("/users")
public Page<User> listUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    return userService.findAll(page, size);
}
```

Exemplo:

```http
GET /users?page=1&size=10
```

## Dicas práticas

- Use path params para indicar **qual recurso** está sendo acessado.
- Use query params para indicar **quais critérios ou opções** aplicar à consulta.
- Nomes e valores de query params devem ser codificados corretamente quando contêm espaços ou caracteres especiais. Por exemplo, `Ana Maria` pode ser enviado como `Ana%20Maria`.
- Query params normalmente são opcionais, mas isso depende do contrato da API.
- Não coloque senhas, tokens ou outros segredos na URL: URLs podem aparecer em históricos, logs e ferramentas de monitoramento.

## Resumo

```text
/users/42?status=active&page=2
       └─ path param: identifica o usuário
                     └─ query params: filtram ou controlam os resultados
```

**Em resumo:** path params apontam para o recurso; query params ajustam a consulta feita sobre ele.
