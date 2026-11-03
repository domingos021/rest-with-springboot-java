# Docker - Guia Completo

> Este documento serve como material de estudo e referência durante o desenvolvimento de projetos.
>
> Sempre que um novo comando, conceito ou recurso for adicionado, ele deverá seguir exatamente a estrutura abaixo.

---

# Padrão da documentação

Todo comando ou conceito deve responder às seguintes perguntas:

## 1. O que é?
Explique o conceito de forma clara e objetiva.

Exemplo:

"O comando `docker run` é responsável por criar e iniciar um novo container a partir de uma imagem."

---

## 2. Para que serve?
Explique o objetivo do comando ou recurso.

Exemplo:

"Permite executar aplicações dentro de containers de forma isolada."

---

## 3. O que acontece internamente?
Explique o funcionamento "por baixo dos panos".

Exemplo:

"Ao executar `docker run`, o Docker verifica se a imagem existe localmente. Caso não exista, faz o download da imagem, cria um novo container, configura o ambiente e inicia sua execução."

---

## 4. Quando devo utilizar?
Explique em quais situações esse comando é indicado.

Exemplo:

- Criar novos containers.
- Testar imagens.
- Executar bancos de dados.
- Executar aplicações.

---

## 5. Quando NÃO devo utilizar?
Explique quando existe um comando mais adequado.

Exemplo:

"Não utilize `docker run` para iniciar um container que já existe. Nesse caso utilize `docker start`."

---

## 6. Sintaxe

Mostre a sintaxe genérica.

Exemplo:

```bash
docker run [OPÇÕES] IMAGEM
```

---

## 7. Parâmetros

Explique cada parâmetro utilizado.

| Parâmetro | Descrição |
|-----------|-----------|
| -d | Executa em segundo plano. |
| -p | Mapeia portas. |
| -e | Define variáveis de ambiente. |
| --name | Define um nome para o container. |

---

## 8. Exemplo básico

```bash
docker run nginx
```

Explique linha por linha o que aconteceu.

---

## 9. Exemplo profissional

```bash
docker run \
-d \
--name postgres-db \
-p 5432:5432 \
-e POSTGRES_PASSWORD=123456 \
postgres
```

Depois explique cada parâmetro individualmente.

---

## 10. Resultado esperado

Mostre a saída esperada.

Exemplo:

```text
Unable to find image 'postgres:latest' locally

Pulling from library/postgres

Digest: sha256:...

Status: Downloaded newer image

a91f...
```

Explique o significado de cada linha.

---

## 11. Fluxo de execução

Mostre visualmente o que acontece.

```text
docker run

↓

Procura imagem

↓

Imagem encontrada?

↓

SIM
↓

Cria container

↓

Inicializa container

↓

Aplicação executando
```

---

## 12. Observações importantes

Liste informações que costumam gerar dúvidas.

Exemplo:

- `docker run` sempre cria um novo container.
- `docker start` inicia um container já existente.
- Se a imagem não existir localmente, ela será baixada automaticamente.

---

## 13. Boas práticas

Explique como utilizar corretamente.

Exemplo:

✔ Dar nomes aos containers.

✔ Utilizar volumes para bancos de dados.

✔ Não executar aplicações importantes sem persistência.

✔ Evitar utilizar o usuário root quando possível.

---

## 14. Erros comuns

Mostre erros frequentes.

Exemplo:

### Erro

```text
Cannot connect to the Docker daemon
```

### Causa

Docker Desktop não está em execução.

### Solução

Abrir o Docker Desktop.

---

## 15. Resumo

Resumo em poucas linhas.

Exemplo:

- Cria containers.
- Pode baixar imagens automaticamente.
- Pode iniciar aplicações.
- É um dos comandos mais importantes do Docker.
