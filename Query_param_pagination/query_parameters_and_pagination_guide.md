# Guia Completo: Query Parameters e Busca Paginada

Este documento aborda os conceitos fundamentais sobre **Query Parameters** (parâmetros de consulta) e **busca paginada**, explicando o que são, por que e quando usá-los, além de apresentar exemplos práticos de implementação e ferramentas para testes, como o Mockaroo.

## 1. O que são Query Parameters?

**Query Parameters** (parâmetros de consulta da URL) são pares de chave e valor anexados ao final de uma URL. A seção começa após `?`, e parâmetros adicionais são separados por `&`.

```text
https://api.exemplo.com/produtos?categoria=eletronicos&ordenacao=preco_asc&pagina=1
```

- `?` inicia a seção de parâmetros de consulta.
- `chave=valor` define um parâmetro, como `categoria=eletronicos`.
- `&` separa vários parâmetros.

## 2. Por que usar Query Parameters?

- **Flexibilidade:** permitem usar o mesmo endpoint para diferentes filtros, ordenações e páginas.
- **URLs compartilháveis:** o estado da busca pode ficar na URL.
- **Parâmetros opcionais:** facilitam oferecer filtros sem criar novas rotas.

## 3. Quando usar Query Parameters?

Use-os quando precisar manipular ou apresentar um subconjunto de uma listagem. Exemplos:

1. **Filtragem:** `?status=ativo&preco_min=50`.
2. **Ordenação:** `?ordenacao=data_criacao&direcao=desc`.
3. **Busca por termos:** `?q=smartphone`.
4. **Paginação:** dividir resultados em páginas menores.

## 4. O que é busca paginada?

A **busca paginada** divide muitos registros em blocos menores, carregados sob demanda.

### Por que usar?

- **Rede:** evita transferir grandes volumes de uma vez.
- **Experiência do usuário:** facilita navegar por uma lista.
- **Recursos:** reduz o volume processado e enviado em cada resposta.

## 5. Como implementar paginação

Em APIs, a paginação costuma usar parâmetros para indicar a página e quantos itens retornar. Os nomes variam conforme a implementação. Por exemplo, uma API pode usar `pagina` e `limite`; no Spring Data, o `Pageable` usa `page`, `size` e `sort` por padrão.

### Exemplo genérico de requisição

```http
GET /api/v1/usuarios?q=silva&pagina=2&limite=15 HTTP/1.1
Host: api.exemplo.com
```

Neste exemplo, `q=silva` aplica uma busca, `pagina=2` solicita uma página e `limite=15` pede até 15 registros. Esses nomes são apenas um exemplo genérico; devem corresponder aos parâmetros aceitos pelo endpoint.

### Exemplo genérico de resposta

```json
{
  "dados": [
    { "id": 16, "nome": "Ana Silva", "email": "ana@email.com" },
    { "id": 17, "nome": "Carlos Silva", "email": "carlos@email.com" }
  ],
  "paginacao": {
    "pagina_atual": 2,
    "limite_por_pagina": 15,
    "total_registros": 45,
    "total_paginas": 3,
    "tem_proxima": true,
    "tem_anterior": true
  }
}
```

Os metadados informam ao cliente a página atual, o total de resultados e se há páginas anteriores ou seguintes.

## 6. Paginação com `Pageable` no Spring

No Spring Data, o parâmetro `Pageable` reúne as informações de paginação e ordenação. O Spring o preenche automaticamente a partir dos query parameters da URL; não é necessário criar um parâmetro separado para cada campo.

Os nomes padrão são:

- `page`: índice da página, começando em **0**. Portanto, `page=0` é a primeira página e `page=1` é a segunda.
- `size`: quantidade de itens solicitada por página.
- `sort`: campo e direção da ordenação, no formato `sort=campo,direcao`, como `sort=firstName,asc`.

Exemplos para o endpoint `/person/v2`:

```text
GET /person/v2
GET /person/v2?page=0&size=12
GET /person/v2?page=1&size=12&sort=firstName,asc
```

No método apresentado, `Pageable pageable` recebe esses valores e é enviado ao serviço. Quando os parâmetros não são informados, este endpoint retorna **20 registros por padrão**, conforme observado na chamada. Cada requisição retorna somente a página solicitada, não todos os registros de uma vez.

O `PagedResourcesAssembler` converte o resultado paginado em um `PagedModel`. Além dos itens e dos metadados, o modelo inclui links HATEOAS de navegação, como `self`, `first`, `prev`, `next` e `last`, quando aplicáveis. Assim, o cliente pode seguir os links para navegar pelas páginas.

Exemplo simplificado de uso:

```java
public ResponseEntity<PagedModel<PersonDTOV2>> findAll(Pageable pageable) {
    Page<PersonDTOV2> page = service.findAll(pageable);
    PagedModel<PersonDTOV2> model =
        pagedAssembler.toModel(page, assemblerV2::addLinks);
    return ResponseEntity.ok(model);
}
```

Nesse exemplo, o serviço busca a página solicitada; o assembler monta a resposta paginada e os links. O `assemblerV2::addLinks` também pode adicionar links aos itens individuais.

## 7. Testando e simulando APIs com Mockaroo

O **Mockaroo** (`mockaroo.com`) gera dados fictícios estruturados em formatos como JSON, CSV ou SQL. Ele pode ajudar a criar conjuntos de dados para testar interfaces e fluxos de busca antes de o backend estar pronto.

1. **Definição do esquema:** configure campos como `id`, `first_name`, `email` e `department`.
2. **Geração de dados:** exporte registros para testar listas maiores.
3. **Simulação:** combine os dados gerados com ferramentas locais, como JSON Server, ou com scripts que implementem parâmetros como página, limite e busca.

## 8. Boas práticas

1. **Defina padrões sensatos:** se o cliente não enviar os parâmetros, o backend deve escolher valores padrão. Neste endpoint, o padrão observado é 20 registros por página.
2. **Estabeleça um tamanho máximo:** limite a quantidade de itens que pode ser solicitada em uma única página para evitar respostas excessivamente grandes.
3. **Considere paginação por cursor:** para feeds contínuos ou dados que mudam frequentemente, cursores baseados em IDs ou timestamps podem evitar que inserções alterem a navegação por páginas.
