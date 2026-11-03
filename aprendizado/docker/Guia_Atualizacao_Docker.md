# Guia de Atualização da Aplicação Docker

## Objetivo

Durante o desenvolvimento, trabalhe normalmente pelo IntelliJ. Quando concluir uma funcionalidade importante, atualize a imagem Docker para validar que tudo continua funcionando em containers.

---

## 1. Fluxo recomendado de atualização

Como o seu `Dockerfile` faz um **build em dois estágios** (o próprio Docker compila o projeto usando o Maven lá dentro), você **não precisa** rodar `mvn clean package` na sua máquina. O Docker faz isso para você.

O fluxo ideal após alterar o código é um único comando:

```bash
docker compose up --build -d
```

---

## 2. Passo a passo detalhado

### Passo 1: Desenvolver e testar localmente
Execute a aplicação pelo IntelliJ, faça seus testes e certifique-se de que tudo está funcionando.

### Passo 2: Reconstruir e subir a nova versão
Na raiz do projeto (onde está o `docker-compose.yml`), execute:
```bash
docker compose up --build -d
```
*(O `--build` força a recriação da imagem com o código novo, e o `-d` roda os containers em segundo plano).*

### Passo 3: Acompanhar os logs (Opcional)
Para garantir que a aplicação subiu sem erros e que o Flyway aplicou as migrações:
```bash
docker compose logs -f app
```

### Passo 4: Testar a aplicação
Abra no navegador:
* **Swagger:** `http://localhost:8080/swagger-ui/index.html`
* **API:** `http://localhost:8080/person/v1`

---

## 3. Quando devo (ou não) atualizar o Docker?

| O que você alterou no código | Precisa reconstruir o Docker? (`--build`) | O que fazer |
|---|---|---|
| **Classes, controllers, services** | **Sim** | `docker compose up --build -d` |
| **`application.properties`** | **Sim** | `docker compose up --build -d` |
| **Dependência no `pom.xml`** | **Sim** | `docker compose up --build -d` (o cache do Maven agiliza o processo) |
| **Criou nova tabela/entidade** | **Sim** | Criar nova migração Flyway (`V__...sql`) + `docker compose up --build -d` |
| **Alterou migração antiga do Flyway** | **Sim** | `docker compose down -v` e depois `docker compose up --build -d` *(atenção: apaga o banco)* |
| **Apenas reiniciar sem mudar código** | **Não** | `docker compose up -d` |
| **Apenas testar algo rápido na IDE** | **Não** | Rodar direto pelo IntelliJ (é mais rápido) |


# Parar e Reiniciar Docker Durante o Desenvolvimento

Durante o desenvolvimento, a aplicação normalmente deve ser executada pelo IntelliJ, pois isso facilita:

- Debug com breakpoints;
- Alterações rápidas no código;
- Testes durante a implementação.

Porém, como o Docker também executa a aplicação usando a mesma porta (por exemplo, `8080`), não devemos executar a aplicação local e o container ao mesmo tempo.

Caso os dois estejam ativos, ocorrerá conflito de porta.

----

# Ciclo de Desenvolvimento: IntelliJ + Docker

Durante o desenvolvimento da aplicação, normalmente trabalhamos diretamente pelo **IntelliJ IDEA**, pois isso facilita:

- Debug com breakpoints;
- Alterações rápidas no código;
- Testes durante a implementação.

O Docker será utilizado para validar a aplicação em um ambiente semelhante ao de execução final.

---

## ⚠️ Atenção: Parar o Docker antes de executar localmente

Se você decidir rodar a aplicação diretamente pelo **IntelliJ**, lembre-se de que a porta `8080` já estará ocupada pelo container do Docker.

Antes de iniciar a aplicação pela IDE, pare o container da aplicação:

```bash
docker compose stop app
PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java> docker compose stop app
```
No terminal
```bash
[+] Stopping 1/1
 ✔ Container rest-with-springboot-java-app-1  Stopped  2.5s

PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java>
```
Agora a porta 8080 está liberada para iniciar a aplicação pelo IntelliJ.

```bash
Caso queira parar todo o ambiente Docker:
PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java> docker compose down
```
## Voltar a testar pelo Docker

```bash
PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java> docker compose up -d
```
No terminal
```bash
PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java> docker compose up -d

[+] Running 2/2
 ✔ Container rest-with-springboot-java-db-1   Healthy  0.6s
 ✔ Container rest-with-springboot-java-app-1  Started 1.1s

PS C:\Users\becky\curso-spring-boot\rest-with-springboot-java>
```
Agora a aplicação está disponível novamente pelo Docker.

## Fluxo recomendado de desenvolvimento


```bash
┌─────────────────────────────┐
│   Desenvolver no IntelliJ   │
└──────────────┬──────────────┘
               │
               ▼
   Testar tudo localmente
               │
               ▼
  Terminou uma funcionalidade?
               │
        ┌──────┴──────┐
      Não             Sim
        │             │
        ▼             ▼
   Continue      mvn clean package
  programando         │
                      ▼
            docker compose up --build -d
                      │
                      ▼
            Testar Swagger e API: http://localhost:8080/person/v1
                      │
                      ▼
        Continue a próxima funcionalidade
```


# subir as alterações apos o desenvolvimento
Após concluir o desenvolvimento de uma funcionalidade, você deve atualizar a imagem Docker para validar que tudo continua funcionando em containers. Para isso, execute o comando:
```bash
docker compose up --build -d
```
mas antes faça:
> mvn clean package