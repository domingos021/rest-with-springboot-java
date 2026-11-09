# Dockerfile explicado: projeto Java Spring Boot

> Guia para entender **cada linha** do Dockerfile do projeto: o que faz, por que existe e como se conecta com o projeto local e com o Docker.

---

## Índice

1. [O que é um Dockerfile](#1-o-que-é-um-dockerfile)
2. [Conceitos base: imagem, container, camada e contexto](#2-conceitos-base)
3. [Visão geral: build em 2 estágios (multi-stage)](#3-visão-geral-multi-stage)
4. [O Dockerfile completo](#4-o-dockerfile-completo)
5. [Explicação linha a linha](#5-explicação-linha-a-linha)
6. [Como o Dockerfile se conecta ao projeto local](#6-como-o-dockerfile-se-conecta-ao-projeto-local)
7. [Cache de camadas: por que a ordem importa](#7-cache-de-camadas)
8. [Como usar: comandos na prática](#8-como-usar-comandos-na-prática)
9. [Outras instruções muito usadas](#9-outras-instruções-muito-usadas)
10. [Boas práticas e erros comuns](#10-boas-práticas-e-erros-comuns)
11. [Resumo rápido](#11-resumo-rápido)

---

## 1. O que é um Dockerfile

Um **Dockerfile** é um arquivo de texto com a **receita** para montar uma **imagem** Docker. Cada linha é uma instrução que o Docker executa em ordem.

Analogia: o Dockerfile é a **receita de bolo**. A imagem é o **bolo pronto**, congelado. O container é uma **fatia servida e sendo comida**, ou seja, a imagem em execução.

```
Dockerfile  ──(docker build)──►  Imagem  ──(docker run)──►  Container
 (receita)                       (pronto)                   (rodando)
```

---

## 2. Conceitos base

| Conceito | O que é | Analogia |
|---|---|---|
| **Imagem** | Pacote somente leitura com tudo o que a app precisa: sistema, Java, seu JAR | Instalador / ISO |
| **Container** | Instância em execução de uma imagem | Programa aberto |
| **Camada (layer)** | Cada instrução do Dockerfile gera uma camada; o Docker guarda cada uma em cache | Folhas empilhadas |
| **Contexto de build** | A pasta enviada ao Docker no `docker build` (geralmente `.`) | A mala que você entrega ao Docker |
| **Registry / Docker Hub** | Repositório onde ficam imagens como `maven` e `eclipse-temurin` | Loja de apps |

---

## 3. Visão geral: multi-stage

Este Dockerfile tem **dois estágios**. Cada `FROM` começa um estágio novo.

```
┌─────────────────────────────────────────┐
│ ESTÁGIO 1: build                        │
│ Imagem: Maven + JDK 21 (pesada)         │
│ Faz: baixa dependências, compila,       │
│      gera target/xxx.jar                │
└──────────────────┬──────────────────────┘
                   │ só o .jar é copiado
                   ▼
┌─────────────────────────────────────────┐
│ ESTÁGIO 2: execução                     │
│ Imagem: apenas JRE 21 (leve)            │
│ Contém: JRE + app.jar                   │
│ ← ESSA é a imagem final                 │
└─────────────────────────────────────────┘
```

**Por que fazer assim?**

- O Maven e o JDK só servem para **compilar**. Para **rodar** basta o JRE.
- A imagem final fica **muito menor**, mais rápida de baixar e mais segura, porque tem menos coisas dentro.
- O estágio 1 é **descartado**: não vai para a imagem final.

---

## 4. O Dockerfile completo

```dockerfile
# ESTÁGIO 1 - BUILD
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ESTÁGIO 2 - EXECUÇÃO
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

(Os comentários originais foram retirados aqui só para caber melhor; a explicação está abaixo.)

---

## 5. Explicação linha a linha

### Estágio 1: build

#### `FROM maven:3.9-eclipse-temurin-21 AS build`

- **`FROM`**: define a **imagem base**, o ponto de partida. Todo Dockerfile começa com ele.
- **`maven:3.9-eclipse-temurin-21`**: imagem oficial do Docker Hub com **Maven 3.9 + JDK 21 (Eclipse Temurin)**. Formato `nome:tag`, onde a tag indica a versão.
- **`AS build`**: dá um **apelido** a este estágio. O estágio 2 vai usar esse nome para buscar o JAR (`--from=build`).

> Você não instala Java nem Maven na imagem. Já vêm prontos nessa imagem base.

#### `WORKDIR /app`

- Define a **pasta de trabalho** dentro do container. Se não existir, o Docker cria.
- Todos os comandos seguintes (`COPY`, `RUN`) acontecem **dentro de `/app`**.
- Equivale a fazer `mkdir -p /app && cd /app`, mas de forma persistente nas próximas instruções.

#### `COPY pom.xml .`

- **`COPY origem destino`**: copia arquivos **da sua máquina** (do contexto de build) **para dentro da imagem**.
- `pom.xml`: arquivo do seu projeto local, ao lado do Dockerfile.
- `.`: destino, ou seja, a pasta atual do container, que é `/app` por causa do `WORKDIR`.
- Resultado: existe `/app/pom.xml` dentro do container.

**Por que copiar só o `pom.xml` agora?** Para aproveitar o cache. Veja a [seção 7](#7-cache-de-camadas).

#### `RUN mvn dependency:go-offline`

- **`RUN`**: executa um comando **durante o build** e salva o resultado como uma camada.
- `mvn dependency:go-offline`: o Maven lê o `pom.xml` e **baixa todas as dependências** (Spring, PostgreSQL driver, Flyway etc.) para o repositório local dentro da imagem.
- Efeito: as dependências ficam guardadas numa camada de cache.

#### `COPY src ./src`

- Copia a pasta `src` do seu projeto (código Java, `application.properties`, migrations do Flyway em `resources`) para `/app/src` no container.
- Vem **depois** do `go-offline` de propósito: o código muda o tempo todo, o `pom.xml` quase nunca.

#### `RUN mvn clean package -DskipTests`

- `clean`: apaga a pasta `target` anterior.
- `package`: compila e **empacota** a aplicação, gerando o JAR em `/app/target/`.
- `-DskipTests`: **não executa os testes** durante o build.
    - O comentário do seu Dockerfile explica o motivo: os testes usam **Testcontainers**, que precisa de Docker, e dentro do `docker build` não há Docker disponível.
    - Detalhe: `-DskipTests` **compila** os testes e só não os executa. Já `-Dmaven.test.skip=true` nem compila. Para esse caso, `-DskipTests` é o suficiente.

---

### Estágio 2: execução

#### `FROM eclipse-temurin:21-jre`

- **Novo `FROM` = novo estágio, começando do zero.** Nada do estágio 1 está aqui ainda.
- `eclipse-temurin:21-jre`: só o **JRE 21** (ambiente para *rodar* Java), sem Maven nem compilador.
- É essa imagem que vira a **imagem final**.

#### `WORKDIR /app`

- Pasta de trabalho do estágio 2. Precisa ser declarada de novo porque é um estágio novo.

#### `COPY --from=build /app/target/*.jar app.jar`

- **`--from=build`**: em vez de copiar da sua máquina, copia **de outro estágio**, o `build` do apelido que demos lá em cima.
- `/app/target/*.jar`: o JAR gerado pelo Maven. O `*` evita ter que escrever o nome e a versão exata (`meuprojeto-0.0.1-SNAPSHOT.jar`).
- `app.jar`: nome simples dentro da imagem final, para o `ENTRYPOINT` não depender da versão do projeto.

> Esta é a linha que liga os dois estágios. É a **única coisa** do estágio 1 que chega na imagem final.

#### `EXPOSE 8080`

- **Documenta** que a aplicação escuta na porta 8080, o padrão do Spring Boot.
- **Importante:** `EXPOSE` **não publica** a porta para o seu computador. É só informação.
- Quem de fato abre a porta é o `-p 8080:8080` no `docker run` ou o `ports:` no `docker-compose.yml` (veremos no próximo guia).

#### `ENTRYPOINT ["java", "-jar", "app.jar"]`

- Define o **comando que roda quando o container inicia**.
- Equivale a digitar `java -jar app.jar` no terminal dentro do container.
- Está na **forma exec** (lista em colchetes), que é a recomendada: o processo Java vira o processo principal (PID 1) e recebe corretamente sinais como `SIGTERM` quando você faz `docker stop`, permitindo um desligamento limpo.

---

## 6. Como o Dockerfile se conecta ao projeto local

Estrutura típica do projeto na sua máquina:

```
meu-projeto/
├── Dockerfile          ← a receita
├── docker-compose.yml  ← (próximo guia)
├── pom.xml             ← copiado no 1º COPY
└── src/                ← copiado no 2º COPY
    ├── main/
    │   ├── java/...
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/   (Flyway)
    └── test/...
```

Fluxo de dados:

```
Sua máquina                         Dentro do build
───────────                         ───────────────
pom.xml        ──COPY──►            /app/pom.xml
src/           ──COPY──►            /app/src/
                                      │
                                      ▼ mvn package
                                    /app/target/xxx.jar
                                      │
                                      ▼ COPY --from=build
                           Imagem final: /app/app.jar
```

Pontos-chave:

- O `docker build .` envia a pasta atual (**contexto**) ao Docker. Só o que está nesse contexto pode ser usado em `COPY`.
- Caminhos de `COPY` são **relativos ao contexto**, não ao Dockerfile em si e não ao seu disco inteiro.
- O que está dentro do container **não aparece** na sua pasta local (a menos que você use volumes, assunto do compose).

---

## 7. Cache de camadas

O Docker reaproveita uma camada se **a instrução e os arquivos que ela usa não mudaram**. Quando uma camada muda, **todas as seguintes** são refeitas.

Ordem usada no seu Dockerfile (**boa**):

```
1. COPY pom.xml .              ← muda raramente
2. RUN mvn dependency:go-offline   ← pesado (baixa a internet), fica em cache
3. COPY src ./src              ← muda toda hora
4. RUN mvn clean package       ← só essa parte refaz
```

Se você mudar só uma linha de código Java, os passos 1 e 2 vêm do **cache**. O build leva segundos em vez de minutos.

Ordem **ruim** (para comparação):

```dockerfile
COPY . .                        # copia tudo de uma vez
RUN mvn clean package           # qualquer mudança no código re-baixa TODAS as dependências
```

> **Regra de ouro:** coloque no topo o que muda **menos** e embaixo o que muda **mais**.

---

## 8. Como usar: comandos na prática

### Construir a imagem

```bash
docker build -t meu-app:1.0 .
```

| Parte | Significado |
|---|---|
| `docker build` | Executa o Dockerfile |
| `-t meu-app:1.0` | **t**ag: dá nome e versão à imagem |
| `.` | Contexto de build: a pasta atual |

### Ver as imagens criadas

```bash
docker images
```

Exemplo de saída:

```
REPOSITORY   TAG   IMAGE ID       CREATED         SIZE
meu-app      1.0   3f2a1b9c8d7e   10 seconds ago  290MB
```

### Rodar um container a partir da imagem

```bash
docker run -p 8080:8080 meu-app:1.0
```

- `-p 8080:8080` → `porta-do-seu-PC:porta-do-container`. Agora `http://localhost:8080` chega na aplicação.

> Atenção: sua aplicação precisa de **PostgreSQL**. Rodando só esse container, ela não achará o banco. É exatamente por isso que existe o **docker-compose**, que sobe app e banco juntos. Veremos no próximo guia.

### Passar configuração por variável de ambiente

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/meubanco \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  meu-app:1.0
```

O Spring Boot converte variáveis de ambiente em propriedades automaticamente (`SPRING_DATASOURCE_URL` → `spring.datasource.url`).

---

## 9. Outras instruções muito usadas

Não estão no seu Dockerfile, mas você vai encontrá-las o tempo todo.

### `ENV`: variável de ambiente fixa na imagem

```dockerfile
ENV JAVA_OPTS="-Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod
```

Fica disponível dentro do container em execução.

### `ARG`: variável só durante o build

```dockerfile
ARG APP_VERSION=1.0
RUN echo "Buildando versão $APP_VERSION"
```

```bash
docker build --build-arg APP_VERSION=2.0 -t meu-app .
```

Diferença: `ARG` existe **só no build**; `ENV` existe **também em runtime**.

### `CMD`: comando padrão (substituível)

```dockerfile
CMD ["java", "-jar", "app.jar"]
```

| | `ENTRYPOINT` | `CMD` |
|---|---|---|
| Papel | O **executável** principal | **Argumentos/padrão** do comando |
| Ao rodar `docker run imagem outro-comando` | Continua valendo; o resto vira argumento | É **substituído** |

Combinação comum:

```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]
```

### `ADD`

Parecido com `COPY`, mas também descompacta `.tar` e aceita URLs. **Prefira `COPY`**, que é mais previsível.

### `USER`: não rodar como root

```dockerfile
RUN useradd -ms /bin/bash appuser
USER appuser
```

Rodar como usuário comum reduz o estrago se alguém explorar a aplicação.

### `HEALTHCHECK`: o Docker verifica se a app está saudável

```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

(Exige Spring Actuator e `curl` na imagem.)

### `VOLUME`: pasta persistente

```dockerfile
VOLUME /app/logs
```

### `LABEL`: metadados

```dockerfile
LABEL maintainer="seu-email@exemplo.com" version="1.0"
```

### `.dockerignore`: o que **não** enviar ao contexto

Arquivo ao lado do Dockerfile, funciona como um `.gitignore`:

```
target/
.git/
.idea/
*.log
.env
```

Benefícios: build mais rápido e nenhum segredo (como `.env`) vazando para dentro da imagem.

---

## 10. Boas práticas e erros comuns

### Boas práticas

- Usar **multi-stage** para a imagem final ficar leve (já feito).
- Copiar o `pom.xml` e baixar dependências **antes** do código (já feito).
- Usar **tags de versão fixas** (`21-jre`) em vez de `latest`, para builds reproduzíveis.
- Ter um **`.dockerignore`**.
- Rodar com **usuário não-root** em produção.
- Nunca colocar **senhas** dentro do Dockerfile. Use variáveis de ambiente ou secrets.

### Erros comuns

| Erro | Causa provável | Solução |
|---|---|---|
| `COPY failed: file not found` | Arquivo fora do contexto ou caminho errado | Conferir onde está rodando `docker build` e o `.` final |
| `Could not find or load main class` / JAR não encontrado | O `*.jar` não casou ou há mais de um JAR em `target/` | Verificar o nome do JAR gerado e o `COPY --from=build` |
| App sobe mas `localhost:8080` não abre | Faltou publicar a porta | Usar `-p 8080:8080` (o `EXPOSE` sozinho não basta) |
| `Connection refused` ao banco | Dentro do container, `localhost` é o **próprio container** | Usar o nome do serviço do banco no compose |
| Build lento toda vez | Ordem das instruções quebrando o cache | Manter `pom.xml` antes de `src` |
| Testes falham no build | Testcontainers precisa de Docker | Manter `-DskipTests` (já feito) |

---

## 11. Resumo rápido

| Instrução | Resumo em uma linha |
|---|---|
| `FROM ... AS build` | Escolhe a imagem base e nomeia o estágio |
| `WORKDIR /app` | Define a pasta de trabalho no container |
| `COPY pom.xml .` | Copia o pom do seu projeto para o container |
| `RUN mvn dependency:go-offline` | Baixa as dependências (fica em cache) |
| `COPY src ./src` | Copia o código-fonte |
| `RUN mvn clean package -DskipTests` | Compila e gera o JAR sem rodar testes |
| `FROM eclipse-temurin:21-jre` | Começa a imagem final, leve, só com JRE |
| `COPY --from=build ...` | Traz só o JAR do estágio 1 |
| `EXPOSE 8080` | Documenta a porta (não a publica) |
| `ENTRYPOINT [...]` | Comando que inicia a aplicação |

---

**Próximo passo:** o guia do `docker-compose.yml`, em que veremos como esta imagem é construída, ligada ao PostgreSQL, com portas, variáveis e volumes. 🐳
