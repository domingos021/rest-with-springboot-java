# Guia Completo: O Verbo HTTP PATCH

O verbo **`PATCH`** é um dos métodos da arquitetura RESTful (HTTP/1.1) projetado especificamente para realizar **atualizações parciais** em um recurso. Enquanto outros métodos como `PUT` substituem o recurso inteiro, o `PATCH` altera apenas os campos que você enviar na requisição.

---

## 1. O Conceito do Verbo PATCH

O `PATCH` aplica modificações parciais a um recurso existente. Ele foi introduzido oficialmente pela RFC 5789 para resolver um problema comum: a necessidade de atualizar apenas um ou dois atributos de um registro grande sem precisar enviar (e processar) todos os outros dados inalterados.

* **Idempotência (com ressalvas):** Por definição padrão, requisições `PATCH` **não** são necessariamente idempotentes (embora possam ser implementadas para ser). Se você enviar um incremento (ex: `adicionar 1 ao contador`), repeti-la produzirá um resultado diferente.
* **Corpo da Requisição:** O payload geralmente contém um conjunto de instruções ou um objeto JSON contendo apenas os campos a serem modificados.

---

## 2. Quando Usar o PATCH?

Você deve utilizar o verbo `PATCH` nos seguintes cenários:

1. **Atualizações Parciais de Cadastro:** Quando o usuário altera apenas o seu endereço de e-mail ou número de telefone em um perfil de usuário, sem mexer no nome, senha ou histórico.
2. **Economia de Banda e Processamento:** Em aplicações móveis ou redes lentas, enviar apenas o campo alterado reduz drasticamente o tráfego de dados.
3. **Evitar Conflitos de Concorrência (Overwriting):** Quando múltiplos clientes atualizam partes diferentes do mesmo recurso simultaneamente, o `PATCH` reduz o risco de um cliente sobrescrever acidentalmente as alterações do outro.

### Exemplo de Requisição `PATCH`
```http
PATCH /api/users/42 HTTP/1.1
Host: api.exemplo.com
Content-Type: application/json

{
  "email": "novo.email@exemplo.com"
}
```

---

## 3. Quando NÃO Usar o PATCH?

O uso do `PATCH` é inadequado nas seguintes situações:

1. **Substituição Completa do Recurso:** Se a intenção é substituir todo o objeto ou recriá-lo do zero, o verbo correto é `PUT`.
2. **Criação de Novos Recursos:** Se o recurso não existe, o `PATCH` geralmente deve retornar um erro (`404 Not Found`), a menos que a API suporte explicitamente a criação via `UPSERT` (o que é raro para o `PATCH`). Para criar, utilize `POST`.
3. **Complexidade Excessiva de Formatos:** Se o formato de patch exigido (como JSON Patch - RFC 6902) for excessivamente complexo para a equipe ou para os consumidores da API, uma abordagem de atualizar campos via `PUT` parcial (embora menos semântica) às vezes é adotada por simplicidade.

---

## 4. A Importância do PATCH

A adoção correta do `PATCH` traz grandes vantagens para APIs RESTful:

* **Semântica HTTP Correta:** Utilizar o verbo adequado comunica claramente a intenção da requisição tanto para o servidor quanto para intermediários (proxies, caches, gateways).
* **Eficiência e Escalabilidade:** Reduz o consumo de banda de rede e o custo de processamento de banco de dados (evitando consultas e escritas desnecessárias).
* **Manutenibilidade:** Deixa o contrato da API limpo, previsível e intuitivo para desenvolvedores terceiros que consomem o serviço.

---

## 5. Diferença entre PATCH e PUT

A confusão entre `PUT` e `PATCH` é frequente. A tabela abaixo resume as principais distinções:

| Característica | `PUT` | `PATCH` |
| :--- | :--- | :--- |
| **Operação** | Substituição total / Atualização idempotente | Atualização parcial |
| **Payload** | Deve conter o recurso **completo** | Contém **apenas** os campos a serem alterados |
| **Se um campo for omitido** | O campo é removido ou redefinido para o valor padrão | O campo permanece **inalterado** no servidor |
| **Idempotência** | Sim (sempre) | Não necessariamente (depende da implementação) |

### Comparação Prática

Imagine que temos um recurso de usuário com três campos: `id`, `nome` e `email`.

#### Cenário com `PUT` (Substituição Total):
Se você fizer uma requisição `PUT` enviando apenas o e-mail:
```http
PUT /api/users/1
Content-Type: application/json

{
  "email": "novo@exemplo.com"
}
```
**Resultado:** Como o `PUT` substitui o recurso inteiro, o campo `nome` (que não foi enviado) **será apagado ou ficará nulo** no banco de dados.

#### Cenário com `PATCH` (Atualização Parcial):
Se você fizer a mesma requisição usando `PATCH`:
```http
PATCH /api/users/1
Content-Type: application/json

{
  "email": "novo@exemplo.com"
}
```
**Resultado:** O campo `email` é atualizado, e o campo `nome` **é preservado** exatamente como estava.