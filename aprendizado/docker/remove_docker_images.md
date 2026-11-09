# 🐳 Remover Imagens Docker

## 📋 Listar todas as imagens

```bash
docker images
```

ou

```bash
docker image ls
```
1. Veja informações do container

```bash
docker ps -a

Você verá algo parecido com:

CONTAINER ID   IMAGE               STATUS
a8017a5e8d6b   kindest/node:v1.33.4 Exited (...)
```

2. Remova o container

```bash
docker rm a8017a5e8d6b

```

---

Exepmlo:

deseja-se remover a imagem `kindest/node:v1.33.4` 

Primeiro, você precisa-se descobrir o container que está utilizando a imagem, para isso, execute:

```bash
docker ps -a --filter "ancestor=kindest/node:v1.33.4"

Se existir um container usando essa imagem, aparecerá algo como:
CONTAINER ID   IMAGE                    STATUS
a8017a5e8d6b   kindest/node:v1.33.4     Exited (0) ...
```
2. Remover somente esse container

```bash
docker rm a8017a5e8d6b
```
2. Remova o container

```bash
docker rm 569c4128dfa6
```

3. Agora remova a imagem

```bash
 docker rmi kindest/node:v1.33.4
```

---

Exepmlo:

```bash
docker rm 569c4128dfa6
```

## 🗑️ Remover uma imagem

Pelo nome e tag:


```bash
docker rmi NOME_DA_IMAGEM:TAG

Ex:docker rmi kindest/node:v1.33.4
docker rmi mysql:latest

Pelo ID da imagem
docker rmi 569c4128dfa6

Remover as duas de uma vez
Pelo nome:
docker rmi mysql:latest kindest/node:v1.33.4

Pelo ID
docker rmi 569c4128dfa6 25a6018e48df
```

Exemplo:

```bash
docker rmi dinisjovete/rest-with-springboot-java:1.0
```

Ou pelo ID da imagem:

```bash
docker rmi IMAGE_ID
```

Exemplo:

```bash
docker rmi a1b2c3d4e5f6
```

---

## 🗑️ Remover várias imagens

```bash
docker rmi imagem1 imagem2 imagem3
```

Exemplo:

```bash
docker rmi \
dinisjovete/rest-with-springboot-java:0.9 \
dinisjovete/rest-with-springboot-java:1.0
```

---

## 🧹 Remover imagens sem uso (dangling)

```bash
docker image prune
```

---

## 🧹 Remover todas as imagens que não estão sendo utilizadas por nenhum container

```bash
docker image prune -a
```

---

## ⚠️ Se a imagem estiver sendo utilizada

Verifique os containers:

```bash
docker ps -a
```

Remova o container:

```bash
docker rm CONTAINER_ID
```

Depois remova a imagem:

```bash
docker rmi NOME_DA_IMAGEM
```

---

## 💪 Forçar a remoção de uma imagem

```bash
docker rmi -f NOME_DA_IMAGEM
```

---

## 🚀 Fluxo mais utilizado

```bash
# Listar imagens
docker images

# Listar containers
docker ps -a

# Remover o container (caso exista)
docker rm CONTAINER_ID

# Remover a imagem
docker rmi NOME_DA_IMAGEM

# Confirmar
docker images
```