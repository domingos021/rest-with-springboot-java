# Docker no projeto `rest-with-springboot-java`

Projeto: API REST com Spring Boot 3.4, Java 21, PostgreSQL e Flyway.

> Rode todos os comandos no terminal, **na raiz do projeto** (a pasta do `pom.xml`).

## Sumário

1. [O que é o Docker](#1-o-que-é-o-docker)
2. [Por que usar o Docker](#2-por-que-usar-o-docker)
3. [Conceitos essenciais](#3-conceitos-essenciais)
4. [Container não é máquina virtual](#4-container-não-é-máquina-virtual)
5. [Quando o Docker não compensa](#5-quando-o-docker-não-compensa)
6. [Passo a passo: dockerizar o projeto do zero](#6-passo-a-passo-dockerizar-o-projeto-do-zero)
7. [Passo a passo: rodar em outra máquina](#7-passo-a-passo-rodar-em-outra-máquina)
8. [Comandos principais](#8-comandos-principais)
9. [Quando usar cada comando](#9-quando-usar-cada-comando)
10. [Pontos importantes](#10-pontos-importantes)
11. [Acessos, limpeza e banco](#11-acessos-limpeza-e-banco)

---

## 1. O que é o Docker

Docker é uma ferramenta que **empacota uma aplicação junto com tudo o que ela precisa para rodar** (Java, bibliotecas, configurações) em uma unidade chamada **container**.

Esse container se comporta de forma igual em qualquer computador que tenha o Docker instalado: no seu notebook, no de um colega ou em um servidor na nuvem.

Ele resolve o problema clássico do **"na minha máquina funciona"**.

---

## 2. Por que usar o Docker

### O problema sem Docker

Para alguém rodar esta API, precisaria:

- instalar o JDK 21 e o Maven;
- instalar o PostgreSQL, criar o banco, o usuário e a senha;
- ajustar variáveis de ambiente e configurações;
- torcer para as versões instaladas serem compatíveis.

### Com Docker

Basta instalar o Docker e rodar um comando:

```bash
docker compose up --build
```

Em poucos minutos sobem o banco, as migrações do Flyway, a aplicação e o seed, sem instalar mais nada.

### Principais vantagens

- **Ambiente igual em todo lugar:** a versão do Java e do PostgreSQL vem fixa na imagem. Sem surpresas por diferença de versão.
- **Não suja a sua máquina:** o PostgreSQL roda dentro do container. Dá para apagar tudo e recomeçar do zero sem desinstalar nada.
- **Vários serviços com um comando:** o Compose liga aplicação e banco juntos, na ordem certa.
- **Deploy facilitado:** plataformas como Render, Railway e AWS usam o seu `Dockerfile` para montar a mesma imagem que você testou.
- **Trabalho em equipe:** quem clonar o repositório sobe o projeto sem um tutorial de instalação de vinte passos.
- **Bom para portfólio:** mostra que o projeto roda com um único comando.

---

## 3. Conceitos essenciais

| Conceito | O que é | No seu projeto |
|---|---|---|
| **Imagem** | A "receita pronta" e congelada, com a aplicação e suas dependências | `rest-with-springboot-java-app` |
| **Container** | A imagem em execução | `app-1` (aplicação) e `db-1` (banco) |
| **Volume** | Pasta guardada fora do container, para os dados sobreviverem | `pgdata` (dados do PostgreSQL) |
| **Dockerfile** | Arquivo com as instruções para construir uma imagem | `Dockerfile` na raiz |
| **Docker Compose** | Arquivo que descreve vários containers e os sobe juntos | `docker-compose.yml` na raiz |
| **Build em dois estágios** | Um estágio compila o projeto (Maven + JDK), o outro monta a imagem final leve (só JRE + JAR) | `Dockerfile` do projeto |

---

## 4. Container não é máquina virtual

| | Máquina virtual | Container |
|---|---|---|
| Sistema operacional | Carrega um inteiro | Compartilha o núcleo da máquina |
| Peso | Pesada (gigabytes) | Leve (megabytes) |
| Tempo para ligar | Minutos | Segundos |
| Isolamento | Total | Isola só o que a aplicação precisa |

---

## 5. Quando o Docker não compensa

Para programar no dia a dia, rodar a aplicação pelo IntelliJ costuma ser mais rápido, porque não é preciso reconstruir a imagem a cada alteração.

Fluxo comum:

1. **Desenvolver e testar pelo IntelliJ.**
2. **Usar `docker compose up --build`** para validar o projeto empacotado, antes de subir para o GitHub ou fazer deploy.

---

## 6. Passo a passo: dockerizar o projeto do zero

### Passo 1: instalar e abrir o Docker Desktop

Instale o Docker Desktop, abra e espere o status ficar **running**. Confirme no terminal:

```bash
docker --version
```

### Passo 2: criar os 4 arquivos na raiz

Todos ficam **na raiz do projeto, ao lado do `pom.xml`**, e **fora** de `src/`. No IntelliJ: botão direito na pasta raiz → **New → File** e digite o nome exato.

```
meu-projeto/
├── Dockerfile
├── docker-compose.yml
├── .env
├── .dockerignore
├── pom.xml
└── src/
```

#### 2.1 `Dockerfile` (sem extensão)

```dockerfile
# ==========================================================
# ESTÁGIO 1 - BUILD
# Compila o projeto e gera o JAR executável usando o Maven.
# Esse estágio é descartado no final: não vai para a imagem.
# ==========================================================
FROM maven:3.9-eclipse-temurin-21 AS build

# Pasta de trabalho dentro do container
WORKDIR /app

# Copia só o pom.xml primeiro e baixa as dependências.
# Assim o Docker guarda essa etapa em cache e só baixa tudo de novo
# quando o pom.xml mudar (builds seguintes ficam bem mais rápidos).
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e gera o JAR.
# -DskipTests: os testes usam Testcontainers, que precisam de Docker
# e não rodam dentro do build.
COPY src ./src
RUN mvn clean package -DskipTests


# ==========================================================
# ESTÁGIO 2 - EXECUÇÃO
# Imagem final, leve: só o JRE 21 (sem Maven e sem JDK) + o JAR.
# ==========================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado no estágio anterior, renomeando para app.jar
COPY --from=build /app/target/*.jar app.jar

# Documenta a porta usada pela aplicação (padrão do Spring Boot)
EXPOSE 8080

# Comando executado quando o container iniciar: roda a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2.2 `docker-compose.yml`

```yaml
# ==========================================================
# DOCKER COMPOSE
# Descreve os serviços (containers) que formam o sistema e
# sobe todos juntos com um único comando: docker compose up
# ==========================================================
services:

  # ----------------------------------------------------------
  # SERVIÇO 1 - BANCO DE DADOS (PostgreSQL)
  # ----------------------------------------------------------
  db:
    # Imagem oficial do PostgreSQL, versão 16, baixada do Docker Hub
    image: postgres:16

    # Variáveis de ambiente que o PostgreSQL lê na primeira inicialização
    environment:
      # Nome do banco criado automaticamente (o mesmo do seu DB_URL)
      POSTGRES_DB: rest_with_springboot_java
      # Usuário administrador do banco
      POSTGRES_USER: postgres
      # Senha do usuário. O ${DB_PASSWORD} vem do arquivo .env
      POSTGRES_PASSWORD: ${DB_PASSWORD}

    # Volume: guarda os dados do banco FORA do container.
    # Sem ele, os dados sumiriam toda vez que o container fosse apagado.
    volumes:
      - pgdata:/var/lib/postgresql/data

    # Verificação de saúde: testa de 5 em 5 segundos se o PostgreSQL
    # já está pronto para receber conexões (pg_isready).
    # Tenta até 10 vezes antes de considerar que falhou.
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d rest_with_springboot_java"]
      interval: 5s
      timeout: 5s
      retries: 10

  # ----------------------------------------------------------
  # SERVIÇO 2 - APLICAÇÃO (Spring Boot)
  # ----------------------------------------------------------
  app:
    # Constrói a imagem a partir do Dockerfile na pasta atual (.)
    build: .

    # Mapeia portas: "porta do seu PC : porta dentro do container".
    # Por isso você acessa em http://localhost:8080
    ports:
      - "8080:8080"

    # Variáveis que o application.properties lê (DB_URL, DB_USER, DB_PASSWORD).
    # Note o "db" no lugar de "localhost": dentro do Compose, o nome
    # do serviço funciona como o endereço do banco na rede interna.
    environment:
      DB_URL: jdbc:postgresql://db:5432/rest_with_springboot_java
      DB_USER: postgres
      DB_PASSWORD: ${DB_PASSWORD}

    # Só inicia a aplicação depois que o banco estiver SAUDÁVEL
    # (healthcheck passou). Evita o erro de conexão na subida.
    depends_on:
      db:
        condition: service_healthy

# ==========================================================
# VOLUMES
# Declara o volume "pgdata" usado pelo banco. O Docker cuida
# de onde ele fica guardado na sua máquina.
# ==========================================================
volumes:
  pgdata:
```

#### 2.3 `.env`

```
# ==========================================================
# VARIÁVEIS DE AMBIENTE (arquivo .env)
# Lido automaticamente pelo Docker Compose, na mesma pasta do
# docker-compose.yml. Guarda valores sensíveis fora do código.
# NUNCA suba este arquivo para o Git.
# ==========================================================

# Senha do PostgreSQL. É usada em dois lugares do compose:
# - no serviço "db", para criar o usuário postgres com essa senha
# - no serviço "app", para a aplicação se conectar ao banco
DB_PASSWORD=troque-por-uma-senha
```

Troque pela senha que quiser. Sem aspas, sem espaços ao redor do `=` e evitando os símbolos `$`, `#` e `"`.

#### 2.4 `.dockerignore`

```
# Pasta gerada pelo Maven na sua máquina. O Docker gera a dele
# dentro do build, então a sua só atrapalharia.
target/

# Histórico do Git: pesado e desnecessário dentro da imagem
.git/

# Configurações do IntelliJ: só servem para a sua IDE
.idea/

# Segredos (senha do banco): nunca devem entrar na imagem
.env

# Arquivos de log
*.log
```

Se o projeto usa Git, adicione também `.env` ao `.gitignore`.

### Passo 3: subir o projeto

```bash
docker compose up --build
```

A primeira vez leva alguns minutos (baixa as imagens e as dependências do Maven). O passo `mvn dependency:go-offline` é o mais demorado. Nos próximos builds ele fica em cache.

### Passo 4: confirmar que deu certo

No log devem aparecer:

- as mensagens do Flyway aplicando as migrações;
- uma linha com `Started StartUp in ... seconds`.

### Passo 5: testar no navegador

Abra `http://localhost:8080/swagger-ui.html`. Com o perfil `dev`, o seed popula pessoas, livros e pedidos de teste.

### Passo 6: acompanhar pelo Docker Desktop

- **Containers → `rest-with-springboot-java`:** devem aparecer `app-1` e `db-1` como **Running**.
- Na coluna de portas, o link `8080:8080` abre a aplicação.
- Aba **Logs:** mostra o mesmo texto do terminal.
- Aba **Exec** do `db-1`: abre um terminal dentro do banco.

### Passo 7: parar

`Ctrl + C` no terminal e depois:

```bash
docker compose down
```

---

## 7. Passo a passo: rodar em outra máquina

**Só a imagem não basta.** A imagem `rest-with-springboot-java-app` contém apenas a aplicação. O banco é outro container (`postgres:16`), e a senha e a conexão vêm do `docker-compose.yml` e do `.env`.

Em qualquer das opções, a outra máquina precisa ter o **Docker instalado**.

### Opção 1: levar o projeto inteiro (a mais simples)

1. Instale o Docker Desktop na outra máquina e abra.
2. Copie o projeto (`git clone`, pen drive, etc.).
3. Crie o `.env` na raiz com a `DB_PASSWORD` (ele não vai pelo Git, porque está no `.gitignore`).
4. No terminal, na raiz do projeto:

```bash
docker compose up --build
```

5. Abra `http://localhost:8080/swagger-ui.html`.

Precisa de internet na primeira vez.

### Opção 2: levar a imagem pronta como arquivo (sem o código-fonte)

**Na sua máquina:**

1. Exporte as imagens para arquivos:

```bash
docker save -o app.tar rest-with-springboot-java-app
docker save -o postgres.tar postgres:16
```

2. Leve `app.tar`, `postgres.tar`, o `docker-compose.yml` e o `.env` para a outra máquina.

**Na outra máquina:**

3. Importe as imagens:

```bash
docker load -i app.tar
docker load -i postgres.tar
```

4. No `docker-compose.yml`, no serviço `app`, **troque** `build: .` por:

```yaml
    image: rest-with-springboot-java-app
```

(senão o Compose tentará construir a partir de um código que não existe lá).

5. Suba:

```bash
docker compose up
```

Dessa forma não precisa de internet.

### Opção 3: publicar no Docker Hub (o jeito profissional)

**Na sua máquina:**

1. Crie uma conta em hub.docker.com e faça login:

```bash
docker login
```

2. Dê um nome com seu usuário e envie:

```bash
docker tag rest-with-springboot-java-app seuusuario/rest-springboot:1.0
docker push seuusuario/rest-springboot:1.0
```

**Na outra máquina:**

3. Leve só o `docker-compose.yml` e o `.env`.
4. No `docker-compose.yml`, troque `build: .` por:

```yaml
    image: seuusuario/rest-springboot:1.0
```

5. Suba (ele baixa as imagens sozinho):

```bash
docker compose up
```

Atenção: em repositório **público**, qualquer pessoa pode baixar a imagem com a sua aplicação compilada. A senha do banco **não** está na imagem (ela vem do `.env`), o que é o correto. Se preferir, crie o repositório como privado.

### Qual escolher

| Quero... | Use |
|---|---|
| Rodar em outro PC meu ou de um colega | Opção 1 |
| Rodar em máquina sem internet e sem o código | Opção 2 |
| Publicar e baixar de qualquer lugar | Opção 3 |
| Fazer deploy em Render, Railway etc. | Enviar o código (a plataforma usa o seu `Dockerfile`) |

---

## 8. Comandos principais

### Subir o projeto

```bash
docker compose up --build      # reconstrói a imagem e sobe tudo
docker compose up              # sobe sem reconstruir
docker compose up -d --build   # reconstrói e sobe em segundo plano
```

### Parar o projeto

```bash
docker compose down            # para tudo, MANTÉM os dados do banco
docker compose down -v         # para tudo e APAGA o banco (volume)
```

### Acompanhar o que está acontecendo

```bash
docker compose ps              # lista os containers e o status
docker compose logs -f app     # log da aplicação
docker compose logs -f db      # log do banco
```

### Reconstruir só a aplicação (sem mexer no banco)

```bash
docker compose up -d --build app
```

### Build do zero, ignorando o cache

```bash
docker compose build --no-cache
docker compose up
```

---

## 9. Quando usar cada comando

O `Dockerfile` compila o código **dentro** do build. Por isso o container só enxerga uma mudança se a imagem for reconstruída.

| O que você fez | O que fazer |
|---|---|
| Alterou classes, controllers, services | `docker compose up --build` |
| Alterou `application.properties` | `docker compose up --build` |
| Adicionou dependência no `pom.xml` | `docker compose up --build` (o download das dependências roda de novo e leva alguns minutos) |
| Não mudou nada, só quer ligar de novo | `docker compose up` |
| Mudou só o `.env` ou o `docker-compose.yml` | `docker compose up` (o Compose recria só o que mudou) |
| Criou ou alterou uma entidade (campo ou tabela nova) | Criar uma **nova migração Flyway** (próxima versão, ex.: `V10__...sql`) e rodar `docker compose up --build` |
| Alterou uma migração **já aplicada** (V1 a V9) | O Flyway recusa (checksum mudou). Em desenvolvimento: `docker compose down -v` e subir de novo |
| Quer o banco limpo com o seed novo | `docker compose down -v` e depois `docker compose up --build` |
| Mudou a senha do `.env` depois da primeira subida | `docker compose down -v` (o volume guarda a senha antiga) |

---

## 10. Pontos importantes

- **Banco de dados:** os dados ficam no volume `pgdata`. Só somem com `docker compose down -v`. O `down` simples e o botão de parar do Docker Desktop mantêm tudo.
- **Flyway:** como `spring.jpa.hibernate.ddl-auto=none`, o Hibernate não altera o banco. Toda mudança de estrutura entra por migração Flyway. Nunca edite uma migração antiga em produção, sempre crie uma nova.
- **Perfil:** o padrão é `dev` (roda o seed com dados de teste). Em deploy, defina `SPRING_PROFILES_ACTIVE=prod`.
- **Conexão com o banco:** dentro do Docker, `localhost` aponta para o próprio container. Por isso o Compose usa `db` (nome do serviço) no `DB_URL`.
- **Testes:** o build usa `-DskipTests` porque os testes usam Testcontainers, que precisam do Docker e não rodam dentro do build. Rode os testes pela IDE ou com `mvn test` na sua máquina.
- **Senhas:** o `.env` precisa estar no `.gitignore`. Não deixe senha escrita no `pom.xml` (o `flyway-maven-plugin` tem uma). Use `${env.DB_PASSWORD}`.
- **Porta ocupada:** se a 8080 estiver em uso (por exemplo, o projeto rodando pelo IntelliJ), pare o outro ou troque no compose para `"8081:8080"`.

---

## 11. Acessos, limpeza e banco

### Acessos

| O quê | Endereço |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Aplicação | http://localhost:8080 |

### Limpeza

```bash
docker image prune             # remove imagens antigas não usadas
docker compose down -v         # remove containers, rede e o volume do banco
```

### Entrar no banco pelo terminal

```bash
docker compose exec db psql -U postgres -d rest_with_springboot_java
```

Comandos úteis dentro do `psql`: `\dt` (lista as tabelas), `\d person` (estrutura de uma tabela), `\q` (sair).
