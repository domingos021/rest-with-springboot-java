# 🐳 Docker Hub - Guia Completo de Repositórios e Publicação de Imagens Docker

## 📌 Introdução

O **Docker Hub** funciona de forma semelhante ao GitHub, porém ao invés de armazenar código-fonte, ele armazena **imagens Docker prontas para execução**.

Comparação:

| GitHub | Docker Hub |
|---|---|
| Armazena código-fonte | Armazena imagens Docker |
| `git push` envia código | `docker push` envia imagens |
| `git clone` baixa código | `docker pull` baixa imagens |
| Repositórios possuem arquivos | Repositórios possuem imagens e tags |

---

## Exemplo

No GitHub:

```
Projeto Spring Boot
        |
        v
Código fonte Java
        |
        v
git commit
        |
        v
git push
```

No Docker Hub:

```
Imagem Docker
        |
        v
Aplicação empacotada
        |
        v
docker push
```

---

# 1. Criar conta no Docker Hub

Acesse:

```
https://hub.docker.com
```

Crie sua conta.

Exemplo:

```
Usuário:

domingosjovete
```

Esse usuário será usado no nome das imagens:

```
domingosjovete/nome-da-imagem
```

---

# 2. Fazer login no Docker Hub pelo terminal

Antes de enviar imagens, o Docker precisa saber qual conta será usada.

Comando:

```bash
docker login
```

Exemplo:

```text
Username:
Password:
```

Resultado esperado:

```text
Login Succeeded
```

Caso queira trocar de usuário:

```bash
docker logout
```

Depois:

```bash
docker login
```

---

# 3. Criar um repositório manualmente no Docker Hub

No site do Docker Hub:

```
Repositories
      |
      v
Create Repository
```

Informe:

```
Repository Name:

rest-with-springboot-java-app
```

Escolha:

```
Public
```

ou

```
Private
```

O endereço final será:

```
domingosjovete/rest-with-springboot-java-app
```

---

# 4. Criar uma imagem Docker da aplicação

Exemplo de projeto Spring Boot:

```
rest-with-springboot-java

│
├── pom.xml
├── Dockerfile
└── src
```

Primeiro gerar o `.jar`:

```bash
mvn clean package
```

Depois criar a imagem:

```bash
docker build -t rest-with-springboot-java-app .
```

Verificar:

```bash
docker image ls
```

Exemplo:

```
REPOSITORY

rest-with-springboot-java-app


TAG

latest
```

---

# 5. Caso a imagem já exista localmente

## Cenário:

A imagem já está criada:

```
rest-with-springboot-java-app:latest
```

Porém ela ainda não está associada ao Docker Hub.

Exemplo:

```
docker image ls

REPOSITORY                         TAG

rest-with-springboot-java-app      latest
```

---

# 6. Associar uma imagem existente ao Docker Hub

O Docker Hub precisa que a imagem siga este padrão:

```
usuario/nome-da-imagem:tag
```

Exemplo:

Usuário:

```
domingosjovete
```

Imagem:

```
rest-with-springboot-java-app
```

Criar uma nova tag:

```bash
docker tag rest-with-springboot-java-app:latest domingosjovete/rest-with-springboot-java-app:latest
```

---

# 7. Verificar a nova tag criada

Executar:

```bash
docker image ls
```

Resultado esperado:

```
REPOSITORY                                      TAG       IMAGE ID

rest-with-springboot-java-app                  latest    e51027a9fe35

domingosjovete/rest-with-springboot-java-app   latest    e51027a9fe35
```

Observe:

```
IMAGE ID

e51027a9fe35
```

é igual.

Isso significa:

- Não foi criada uma segunda imagem.
- Foi criada apenas uma nova referência.
- Agora ela está pronta para ser enviada ao Docker Hub.

---

# 8. Enviar a imagem para o Docker Hub

Com a tag criada:

```bash
docker push usuario/nome-da-imagem:tag
```

Exemplo:

```bash
docker push domingosjovete/rest-with-springboot-java-app:latest
```

Durante o envio aparecerá:

```
The push refers to repository

Layer pushed

latest: digest: sha256:xxxx
```

Quando finalizar, o repositório estará disponível no Docker Hub.

---

# 9. Criar o repositório automaticamente pelo primeiro push

Também é possível não criar manualmente no site.

Fluxo:

Login:

```bash
docker login
```

Criar tag:

```bash
docker tag imagem-local usuario/nome-imagem:latest
```

Exemplo:

```bash
docker tag rest-with-springboot-java-app:latest domingosjovete/rest-with-springboot-java-app:latest
```

Enviar:

```bash
docker push domingosjovete/rest-with-springboot-java-app:latest
```

O Docker Hub criará automaticamente:

```
Repositories

domingosjovete/rest-with-springboot-java-app
```

---

# 10. Baixar a imagem em outra máquina

Em qualquer computador com Docker:

```bash
docker pull domingosjovete/rest-with-springboot-java-app:latest
```

Verificar:

```bash
docker image ls
```

Executar:

```bash
docker run -p 8080:8080 domingosjovete/rest-with-springboot-java-app:latest
```

---

# 11. Trabalhando com versões usando TAG

O recomendado é não usar somente:

```
latest
```

Uma aplicação real possui versões.

Exemplo:

```
rest-with-springboot-java-app:1.0
rest-with-springboot-java-app:1.1
rest-with-springboot-java-app:2.0
```

---

## Versão 1.0

Criar:

```bash
docker tag rest-with-springboot-java-app:latest domingosjovete/rest-with-springboot-java-app:1.0
```

Enviar:

```bash
docker push domingosjovete/rest-with-springboot-java-app:1.0
```

---

## Versão 1.1

Criar:

```bash
docker tag rest-with-springboot-java-app:latest domingosjovete/rest-with-springboot-java-app:1.1
```

Enviar:

```bash
docker push domingosjovete/rest-with-springboot-java-app:1.1

Cria nova tag para a versão 1.0:
docker tag domingosjovete/rest-with-springboot-java-app:latest domingosjovete/rest-with-springboot-java-app:1.0

Manda essa tag para o Docker Hub:
docker push domingosjovete/rest-with-springboot-java-app:1.0
```

Resultado no Docker Hub:

```
Tags:

latest
1.1
1.0
```

---