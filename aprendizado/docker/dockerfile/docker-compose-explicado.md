# docker-compose.yml explicado: Spring Boot + PostgreSQL

> Guia para entender **cada chave** do `docker-compose.yml`: o que faz, por que existe e como se conecta com o Dockerfile, com o banco e com o projeto local.
>
> Pré-requisito: o guia `dockerfile-explicado.md` (o compose usa aquele Dockerfile).

---

## Índice

1. [O que é o Docker Compose e por que usar](#1-o-que-é-o-docker-compose)
2. [Visão geral da arquitetura](#2-visão-geral-da-arquitetura)
3. [O arquivo completo (resumido)](#3-o-arquivo-completo-resumido)
4. [Estrutura e sintaxe YAML](#4-estrutura-e-sintaxe-yaml)
5. [Serviço `db` (PostgreSQL) linha a linha](#5-serviço-db-postgresql)
6. [Serviço `app` (Spring Boot) linha a linha](#6-serviço-app-spring-boot)
7. [Seção `volumes` (nível raiz)](#7-seção-volumes-nível-raiz)
8. [O arquivo `.env`](#8-o-arquivo-env)
9. [Como tudo se conecta: o fluxo completo](#9-como-tudo-se-conecta)
10. [Comandos do dia a dia](#10-comandos-do-dia-a-dia)
11. [Outras configurações muito usadas](#11-outras-configurações-muito-usadas)
12. [Erros comuns e soluções](#12-erros-comuns-e-soluções)
13. [Resumo rápido](#13-resumo-rápido)

---

## 1. O que é o Docker Compose

Com só o Dockerfile você consegue construir **uma** imagem e rodar **um** container. Mas o seu sistema tem **dois** componentes que precisam trabalhar juntos:

- a **aplicação** Spring Boot;
- o **banco de dados** PostgreSQL.

Subir cada um na mão exigiria vários comandos longos (`docker network create`, `docker run -p ... -e ... --network ...`). O **Docker Compose** resolve isso: você descreve **tudo num arquivo YAML** e sobe com **um único comando**:

```bash
docker compose up --build
```

Analogia: o Dockerfile é a **receita de um prato**. O compose é o **cardápio completo do jantar**: diz quais pratos servir, em que ordem e como eles se relacionam na mesa.

---

## 2. Visão geral da arquitetura

```
 Seu computador (host)
 ┌──────────────────────────────────────────────────────────┐
 │                                                          │
 │   navegador ──► localhost:8080                           │
 │                      │  (ports: "8080:8080")             │
 │   ┌──────────────────▼────────────── rede interna ─────┐ │
 │   │                                                    │ │
 │   │  ┌───────────────┐   jdbc:...//db:5432   ┌───────┐ │ │
 │   │  │ app           │ ────────────────────► │ db    │ │ │
 │   │  │ Spring Boot   │                       │ Postgres│ │
 │   │  │ porta 8080    │                       │ 5432  │ │ │
 │   │  └───────────────┘                       └───┬───┘ │ │
 │   └──────────────────────────────────────────────┼─────┘ │
 │                                                  │       │
 │                          volume postgres_data ◄──┘       │
 │                          (dados sobrevivem)              │
 └──────────────────────────────────────────────────────────┘
```

Pontos centrais:

- O **compose cria uma rede privada** automaticamente. Os serviços se enxergam **pelo nome**.
- Só a porta **8080 da app** é aberta para o seu computador. O banco fica protegido dentro da rede.
- Os dados do banco ficam num **volume**, fora do container.

---

## 3. O arquivo completo (resumido)

```yaml
services:
  db:
    image: postgres:16
    environment:
      POSTGRES_DB: rest_with_springboot_java
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d rest_with_springboot_java"]
      interval: 5s
      timeout: 5s
      retries: 10
      start_period: 10s

  app:
    build: .
    image: domingosjovete/curso_spring_boot:1.1
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:postgresql://db:5432/rest_with_springboot_java
      DB_USER: postgres
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      db:
        condition: service_healthy

volumes:
  postgres_data:
    external: true
```

---

## 4. Estrutura e sintaxe YAML

### Regras do YAML

- **A indentação é a estrutura.** Use **espaços** (nunca tab). Padrão: 2 espaços por nível.
- `chave: valor` define uma propriedade.
- `- item` (hífen) define um **item de lista**.
- `#` inicia um comentário.

### Os blocos principais do compose

```yaml
services:   # os containers que formam o sistema
  db: ...
  app: ...

volumes:    # volumes nomeados usados pelos serviços
  ...

# (opcional) networks:  redes personalizadas
```

> **Dica:** antigamente havia uma linha `version: "3.8"` no topo. Hoje ela é **obsoleta**: o Docker Compose moderno a ignora. O seu arquivo não tem, e está certo.

---

## 5. Serviço `db` (PostgreSQL)

```yaml
db:
```

O nome `db` é **muito importante**: ele vira o **hostname** do banco na rede interna. A aplicação vai se conectar usando exatamente `db`.

### `image: postgres:16`

- Usa uma **imagem pronta** do Docker Hub: PostgreSQL versão 16.
- Diferente da `app`, aqui **não há Dockerfile**: você não precisa construir nada, só baixar.
- Formato `nome:tag`. Fixar o `16` evita que uma atualização automática quebre o banco.

### `environment:`

Variáveis de ambiente passadas ao container. A imagem do PostgreSQL as lê **na primeira inicialização** para criar o banco.

| Variável | O que faz |
|---|---|
| `POSTGRES_DB` | Nome do banco criado automaticamente (`rest_with_springboot_java`) |
| `POSTGRES_USER` | Usuário administrador (`postgres`) |
| `POSTGRES_PASSWORD` | Senha do usuário, lida do `.env` pela sintaxe `${DB_PASSWORD}` |

> ⚠️ **Detalhe importante:** essas variáveis só valem quando o diretório de dados está **vazio** (primeira vez). Se o volume já tem dados, mudar `POSTGRES_PASSWORD` depois **não altera** a senha do banco existente. É uma dúvida muito comum.

### `volumes:`

```yaml
volumes:
  - postgres_data:/var/lib/postgresql/data
```

Formato: `NOME_DO_VOLUME:CAMINHO_NO_CONTAINER`

- `postgres_data`: volume gerenciado pelo Docker (declarado na seção raiz).
- `/var/lib/postgresql/data`: pasta onde o PostgreSQL guarda os dados dentro do container.

**Por que existe?** Containers são **descartáveis**. Sem volume, apagar o container apagaria todos os seus dados. Com o volume, os dados ficam **fora** do ciclo de vida do container.

```
container db ──(apagado)──► ✗
volume postgres_data ──────► continua intacto ✓
```

### `healthcheck:`

O Docker pergunta periodicamente: "esse container está saudável?". Aqui, "saudável" = o PostgreSQL **aceita conexões**.

```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U postgres -d rest_with_springboot_java"]
  interval: 5s
  timeout: 5s
  retries: 10
  start_period: 10s
```

| Chave | Significado |
|---|---|
| `test` | Comando executado **dentro** do container. Se retornar 0 = saudável |
| `CMD-SHELL` | Roda o comando por um shell (`/bin/sh -c`) |
| `pg_isready` | Utilitário do PostgreSQL que verifica se o servidor aceita conexões |
| `-U postgres -d ...` | Usuário e banco a testar |
| `interval: 5s` | Repete o teste a cada 5 segundos |
| `timeout: 5s` | Se o teste demorar mais que 5s, conta como falha |
| `retries: 10` | Após 10 falhas seguidas, marca como `unhealthy` |
| `start_period: 10s` | Período de carência inicial: falhas nesses 10s **não contam** como erro |

**Por que isso é necessário?** Um container "rodando" não significa que o banco está **pronto**. O PostgreSQL leva alguns segundos para inicializar. O healthcheck separa "processo iniciado" de "pronto para uso".

Estados possíveis: `starting` → `healthy` (ou `unhealthy`).

---

## 6. Serviço `app` (Spring Boot)

### `build: .`

- Diz ao Compose: "construa a imagem **usando o Dockerfile da pasta atual**" (`.` é o contexto de build).
- **É aqui que o compose se conecta ao Dockerfile do guia anterior.**
- Equivale a rodar `docker build .` automaticamente.

Variação com mais controle:

```yaml
build:
  context: .              # pasta enviada ao Docker
  dockerfile: Dockerfile  # nome do arquivo (útil se for outro nome)
```

### `image: domingosjovete/curso_spring_boot:1.1`

- Quando `build` e `image` aparecem **juntos**, o Compose **constrói** a imagem e **dá este nome/tag** a ela.
- O formato `usuario/repositorio:tag` é o padrão do Docker Hub. Assim você pode fazer `docker push` dessa imagem depois.
- A tag `1.1` evita confundir com versões anteriores (como a `1.0`).

### `ports:`

```yaml
ports:
  - "8080:8080"
```

Formato: `"PORTA_DO_SEU_PC:PORTA_DO_CONTAINER"`

- Abre a porta 8080 do seu computador e **redireciona** para a 8080 do container.
- **É isto que torna a app acessível** em `http://localhost:8080`.
- Lembra do `EXPOSE 8080` do Dockerfile? Ele só **documentava**. O `ports:` é quem **publica de verdade**.

Exemplos de variação:

```yaml
- "9090:8080"   # acessa em localhost:9090, container continua na 8080
```

> As aspas são recomendadas: o YAML pode interpretar números com `:` de formas inesperadas.

### `environment:`

```yaml
environment:
  DB_URL: jdbc:postgresql://db:5432/rest_with_springboot_java
  DB_USER: postgres
  DB_PASSWORD: ${DB_PASSWORD}
```

Variáveis que a aplicação lê para saber como falar com o banco. Elas conectam com o `application.properties`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

Desmontando a URL:

```
jdbc:postgresql://db:5432/rest_with_springboot_java
│    │            │  │    └─ nome do banco (= POSTGRES_DB)
│    │            │  └────── porta do PostgreSQL dentro da rede
│    │            └───────── HOSTNAME = nome do serviço "db"
│    └────────────────────── driver/tipo de banco
└─────────────────────────── protocolo JDBC (Java)
```

> ⭐ **O ponto mais importante do compose:** dentro de um container, `localhost` significa **o próprio container**, e não o seu PC nem outro container. Para a `app` achar o banco, ela usa o **nome do serviço** (`db`). O Compose cria um DNS interno que resolve `db` para o IP do container do banco.

Na sua máquina (fora do Docker) você usaria `localhost`. Dentro do Compose, usa `db`. Por isso as variáveis vêm de fora: o mesmo código funciona nos dois cenários.

### `depends_on:`

```yaml
depends_on:
  db:
    condition: service_healthy
```

- Define **ordem de inicialização**: a `app` só sobe **depois** do `db`.
- `condition: service_healthy` é o diferencial: espera o **healthcheck passar**, não apenas o container iniciar.

Comparação:

| Forma | Comportamento |
|---|---|
| `depends_on: [db]` | Espera o container `db` **iniciar** (o banco ainda pode estar carregando) |
| `condition: service_healthy` | Espera o banco estar **pronto de fato** ✅ |

**Por que importa aqui:** o Spring Boot executa o **Flyway** (migrations) logo na subida e precisa de conexão imediata. Se o banco não estiver pronto, a app falha com erro de conexão.

```
tempo ─────────────────────────────────────────────►
db:   [iniciando....][healthcheck ok ✓]
app:                 [só agora começa a subir]
```

---

## 7. Seção `volumes` (nível raiz)

```yaml
volumes:
  postgres_data:
    external: true
```

Todo volume **usado** num serviço precisa ser **declarado** aqui.

### O que `external: true` significa

- O volume **já existe**, foi criado fora do Compose.
- O Compose **não o cria nem o remove**. Só o usa.
- Se o volume **não existir**, o `up` dá erro. Crie com:

```bash
docker volume create postgres_data
```

Comparação:

| Declaração | Quem cria | `docker compose down -v` remove? |
|---|---|---|
| `postgres_data:` (sem external) | O Compose, no primeiro `up` | **Sim** (apaga seus dados!) |
| `external: true` | Você, manualmente | **Não** (mais seguro) |

Usar `external: true` é uma proteção: nem um `down -v` distraído apaga o banco.

---

## 8. O arquivo `.env`

O compose usa `${DB_PASSWORD}` em dois lugares. De onde vem o valor? De um arquivo chamado **`.env`**, **na mesma pasta do `docker-compose.yml`**, lido automaticamente.

```env
DB_PASSWORD=minha_senha_forte
```

Estrutura do projeto:

```
meu-projeto/
├── .env                  ← senhas (NÃO vai pro Git)
├── .gitignore            ← contém ".env"
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── src/
```

### Por que usar `.env`

- A senha **não fica escrita** dentro do `docker-compose.yml`.
- Você pode versionar o compose no Git **sem vazar segredo**.
- Cada ambiente (seu PC, servidor) tem o seu próprio `.env`.

### Regras de segurança

1. Coloque `.env` no `.gitignore`.
2. Crie um **`.env.example`** (sem valores reais) para documentar quais variáveis existem:
   ```env
   DB_PASSWORD=troque_esta_senha
   ```
3. Se `DB_PASSWORD` não estiver definido, o Compose avisa que a variável está em branco.

Valor padrão e variável obrigatória:

```yaml
${DB_PASSWORD:-padrao}                       # usa "padrao" se não existir
${DB_PASSWORD:?DB_PASSWORD é obrigatória}    # dá erro se não existir
```

---

## 9. Como tudo se conecta

O que acontece ao rodar `docker compose up --build`, passo a passo:

```
1. Compose lê docker-compose.yml e o .env (substitui ${DB_PASSWORD})
2. Cria a rede interna do projeto
3. Verifica se o volume externo postgres_data existe
4. Constrói a imagem da app  ──► executa o Dockerfile (2 estágios, gera o JAR)
5. Sobe o container "db"     ──► PostgreSQL inicia, monta o volume
6. Healthcheck roda a cada 5s ──► pg_isready... ... ✓ healthy
7. Só então sobe o container "app"
8. app executa: java -jar app.jar   (ENTRYPOINT do Dockerfile)
9. Spring lê DB_URL/DB_USER/DB_PASSWORD, conecta em db:5432
10. Flyway aplica as migrations no banco
11. Você acessa http://localhost:8080 ✓
```

Mapa de conexões entre os arquivos:

| Onde | Se conecta com | Como |
|---|---|---|
| `build: .` (compose) | `Dockerfile` | O compose executa o Dockerfile |
| `ports: 8080:8080` | `EXPOSE 8080` | Publica a porta que o Dockerfile documenta |
| `DB_URL ... db:5432` | serviço `db` | Nome do serviço = hostname na rede |
| `DB_URL ... /rest_with_...` | `POSTGRES_DB` | Mesmo nome de banco |
| `DB_USER` / `DB_PASSWORD` | `POSTGRES_USER` / `POSTGRES_PASSWORD` | Mesmas credenciais |
| `DB_*` (compose) | `application.properties` | Spring lê as variáveis de ambiente |
| `depends_on` | `healthcheck` do `db` | Espera o banco ficar saudável |
| `postgres_data` | volume externo | Dados persistem fora do container |

---

## 10. Comandos do dia a dia

### Subir e derrubar

```bash
docker compose up --build        # constrói e sobe (mostra logs no terminal)
docker compose up -d             # sobe em segundo plano (detached)
docker compose up --build -d     # constrói + segundo plano
docker compose down              # para e remove containers e rede
docker compose down -v           # idem + remove volumes não-externos (cuidado!)
docker compose stop              # só para (não remove)
docker compose start             # inicia containers parados
docker compose restart app       # reinicia só um serviço
```

### Ver o que está acontecendo

```bash
docker compose ps                # status dos serviços (e health)
docker compose logs              # logs de todos
docker compose logs -f app       # acompanha logs da app em tempo real
docker compose logs --tail=100 db
docker compose config            # mostra o compose final, com ${} já substituídos
```

Saída típica do `ps`:

```
NAME      SERVICE   STATUS                    PORTS
proj-db   db        Up 2 minutes (healthy)    5432/tcp
proj-app  app       Up 2 minutes              0.0.0.0:8080->8080/tcp
```

### Entrar nos containers

```bash
docker compose exec app sh                                   # shell na app
docker compose exec db psql -U postgres -d rest_with_springboot_java   # SQL direto no banco
```

Dentro do `psql`:

```sql
\dt                        -- lista tabelas
SELECT * FROM flyway_schema_history;   -- migrations aplicadas
\q                         -- sair
```

### Rebuild e atualização

```bash
docker compose build             # só reconstrói as imagens
docker compose build --no-cache  # reconstrói ignorando o cache
docker compose up -d --build app # reconstrói e recria só a app
docker compose pull              # baixa versões novas das imagens (ex.: postgres)
```

### Volumes, imagens, rede

```bash
docker volume ls                       # lista volumes
docker volume inspect postgres_data    # detalhes (onde está no disco)
docker volume create postgres_data     # cria o volume externo
docker images                          # imagens locais
docker network ls                      # redes
docker ps -a                           # todos os containers (inclusive parados)
docker system prune                    # limpa containers/imagens/redes sem uso
```

### Fluxo típico de desenvolvimento

```bash
# 1ª vez
docker volume create postgres_data
docker compose up --build -d

# alterou o código Java
docker compose up -d --build app
docker compose logs -f app

# fim do dia
docker compose stop
```

---

## 11. Outras configurações muito usadas

Não estão no seu arquivo, mas são comuns.

### `restart:` reinicia automaticamente

```yaml
app:
  restart: unless-stopped
```

| Valor | Comportamento |
|---|---|
| `no` | Nunca reinicia (padrão) |
| `always` | Sempre reinicia |
| `on-failure` | Só se sair com erro |
| `unless-stopped` | Reinicia, exceto se você parou manualmente |

### `ports:` no banco para acessar de fora

Hoje o banco **não está acessível** do seu PC (só dentro da rede). Para conectar com DBeaver/pgAdmin:

```yaml
db:
  ports:
    - "5432:5432"
```

Ou, mais seguro, só para a sua máquina:

```yaml
    - "127.0.0.1:5432:5432"
```

Se já tiver um PostgreSQL local usando 5432, mude o lado esquerdo: `"5433:5432"`.

### `env_file:` carrega várias variáveis de um arquivo

```yaml
app:
  env_file:
    - .env
```

Diferença: `${VAR}` no YAML usa o `.env` para **substituir no próprio compose**; `env_file` **injeta todas** as variáveis do arquivo dentro do container.

### `container_name:` nome fixo

```yaml
db:
  container_name: meu-postgres
```

Útil, mas impede escalar o serviço (`--scale`) e pode gerar conflito de nomes.

### `healthcheck` na app (Spring Actuator)

```yaml
app:
  healthcheck:
    test: ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
    interval: 10s
    retries: 5
```

Exige a dependência `spring-boot-starter-actuator` e `curl` na imagem.

### `volumes:` com bind mount (pasta do seu PC)

```yaml
db:
  volumes:
    - ./init.sql:/docker-entrypoint-initdb.d/init.sql
```

Formato `./caminho-local:/caminho-no-container`. Scripts em `docker-entrypoint-initdb.d` rodam na primeira inicialização do banco.

### `networks:` redes personalizadas

```yaml
services:
  app:
    networks: [backend]
  db:
    networks: [backend]
networks:
  backend:
```

Útil para isolar grupos de serviços. Sem declarar, o Compose usa uma rede padrão (suficiente para o seu caso).

### `command:` sobrescreve o comando da imagem

```yaml
app:
  command: ["--spring.profiles.active=dev"]
```

### `profiles:` serviços opcionais

```yaml
adminer:
  image: adminer
  profiles: ["tools"]
  ports: ["8081:8080"]
```

```bash
docker compose --profile tools up
```

---

## 12. Erros comuns e soluções

| Sintoma | Causa provável | Solução |
|---|---|---|
| `external volume "postgres_data" not found` | O volume externo não existe | `docker volume create postgres_data` |
| `The "DB_PASSWORD" variable is not set` | `.env` ausente, no lugar errado ou sem a variável | Criar `.env` na **mesma pasta** do compose |
| `password authentication failed` | Senha do `.env` diferente da que o volume já guarda (a senha é gravada na 1ª inicialização) | Usar a senha original, ou recriar o volume (apaga dados) |
| `Connection refused` na app | Usando `localhost` em vez de `db`, ou banco ainda não pronto | `DB_URL` com `db` e `depends_on` com `service_healthy` |
| `port is already allocated` / `address already in use` | Porta 8080 (ou 5432) já em uso no seu PC | Mudar o lado esquerdo: `"8081:8080"` |
| Alterei o código e nada mudou | A imagem não foi reconstruída | `docker compose up --build` |
| Alterei `.env` e nada mudou | Containers antigos continuam rodando | `docker compose up -d` para recriar |
| `yaml: line X: mapping values...` | Indentação errada ou tab no arquivo | Usar espaços; validar com `docker compose config` |
| Banco "sem dados" após `down` | Volume não-externo removido com `-v` | Manter `external: true` e evitar `down -v` |
| Container `unhealthy` | Banco não subiu / healthcheck com usuário ou banco errados | `docker compose logs db` |

---

## 13. Resumo rápido

| Chave | Em uma linha |
|---|---|
| `services` | Lista dos containers do sistema |
| `db` / `app` | Nomes dos serviços (**viram hostnames** na rede interna) |
| `image` | Imagem a usar (ou nome dado à imagem construída) |
| `build: .` | Constrói a imagem com o Dockerfile da pasta |
| `environment` | Variáveis de ambiente do container |
| `${DB_PASSWORD}` | Valor lido do arquivo `.env` |
| `ports` | `"PC:container"`: torna o serviço acessível de fora |
| `volumes` (no serviço) | Liga um volume/pasta a um caminho do container |
| `healthcheck` | Teste para saber se o serviço está **pronto** |
| `depends_on` + `service_healthy` | Ordem de subida, esperando o banco ficar saudável |
| `volumes` (raiz) | Declara os volumes usados |
| `external: true` | O volume já existe e o Compose não o gerencia |

---

### Comando mais importante de tudo

```bash
docker compose up --build
```

Ele constrói a imagem pelo **Dockerfile**, sobe o **PostgreSQL**, espera ficar **saudável**, sobe o **Spring Boot**, e você acessa em `http://localhost:8080`. 🐳
