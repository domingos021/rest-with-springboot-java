# 🐳 Docker - Comandos mais usados para Imagens

## 📖 O que é uma imagem?

Uma **imagem (Image)** é um modelo pronto que contém tudo o que uma aplicação precisa para executar:

- Sistema operacional base
- Java
- Bibliotecas
- Dependências
- Sua aplicação

Ela é semelhante a um **arquivo de instalação**.

Exemplo:

```
Imagem
   │
   ├── Ubuntu
   ├── Java 21
   ├── Spring Boot
   └── Meu projeto
```

Quando executamos uma imagem, o Docker cria um **Container**.

```
Imagem  ─────────►  Container
(modelo)           (aplicação executando)
```

---

# 📌 Comandos mais usados para imagens

---

## 1. Listar todas as imagens

### Comando

```bash
docker image ls
```

ou

```bash
docker images
```

### O que faz?

Mostra todas as imagens armazenadas no computador.

Exemplo:

```
REPOSITORY                  TAG       IMAGE ID       CREATED       SIZE
postgres                    16        a12bc34de56   2 days ago    438MB
rest-with-springboot-java   latest    8ab3c67d891   5 minutes     520MB
```

### Quando usar?

- Ver quais imagens existem.
- Conferir se a imagem foi criada.
- Descobrir o nome da imagem.

---

## 2. Construir uma imagem

### Comando

```bash
docker image build -t nome-da-imagem .
```

Exemplo:

```bash
docker image build -t rest-with-springboot-java .
```

### O que faz?

Lê o **Dockerfile** e cria uma nova imagem.

### Quando usar?

Sempre que:

- alterar o Dockerfile;
- gerar um novo JAR;
- atualizar sua aplicação.

---

## 3. Baixar uma imagem da Internet

### Comando

```bash
docker image pull nome:tag
```

Exemplo:

```bash
docker image pull postgres:16
```

### O que faz?

Baixa uma imagem do Docker Hub.

### Quando usar?

Quando ainda não possui a imagem.

---

## 4. Ver detalhes de uma imagem

### Comando

```bash
docker image inspect nome-da-imagem
```

Exemplo

```bash
docker image inspect postgres
```

ou

```bash
docker image inspect rest-with-springboot-java
```

### O que faz?

Mostra informações completas da imagem.

Exemplo:

- ID
- tamanho
- arquitetura
- camadas
- data de criação
- configuração

### Quando usar?

Quando precisar analisar uma imagem.

---

## 5. Ver o histórico da imagem

### Comando

```bash
docker image history nome-da-imagem
```

Exemplo

```bash
docker image history postgres
```

### O que faz?

Mostra cada camada utilizada para construir a imagem.

### Quando usar?

Para entender como a imagem foi criada.

Muito útil durante estudos.

---

## 6. Remover uma imagem

### Comando

```bash
docker image rm nome-da-imagem
```

Exemplo

```bash
docker image rm rest-with-springboot-java
```

### O que faz?

Remove uma imagem do computador.

### Quando usar?

Quando ela não será mais utilizada ou deseja recriá-la do zero.

---

## 7. Remover imagens não utilizadas

### Comando

```bash
docker image prune
```

### O que faz?

Remove imagens que não estão sendo utilizadas.

### Quando usar?

Para liberar espaço em disco.

---

## 8. Salvar uma imagem em arquivo

### Comando

```bash
docker image save -o backup.tar nome-da-imagem
```

Exemplo

```bash
docker image save -o postgres.tar postgres
```

### O que faz?

Exporta a imagem para um arquivo `.tar`.

### Quando usar?

- Fazer backup.
- Levar para outro computador.

---

## 9. Restaurar uma imagem salva

### Comando

```bash
docker image load -i backup.tar
```

### O que faz?

Importa uma imagem salva anteriormente.

### Quando usar?

Quando recebeu uma imagem em arquivo.

---

## 10. Dar outro nome para uma imagem

### Comando

```bash
docker image tag imagem-antiga imagem-nova
```

Exemplo

```bash
docker image tag postgres:16 meu-postgres:1.0
```

### O que faz?

Cria uma nova referência para a mesma imagem.

### Quando usar?

Antes de enviar uma imagem para um registro ou organizar versões.

---

## 11. Enviar uma imagem para um Registry

### Comando

```bash
docker image push nome-da-imagem
```

Exemplo

```bash
docker image push usuario/rest-with-springboot-java:1.0
```

### O que faz?

Envia uma imagem para um Registry (Docker Hub, GitHub Container Registry, etc.).

### Quando usar?

Quando deseja compartilhar ou publicar sua imagem.

---

# 📋 Fluxo mais comum durante o desenvolvimento

## 1º Gerar o JAR

```bash
mvn clean package
```

↓

## 2º Construir a imagem

```bash
docker image build -t rest-with-springboot-java .
```

↓

## 3º Conferir se a imagem foi criada

```bash
docker image ls
```

↓

## 4º Executar a imagem

```bash
docker run rest-with-springboot-java
```

ou

```bash
docker compose up
```

---

# 📚 Resumo

| Comando | Serve para |
|----------|------------|
| `docker image ls` | Listar imagens |
| `docker image build` | Criar imagem |
| `docker image pull` | Baixar imagem |
| `docker image inspect` | Ver detalhes da imagem |
| `docker image history` | Ver histórico das camadas |
| `docker image rm` | Remover imagem |
| `docker image prune` | Remover imagens não utilizadas |
| `docker image save` | Exportar imagem |
| `docker image load` | Importar imagem |
| `docker image tag` | Renomear ou versionar imagem |
| `docker image push` | Enviar imagem para um Registry |