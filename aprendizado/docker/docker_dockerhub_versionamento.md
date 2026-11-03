# 🐳 Docker e Docker Hub - Guia Completo de Imagens, Containers e Versionamento

## 📌 Introdução

O Docker permite empacotar uma aplicação junto com todas as suas dependências dentro de uma **imagem Docker**.

Essa imagem pode ser executada em qualquer ambiente compatível através de um **container**.

O Docker Hub funciona como um repositório remoto para armazenar e compartilhar imagens Docker.

Fluxo geral:

```
Código da aplicação
        ↓
Arquivo .jar
        ↓
Imagem Docker
        ↓
Container em execução
        ↓
Docker Hub
```

---

# 🐳 1. Conceitos fundamentais do Docker

## 📦 O que é uma imagem Docker?

Uma **imagem Docker** é um pacote contendo tudo que a aplicação precisa para funcionar.

Ela contém:

- Sistema base
- Java
- Dependências
- Arquivo `.jar`
- Configurações necessárias

Exemplo:

```
domingosjovete/rest-with-springboot-java-app:1.0
```

Estrutura:

```
Imagem Docker

├── Java
├── Dependências
└── app.jar
```

A imagem é considerada **imutável**.

Isso significa que depois de criada ela não é alterada.

Quando a aplicação muda, uma nova imagem é criada.

---

# 🚀 O que é um container?

O container é uma instância de uma imagem Docker em execução.

Exemplo:

```
Imagem Docker
       |
       ↓
Container rodando
       |
       ↓
Aplicação disponível
```

A imagem é o modelo.

O container é a aplicação funcionando baseada nesse modelo.

---

# 🔄 Diferença entre imagem e container

| Conceito | Explicação |
|---|---|
| Imagem | Pacote da aplicação |
| Container | Aplicação executando usando uma imagem |
| Dockerfile | Arquivo com instruções para criar uma imagem |
| Docker Hub | Repositório online de imagens |

---

# 📄 Dockerfile

O Dockerfile é a receita usada pelo Docker para criar uma imagem.

Exemplo:

```dockerfile
FROM eclipse-temurin:21-jdk

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
```

Explicação:

## FROM

Define a imagem base.

Exemplo:

```dockerfile
FROM eclipse-temurin:21-jdk
```

Utiliza uma imagem contendo Java 21.

---

## ARG

Cria uma variável para localizar o arquivo `.jar`.

```dockerfile
ARG JAR_FILE=target/*.jar
```

---

## COPY

Copia o arquivo gerado pelo Maven para dentro da imagem.

```dockerfile
COPY ${JAR_FILE} app.jar
```

---

## ENTRYPOINT

Define o comando executado quando o container iniciar.

```dockerfile
ENTRYPOINT ["java","-jar","/app.jar"]
```

---

# 🔨 Criando uma imagem Docker

Dentro do projeto:

```bash
docker build -t nome-da-imagem .
```

Exemplo:

```bash
docker build -t domingosjovete/rest-with-springboot-java-app:1.0 .
```

O Docker irá:

1. Ler o Dockerfile
2. Criar as camadas da imagem
3. Adicionar o arquivo `.jar`
4. Gerar uma imagem Docker

---

# 🔎 Visualizar imagens existentes

Comando:

```bash
docker images
```

Exemplo:

```
REPOSITORY
domingosjovete/rest-with-springboot-java-app

TAG
1.0

IMAGE ID
e51027a9fe35
```

---

# ▶️ Criando um container

Executar uma imagem:

```bash
docker run nome-da-imagem
```

Exemplo:

```bash
docker run domingosjovete/rest-with-springboot-java-app:1.0
```

O Docker cria um container baseado naquela imagem.

---

# 📋 Ver containers

Containers em execução:

```bash
docker ps
```

Todos os containers:

```bash
docker ps -a
```

---

# ⛔ Parar e remover containers

Parar:

```bash
docker stop nome-container
```

Remover:

```bash
docker rm nome-container
```

---

# 🔄 Atualização de uma aplicação Docker

## ❌ O que não acontece

Quando o código muda:

```
Código novo
```

a imagem antiga:

```
Imagem 1.0
```

não muda automaticamente.

O container continua executando a versão antiga.

---

## ✅ Processo correto

Quando houver alteração no projeto:

```
Código atualizado
        ↓
Novo arquivo .jar
        ↓
Nova imagem Docker
        ↓
Nova versão
        ↓
Novo container
```

---

# Exemplo de atualização

## 1 - Alterar código Java

Exemplo:

Adicionar um novo endpoint:

```java
@GetMapping("/products")
public List<ProductDTO> findAll(){
    return service.findAll();
}
```

---

## 2 - Gerar novo JAR

Executar:

```bash
mvn clean package
```

Novo arquivo:

```
target/
 └── rest-with-springboot-java.jar
```

---

## 3 - Criar nova imagem

Criar versão:

```bash
docker build -t domingosjovete/rest-with-springboot-java-app:1.1 .
```

Agora:

```
1.0 → versão antiga

1.1 → nova versão
```

---

## 4 - Substituir container antigo

Parar:

```bash
docker stop nome-container
```

Remover:

```bash
docker rm nome-container
```

Executar nova versão:

```bash
docker run domingosjovete/rest-with-springboot-java-app:1.1
```

---

# 🌎 Docker Hub

## 📌 O que é Docker Hub?

O Docker Hub é um repositório online de imagens Docker.

Comparação:

```
GitHub
   ↓
Código fonte


Docker Hub
   ↓
Imagens Docker
```

Exemplo:

```
docker.io/domingosjovete/rest-with-springboot-java-app
```

---

# 🔐 Login no Docker Hub

Antes de enviar imagens:

```bash
docker login
```

Informar:

```
Username:
Password:
```

---

# 🏷️ Tags e versionamento

Uma imagem pode possuir várias tags.

Exemplo:

```
rest-with-springboot-java-app

latest
1.0
1.1
2.0
```

A tag representa uma versão.

---

# Criando uma versão da imagem

Imagem atual:

```
:latest
```

Criar versão:

```bash
docker tag \
domingosjovete/rest-with-springboot-java-app:latest \
domingosjovete/rest-with-springboot-java-app:1.0
```

Agora:

```
latest → e51027a9fe35

1.0 → e51027a9fe35
```

As duas tags apontam para a mesma imagem.

---

# 📤 Enviar imagem para Docker Hub

Enviar versão:

```bash
docker push domingosjovete/rest-with-springboot-java-app:1.0
```

Resultado:

```
Docker Hub

Tags:

latest
1.0
```

---

# 🔄 Atualizando uma versão no Docker Hub

Fluxo:

```
Alterar código
        ↓
mvn clean package
        ↓
docker build
        ↓
Criar nova tag
        ↓
docker push
        ↓
Docker Hub atualizado
```

---

# 📌 Exemplo de versões

```
1.0
Primeira versão publicada


1.1
Novas funcionalidades


1.2
Correções e melhorias


2.0
Mudanças grandes na aplicação
```

---

# 🏆 Boas práticas

## Não sobrescrever versões

Evite:

```
1.0
↓
Alterar o conteúdo da mesma versão
```

Prefira:

```
1.0
1.1
1.2
```

---

## Usar latest corretamente

A tag:

```
latest
```

representa a versão atual.

Exemplo:

```
latest → 1.1
```

---

## Manter histórico

Caso uma versão apresente problema:

```
Versão atual:

1.1 ❌


Voltar:

1.0 ✅
```

---

# 📚 Resumo final

## Docker

```
Código
  ↓
JAR
  ↓
Imagem Docker
  ↓
Container
```

## Docker Hub

```
Imagem local
      ↓
Tag de versão
      ↓
docker push
      ↓
Imagem publicada
```

## Regra principal

Docker não atualiza uma imagem existente.

O processo correto é:

```
Criar nova imagem
        ↓
Criar nova versão
        ↓
Enviar para Docker Hub
        ↓
Criar novo container
```