# Dockerfile

> Arquivo responsável por definir as instruções necessárias para criar uma imagem Docker personalizada.

---

# 1. O que é?

O Dockerfile é um arquivo de texto que contém uma sequência de instruções que o Docker utiliza para **construir uma imagem**.

Ele funciona como uma "receita" para criar uma imagem.

A imagem gerada pelo Dockerfile poderá posteriormente ser utilizada para criar containers.

Analogia com Java:

```
Classe Java

↓

Objeto criado através do new
```

Docker:

```
Dockerfile

↓

Imagem Docker

↓

Container
```

---

# 2. Para que serve?

O Dockerfile serve para automatizar a criação de ambientes.

Ele permite definir:

- Sistema operacional base.
- Versão da linguagem utilizada.
- Dependências necessárias.
- Arquivos que serão copiados.
- Variáveis de ambiente.
- Portas utilizadas.
- Comando que será executado quando o container iniciar.

---

# 3. O que acontece internamente?

Quando executamos:

```bash
docker build
```

o Docker lê o Dockerfile linha por linha.

Cada instrução gera uma nova camada (*layer*) dentro da imagem.

Fluxo:

```
Dockerfile

↓

Docker Engine interpreta as instruções

↓

Executa cada etapa

↓

Cria camadas da imagem

↓

Gera uma nova imagem

↓

Imagem pronta para criar containers
```

---

# 4. Quando devo utilizar?

Utilizamos Dockerfile quando precisamos criar uma imagem própria.

Exemplos:

- Aplicação Spring Boot.
- API Java.
- Front-end React.
- Aplicação Node.js.
- Sistemas empresariais.

---

# 5. Quando NÃO devo utilizar?

Não é necessário criar um Dockerfile quando uma imagem pronta já atende a necessidade.

Exemplo:

Para subir um banco PostgreSQL:

```bash
docker run postgres
```

A imagem oficial do PostgreSQL já existe.

Não precisamos criar uma imagem personalizada.

---

# 6. Estrutura de um Dockerfile

Exemplo:

```dockerfile
FROM openjdk:21

WORKDIR /app

COPY app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# 7. Principais instruções

---

## FROM

### O que é?

Define a imagem base que será utilizada para construir a nova imagem.

É normalmente a primeira instrução do Dockerfile.

---

### Exemplo:

```dockerfile
FROM openjdk:21
```

Significa:

"Minha aplicação será construída em cima de uma imagem que já possui Java 21."

---

### Analogia Java

Seria como utilizar uma classe existente como base.

---

## WORKDIR

### O que é?

Define o diretório de trabalho dentro do container.

---

### Exemplo:

```dockerfile
WORKDIR /app
```

Depois disso:

```dockerfile
COPY app.jar app.jar
```

será executado dentro de:

```
/app
```

---

## COPY

### O que é?

Copia arquivos da máquina local para dentro da imagem.

---

### Exemplo:

```dockerfile
COPY app.jar app.jar
```

Antes:

```
Computador

app.jar
```

Depois:

```
Container

/app/app.jar
```

---

## RUN

### O que é?

Executa comandos durante a criação da imagem.

Importante:

Ele executa no momento do:

```bash
docker build
```

e não quando o container inicia.

---

### Exemplo:

```dockerfile
RUN apt update
```

---

## EXPOSE

### O que é?

Documenta qual porta a aplicação utiliza.

---

### Exemplo:

```dockerfile
EXPOSE 8080
```

Significa:

"A aplicação dentro desse container utiliza a porta 8080."

Observação:

O EXPOSE sozinho não libera a porta.

Para liberar:

```bash
docker run -p 8080:8080 minha-imagem
```

---

## ENTRYPOINT

### O que é?

Define o comando principal executado quando o container inicia.

---

### Exemplo:

```dockerfile
ENTRYPOINT ["java","-jar","app.jar"]
```

Quando o container iniciar:

```bash
docker start container
```

o Java será executado automaticamente.

---

## CMD

### O que é?

Define um comando padrão para o container.

---

### Exemplo:

```dockerfile
CMD ["java","-jar","app.jar"]
```

A diferença:

- ENTRYPOINT normalmente define o executável principal.
- CMD define argumentos ou comando padrão.

---

# 8. Exemplo profissional - Spring Boot

Estrutura:

```
meu-projeto

├── Dockerfile

├── pom.xml

└── target

    └── app.jar
```

Dockerfile:

```dockerfile
FROM eclipse-temurin:21

WORKDIR /app

COPY target/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
```

---

# 9. Construindo uma imagem

Com o Dockerfile criado:

```bash
docker build -t minha-api .
```

Explicação:

```text
docker build

→ cria uma imagem

-t

→ define o nome da imagem

minha-api

→ nome escolhido

.

→ utiliza o Dockerfile da pasta atual
```

---

# 10. Verificando a imagem criada

```bash
docker images
```

Resultado:

```
REPOSITORY

minha-api

TAG

latest
```

---

# 11. Criando um container da imagem

```bash
docker run -p 8080:8080 minha-api
```

Fluxo:

```
Imagem

↓

docker run

↓

Container

↓

Spring Boot iniciado
```

---

# 12. Fluxo completo

```
Código Java

↓

Maven gera .jar

↓

Dockerfile

↓

docker build

↓

Imagem Docker

↓

docker run

↓

Container executando aplicação
```

---

# 13. Boas práticas

✔ Utilizar imagens oficiais.

✔ Definir versões específicas.

Exemplo:

Evitar:

```dockerfile
FROM openjdk
```

Preferir:

```dockerfile
FROM openjdk:21
```

---

✔ Criar imagens pequenas.

✔ Utilizar `.dockerignore`.

✔ Não colocar senhas dentro do Dockerfile.

Errado:

```dockerfile
ENV PASSWORD=123456
```

Preferir:

```bash
-e PASSWORD=123456
```

---

# 14. Erros comuns

## Erro

```text
COPY failed
```

### Causa

Arquivo não encontrado no contexto do build.

---

## Erro

```text
Unable to find image
```

### Causa

Imagem base inexistente.

---

# 15. Resumo

Dockerfile é o arquivo responsável por ensinar o Docker como criar uma imagem.

Conceitos principais:

- Dockerfile → receita.
- Image → resultado da receita.
- Container → execução da imagem.

Fluxo:

```
Dockerfile

↓

docker build

↓

Image

↓

docker run

↓

Container
```