# Docker - Guia Completo

> Material de estudo
>
> Objetivo:
> Entender o Docker de forma profissional, desde os conceitos até os comandos mais utilizados no dia a dia.

---

# Sumário

1. O que é Docker
2. Problema que o Docker resolve
3. Máquina Virtual x Docker
4. Conceitos Fundamentais
5. Arquitetura do Docker
6. Instalação
7. Verificando a instalação
8. Comandos básicos
9. Trabalhando com imagens
10. Trabalhando com containers
11. Logs
12. Execução de comandos
13. Volumes
14. Redes
15. Docker Compose
16. Limpeza do Docker
17. Fluxo de trabalho completo
18. Comandos mais utilizados

---

# O que é Docker

Docker é uma plataforma que permite criar, distribuir e executar aplicações dentro de ambientes isolados chamados **containers**.

Um container possui tudo que a aplicação precisa para funcionar:

- código
- bibliotecas
- dependências
- configurações
- runtime

Assim, a aplicação funciona da mesma forma em qualquer computador.

Exemplo:

Meu computador
↓

Java 21
Spring Boot
PostgreSQL

↓

Servidor Linux

Java 21
Spring Boot
PostgreSQL

↓

Cloud

Java 21
Spring Boot
PostgreSQL

Tudo funciona exatamente igual.

---

# Problema que o Docker resolve

Antes do Docker era comum ouvir:

> "Na minha máquina funciona."

Isso acontecia porque:

- versões diferentes do Java
- versões diferentes do banco
- bibliotecas incompatíveis
- configurações diferentes

Docker elimina esse problema.

O ambiente inteiro é empacotado.

---

# Máquina Virtual x Docker

## Máquina Virtual

```
Hardware

↓

Sistema Operacional

↓

Hypervisor

↓

VM

↓

Sistema Operacional

↓

Aplicação
```

Cada VM possui um sistema operacional inteiro.

Consome bastante memória.

Inicializa lentamente.

---

## Docker

```
Hardware

↓

Sistema Operacional

↓

Docker Engine

↓

Container

↓

Aplicação
```

O container compartilha o Kernel do sistema operacional.

Por isso:

- mais leve
- mais rápido
- menos memória

---

# Conceitos Fundamentais

## Docker Engine

É o software responsável por criar e gerenciar containers.

Sem ele o Docker não funciona.

---

## Image (Imagem)

Uma imagem é um modelo pronto para criar containers.

Ela é somente leitura.

Pode ser comparada a uma classe em Java.

Exemplo:

```
Classe Pessoa
```

A classe não existe em memória.

Ela apenas define como um objeto será criado.

Da mesma forma:

```
Imagem Ubuntu
```

Define como será criado um container Ubuntu.

---

## Container

É uma instância de uma imagem.

Analogia:

Classe

↓

Objeto

Imagem Docker

↓

Container

---

## Docker Hub

É o repositório oficial de imagens.

Semelhante ao Maven Central.

Exemplos:

- postgres
- mysql
- nginx
- redis
- openjdk

---

## Volume

Permite armazenar dados fora do container.

Mesmo apagando o container, os dados permanecem.

Muito usado para bancos de dados.

---

## Network

Permite comunicação entre containers.

Exemplo:

```
Spring Boot

↓

PostgreSQL

↓

Redis
```

Todos conversam através de uma rede Docker.

---

# Arquitetura do Docker

```
Cliente Docker

↓

Docker Engine

↓

Imagens

↓

Containers

↓

Volumes

↓

Networks
```

---

# Instalação

Windows

Baixar:

Docker Desktop

Após instalar:

Abrir o Docker Desktop

Esperar aparecer:

Running

---

# Verificando a instalação

## Verificar versão

```bash
docker --version
```

Exemplo

```
Docker version 28.3.2
```

---

## Informações completas

```bash
docker info
```

Mostra:

- versão
- memória
- CPUs
- containers
- imagens
- volumes
- redes
- sistema operacional

---

## Ajuda geral

```bash
docker --help
```

Lista todos os comandos disponíveis.

---

# Trabalhando com imagens

## Baixar uma imagem

```bash
docker pull postgres
```

Baixa a imagem oficial.

---

## Listar imagens

```bash
docker images
```

ou

```bash
docker image ls
```

Exemplo

```
postgres

mysql

redis
```

---

## Remover imagem

```bash
docker rmi postgres
```

---

## Procurar imagens

```bash
docker search postgres
```

---

# Trabalhando com Containers

## Criar um container

```bash
docker run postgres
```

---

## Criar e dar nome

```bash
docker run --name banco postgres
```

---

## Executar em segundo plano

```bash
docker run -d postgres
```

-d = detached

Executa em background.

---

## Mapear porta

```bash
docker run -p 5432:5432 postgres
```

Formato

```
porta_host:porta_container
```

---

## Definir variável de ambiente

```bash
docker run \
-e POSTGRES_PASSWORD=123456 \
postgres
```

---

## Criar tudo junto

```bash
docker run \
-d \
--name postgres-db \
-p 5432:5432 \
-e POSTGRES_PASSWORD=123456 \
postgres
```

---

## Listar containers ativos

```bash
docker ps
```

---

## Listar todos

```bash
docker ps -a
```

---

## Iniciar container

```bash
docker start postgres-db
```

---

## Parar container

```bash
docker stop postgres-db
```

---

## Reiniciar

```bash
docker restart postgres-db
```

---

## Pausar

```bash
docker pause postgres-db
```

---

## Continuar

```bash
docker unpause postgres-db
```

---

## Remover container

```bash
docker rm postgres-db
```

---

## Remover container em execução

```bash
docker rm -f postgres-db
```

---

# Logs

Ver logs

```bash
docker logs postgres-db
```

---

Acompanhar logs em tempo real

```bash
docker logs -f postgres-db
```

---

Últimas linhas

```bash
docker logs --tail 100 postgres-db
```

---

# Executar comandos dentro do container

Abrir terminal

```bash
docker exec -it postgres-db bash
```

ou

```bash
docker exec -it postgres-db sh
```

---

Executar comando

```bash
docker exec postgres-db ls
```

---

# Volumes

Criar volume

```bash
docker volume create postgres-data
```

---

Listar

```bash
docker volume ls
```

---

Inspecionar

```bash
docker volume inspect postgres-data
```

---

Remover

```bash
docker volume rm postgres-data
```

---

Usar volume

```bash
docker run \
-v postgres-data:/var/lib/postgresql/data \
postgres
```

---

# Networks

Listar

```bash
docker network ls
```

---

Criar

```bash
docker network create minha-rede
```

---

Inspecionar

```bash
docker network inspect minha-rede
```

---

Remover

```bash
docker network rm minha-rede
```

---

Conectar container

```bash
docker network connect minha-rede postgres-db
```

---

# Docker Compose

Permite subir vários containers ao mesmo tempo.

Exemplo:

- Spring Boot
- PostgreSQL
- Redis

Tudo com um único comando.

---

Subir

```bash
docker compose up
```

---

Subir em background

```bash
docker compose up -d
```

---

Parar

```bash
docker compose down
```

---

Reconstruir imagens

```bash
docker compose up --build
```

---

# Limpeza

Remover containers parados

```bash
docker container prune
```

---

Remover imagens sem uso

```bash
docker image prune
```

---

Remover volumes sem uso

```bash
docker volume prune
```

---

Remover redes sem uso

```bash
docker network prune
```

---

Limpeza completa

```bash
docker system prune
```

Com volumes

```bash
docker system prune -a --volumes
```

---

# Fluxo de trabalho completo

```
docker pull postgres

↓

docker images

↓

docker run

↓

docker ps

↓

docker logs

↓

docker exec

↓

docker stop

↓

docker start

↓

docker rm
```

---

# Comandos mais utilizados

| Comando | Função |
|----------|--------|
| docker --version | Mostra a versão do Docker |
| docker info | Informações completas |
| docker images | Lista imagens |
| docker pull | Baixa uma imagem |
| docker search | Procura imagens |
| docker rmi | Remove imagem |
| docker run | Cria container |
| docker ps | Lista containers ativos |
| docker ps -a | Lista todos os containers |
| docker start | Inicia container |
| docker stop | Para container |
| docker restart | Reinicia container |
| docker rm | Remove container |
| docker exec | Executa comandos dentro do container |
| docker logs | Visualiza logs |
| docker volume ls | Lista volumes |
| docker network ls | Lista redes |
| docker compose up | Inicia serviços |
| docker compose down | Finaliza serviços |
| docker system prune | Limpa recursos não utilizados |

---

# Resumo

Docker permite criar ambientes isolados e reproduzíveis através de containers.

Principais conceitos:

- Docker Engine → responsável por executar os containers.
- Image → modelo para criação de containers.
- Container → instância em execução de uma imagem.
- Volume → persistência de dados.
- Network → comunicação entre containers.
- Docker Hub → repositório oficial de imagens.

Com esses conceitos é possível executar aplicações completas de forma consistente em qualquer ambiente.