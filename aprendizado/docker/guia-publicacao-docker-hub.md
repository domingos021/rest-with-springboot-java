# 🐳 Publicar uma imagem Docker no Docker Hub — passo a passo

## 1. Objetivo

Este guia documenta o processo realizado para publicar a aplicação **Spring Boot** no **Docker Hub**, desde a criação do repositório remoto até o envio das imagens com as tags `1.0` e `latest`.

Ao final, teremos:

- Uma imagem Docker criada localmente.
- Um repositório remoto no Docker Hub.
- Duas tags apontando para a mesma imagem: `1.0` e `latest`.
- A imagem publicada e disponível para download em outras máquinas.

### Dados utilizados neste projeto

| Item | Valor |
|---|---|
| Projeto | `rest-with-springboot-java` |
| Usuário Docker Hub | `domingosjovete` |
| Repositório | `curso_spring_boot` |
| Nome completo da imagem | `domingosjovete/curso_spring_boot` |
| Primeira versão | `1.0` |
| Tag de referência | `latest` |

> **Atenção:** os comandos deste guia foram escritos para o PowerShell do Windows. Substitua o usuário, o repositório ou a versão caso esteja trabalhando em outro projeto.

---

## 2. Criar o repositório no Docker Hub

Antes de enviar uma imagem, é necessário ter um repositório no Docker Hub para recebê-la.

### Passo 1 — Acessar o Docker Hub

Acesse:

https://hub.docker.com/

Entre na sua conta.

### Passo 2 — Criar um repositório

1. Abra a área de repositórios.
2. Clique em **Create repository**.
3. Informe o nome do repositório: `curso_spring_boot`.
4. Escolha a visibilidade desejada, por exemplo, **Public**.
5. Confirme a criação.

O repositório criado foi:

`domingosjovete/curso_spring_boot`

A página do repositório:

https://hub.docker.com/r/domingosjovete/curso_spring_boot

### O que significa `usuario/repositorio`?

No Docker Hub, o nome completo da imagem segue esta estrutura:

```text
USUARIO/REPOSITORIO
```

No nosso caso:

```text
domingosjovete/curso_spring_boot
```

Esse nome será utilizado nos comandos de tag, push e pull.

> Criar o repositório no site não envia a imagem automaticamente. Primeiro precisamos construir a imagem no computador e depois publicá-la com `docker push`.

---

## 3. Preparar o projeto local

Abra o PowerShell na pasta raiz do projeto, isto é, na pasta em que está o `pom.xml` e o `Dockerfile`.

Exemplo:

```powershell
cd C:\Users\becky\curso-spring-boot\rest-with-springboot-java
```

Confira se está na pasta correta:

```powershell
dir
```

Você deverá localizar, entre outros arquivos, o `pom.xml` e o `Dockerfile`.

### O papel do Dockerfile

O Dockerfile contém as instruções usadas pelo Docker para construir a imagem.

Neste projeto, utilizamos uma construção em múltiplos estágios (*multi-stage build*): um estágio prepara/compila a aplicação e outro contém o ambiente necessário para executá-la.

Exemplo da estrutura utilizada:

```dockerfile
# Estágio de compilação
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Estágio de execução
FROM eclipse-temurin:21-jre

WORKDIR /app

ARG JAR_FILE=target/*.jar
COPY --from=build /app/${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

> **Observação:** o exemplo acima ilustra a estrutura multi-stage utilizada no projeto. Se o seu Dockerfile atual tiver instruções diferentes, mantenha o arquivo real do projeto; os comandos de build e publicação continuam seguindo o mesmo princípio.

---

## 4. Criar a imagem Docker local

Com o terminal na raiz do projeto, execute:

```powershell
docker build -t domingosjovete/curso_spring_boot:1.0 .
```

### Entendendo o comando

| Parte | Significado |
|---|---|
| `docker build` | Constrói uma imagem Docker. |
| `-t` | Define o nome e a tag da imagem (*tag*). |
| `domingosjovete/curso_spring_boot` | Usuário e repositório que identificam a imagem. |
| `:1.0` | Tag que identifica a versão. |
| `.` | Informa que o contexto de construção é a pasta atual. |

O ponto final é importante: ele indica ao Docker onde procurar o Dockerfile e os arquivos necessários para a construção.

### O que acontece durante o build?

De acordo com as instruções do Dockerfile, o Docker:

1. Lê o Dockerfile.
2. Prepara o ambiente de compilação.
3. Copia os arquivos necessários.
4. Executa a compilação e empacotamento da aplicação.
5. Prepara a imagem final de execução.
6. Gera a imagem local com o nome e a tag informados.

Quando a construção termina, uma mensagem semelhante a esta confirma que a imagem recebeu o nome indicado:

```text
naming to docker.io/domingosjovete/curso_spring_boot:1.0
```

> **Importante:** `docker build` cria a imagem no Docker local. Ele não publica a imagem no Docker Hub.

---

## 5. Verificar a imagem criada localmente

Execute:

```powershell
docker images
```

Esse comando lista as imagens disponíveis no Docker local.

Também é possível consultar diretamente a imagem:

```powershell
docker image ls domingosjovete/curso_spring_boot
```

A saída deverá apresentar uma linha semelhante a:

```text
REPOSITORY                         TAG   IMAGE ID
domingosjovete/curso_spring_boot   1.0   a0a00427f2b9
```

Os identificadores e as datas podem ser diferentes em cada construção.

### Consultar os detalhes da imagem

```powershell
docker image inspect domingosjovete/curso_spring_boot:1.0
```

Esse comando apresenta informações detalhadas, como ID, configuração, camadas e metadados da imagem.

### Consultar o histórico de construção

```powershell
docker history domingosjovete/curso_spring_boot:1.0
```

Esse comando permite visualizar as camadas e instruções que contribuíram para a imagem.

---

## 6. Criar a tag `latest`

Além da versão `1.0`, decidimos disponibilizar a mesma imagem com a tag `latest`.

Execute:

```powershell
docker tag domingosjovete/curso_spring_boot:1.0 domingosjovete/curso_spring_boot:latest
```

### O que esse comando faz?

O comando `docker tag` cria uma nova referência para uma imagem existente.

Neste caso:

- Origem: `domingosjovete/curso_spring_boot:1.0`
- Destino: `domingosjovete/curso_spring_boot:latest`

**Atenção:** esse comando não constrói uma segunda imagem nem modifica o conteúdo da imagem original. As duas tags passam a apontar para o mesmo conteúdo de imagem.

Podemos conferir:

```powershell
docker images
```

O resultado deverá mostrar as duas tags:

```text
REPOSITORY                         TAG       IMAGE ID
domingosjovete/curso_spring_boot   1.0       a0a00427f2b9
domingosjovete/curso_spring_boot   latest   a0a00427f2b9
```

O `IMAGE ID` igual indica que as duas referências apontam para a mesma imagem local.

### O que significa `latest`?

`latest` é uma tag convencional usada para indicar uma versão que o responsável pelo repositório deseja tratar como referência mais recente.

Ela **não é atualizada automaticamente** quando o código muda. É necessário construir a nova imagem e atribuir a tag `latest` a ela.

---

## 7. Autenticar no Docker Hub

Antes de enviar a imagem, precisamos autenticar o Docker local na conta do Docker Hub.

Execute:

```powershell
docker login
```

No processo realizado, o Docker informou que já havia credenciais salvas:

```text
Authenticating with existing credentials...
Username: domingosjovete

Login Succeeded
```

A mensagem:

```text
Login Succeeded
```

confirma que a autenticação foi concluída.

### Se quiser entrar com outra conta

Primeiro encerre a sessão atual:

```powershell
docker logout
```

Depois faça login novamente:

```powershell
docker login
```

> Não compartilhe senhas, tokens de acesso ou credenciais em arquivos públicos ou capturas de tela.

---

## 8. Publicar a tag `1.0` no Docker Hub

Com a imagem criada, tagueada e autenticada, podemos enviá-la ao repositório remoto.

Execute:

```powershell
docker push domingosjovete/curso_spring_boot:1.0
```

### O que significa `docker push`?

O comando `docker push` envia a imagem local para o repositório correspondente no Docker Hub.

Durante o envio, podem aparecer mensagens como:

| Mensagem | Significado |
|---|---|
| `Pushed` | A camada foi enviada ao Docker Hub. |
| `Mounted from` | O Docker Hub reutilizou uma camada que já estava disponível em outro repositório. |
| `Layer already exists` | A camada já existia no destino e não precisou ser enviada novamente. |
| `Already exists` | O conteúdo já estava disponível no destino. |
| `digest: sha256:...` | Identificador do conteúdo publicado no registro. |

Ao final, uma saída semelhante a esta indica que a tag foi publicada:

```text
1.0: digest: sha256:... size: ...
```

O digest real deve ser consultado na saída do próprio comando.

---

## 9. Publicar a tag `latest`

Como criamos também a tag `latest`, precisamos enviá-la separadamente.

Execute:

```powershell
docker push domingosjovete/curso_spring_boot:latest
```

O Docker Hub pode informar que várias camadas já existem. Isso é esperado: como `1.0` e `latest` apontam para a mesma imagem, não é necessário transmitir novamente todo o conteúdo.

Uma saída semelhante a esta confirma a publicação:

```text
latest: digest: sha256:... size: ...
```

### Como interpretar os resultados do nosso envio?

No processo realizado, as duas publicações terminaram com o mesmo digest:

```text
1.0:    sha256:a0a00427f2b92174eaf7a9b0d953ae94fe8d442bee068ebb5d35a8ba8610015e
latest: sha256:a0a00427f2b92174eaf7a9b0d953ae94fe8d442bee068ebb5d35a8ba8610015e
```

Isso confirma que as duas tags publicadas apontam para o mesmo conteúdo de imagem.

---

## 10. Conferir no Docker Hub

Acesse o repositório:

https://hub.docker.com/r/domingosjovete/curso_spring_boot

Na página, abra a área **Tags**.

Devem aparecer:

- `1.0`
- `latest`

A presença das tags no repositório remoto confirma que a publicação foi realizada.

---

## 11. Baixar a imagem publicada

Depois de publicada, a imagem pode ser baixada em outro computador que tenha Docker instalado.

### Baixar uma versão específica

```powershell
docker pull domingosjovete/curso_spring_boot:1.0
```

Esse comando solicita ao Docker Hub a imagem identificada pela tag `1.0`.

### Baixar a tag `latest`

```powershell
docker pull domingosjovete/curso_spring_boot:latest
```

Se a tag `latest` tiver sido atualizada para uma versão mais recente, esse comando baixará o conteúdo associado a ela.

### Conferir as imagens baixadas

```powershell
docker images
```

> `docker pull` baixa a imagem. Ele não inicia automaticamente um container e não substitui a configuração necessária para executar a aplicação, como variáveis de ambiente, rede e conexão com o PostgreSQL.

---

## 12. Fluxo completo dos comandos utilizados

Esta é a sequência principal realizada para publicar a primeira versão.

### 1 — Entrar na pasta do projeto

```powershell
cd C:\Users\becky\curso-spring-boot\rest-with-springboot-java
```

### 2 — Construir a imagem local

```powershell
docker build -t domingosjovete/curso_spring_boot:1.0 .
```

### 3 — Conferir a imagem

```powershell
docker images
```

### 4 — Criar a tag `latest`

```powershell
docker tag domingosjovete/curso_spring_boot:1.0 domingosjovete/curso_spring_boot:latest
```

### 5 — Autenticar no Docker Hub

```powershell
docker login
```

### 6 — Publicar a versão `1.0`

```powershell
docker push domingosjovete/curso_spring_boot:1.0
```

### 7 — Publicar a tag `latest`

```powershell
docker push domingosjovete/curso_spring_boot:latest
```

---

## 13. Como publicar uma atualização no futuro

Quando o código da aplicação mudar, não basta executar `docker push` novamente esperando que o Docker Hub atualize a imagem sozinho.

É necessário construir uma nova imagem com uma nova versão.

Por exemplo, se a próxima versão for `1.1`:

### Passo 1 — Construir a nova versão

```powershell
docker build -t domingosjovete/curso_spring_boot:1.1 .
```

### Passo 2 — Atualizar a referência `latest`

```powershell
docker tag domingosjovete/curso_spring_boot:1.1 domingosjovete/curso_spring_boot:latest
```

Agora, localmente:

- `1.0` continua identificando a imagem anterior.
- `1.1` identifica a nova imagem.
- `latest` passa a apontar para a nova imagem.

### Passo 3 — Publicar a nova versão

```powershell
docker push domingosjovete/curso_spring_boot:1.1
```

### Passo 4 — Publicar a nova referência `latest`

```powershell
docker push domingosjovete/curso_spring_boot:latest
```

### Resultado esperado

| Tag | Conteúdo identificado |
|---|---|
| `1.0` | Imagem da primeira publicação |
| `1.1` | Imagem da nova atualização |
| `latest` | Imagem que foi definida como a referência mais recente |

> **Boa prática:** mantenha as tags de versão (`1.0`, `1.1`, `1.2`...) para identificar publicações específicas. Atualize `latest` quando desejar que ela represente a versão mais recente disponibilizada.

---

## 14. Diferença entre os principais comandos

| Comando | Para que serve | Quando usar |
|---|---|---|
| `docker build` | Constrói uma imagem local a partir do Dockerfile. | Quando precisar criar ou reconstruir a imagem após alterações. |
| `docker images` | Lista as imagens locais. | Para conferir quais imagens e tags estão disponíveis. |
| `docker tag` | Cria outra referência/tag para uma imagem. | Para atribuir uma versão ou a tag `latest`. |
| `docker login` | Autentica no registro Docker. | Antes de publicar uma imagem privada ou quando a sessão não estiver autenticada. |
| `docker push` | Envia uma imagem para o Docker Hub. | Depois de construir e identificar a imagem com o repositório e a tag corretos. |
| `docker pull` | Baixa uma imagem do Docker Hub. | Para obter uma imagem publicada em outra máquina ou atualizar uma cópia local. |
| `docker image inspect` | Exibe detalhes da imagem. | Para investigar configuração e metadados. |
| `docker history` | Exibe o histórico de camadas da imagem. | Para entender como a imagem foi construída. |
| `docker logout` | Encerra a autenticação local. | Quando quiser sair da conta atual. |

---

## 15. Entendendo o processo: local e remoto

É importante separar as etapas locais das etapas realizadas no Docker Hub.

```text
                    COMPUTADOR LOCAL
                 ┌─────────────────────┐
                 │ Código Spring Boot  │
                 │ pom.xml             │
                 │ Dockerfile          │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    docker build     │
                 │                     │
                 │ Cria a imagem local │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    docker tag       │
                 │                     │
                 │ 1.0 e latest        │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │    docker login     │
                 │                     │
                 │ Autentica a conta   │
                 └──────────┬──────────┘
                            │
                            ▼
                    docker push
                            │
                            ▼
                 ┌─────────────────────┐
                 │      DOCKER HUB     │
                 │                     │
                 │ curso_spring_boot   │
                 │                     │
                 │ Tags:               │
                 │ - 1.0               │
                 │ - latest            │
                 └─────────────────────┘
```

### Resumo conceitual

- **Build:** cria a imagem no computador.
- **Tag:** identifica a imagem com um nome e uma versão.
- **Login:** autentica no Docker Hub.
- **Push:** publica a imagem no Docker Hub.
- **Pull:** baixa uma imagem publicada.

**Conclusão:** a imagem só fica disponível no Docker Hub depois que o `docker push` termina com sucesso. Criar a imagem localmente e criar uma tag não são suficientes para publicá-la.
