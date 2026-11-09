# Gerenciamento de Containers com Docker

> Guia prático e profissional: conceitos, passo a passo, comandos essenciais, boas práticas e troubleshooting.

---

## Sumário

1. [Introdução](#1-introdução)
2. [Conceitos fundamentais](#2-conceitos-fundamentais)
3. [Ciclo de vida de um container](#3-ciclo-de-vida-de-um-container)
4. [Instalação e verificação](#4-instalação-e-verificação)
5. [Passo a passo: do zero ao primeiro container](#5-passo-a-passo-do-zero-ao-primeiro-container)
6. [Comandos essenciais](#6-comandos-essenciais)
7. [Dockerfile: construindo imagens](#7-dockerfile-construindo-imagens)
8. [Volumes e persistência de dados](#8-volumes-e-persistência-de-dados)
9. [Redes](#9-redes)
10. [Docker Compose: múltiplos containers](#10-docker-compose-múltiplos-containers)
11. [Variáveis de ambiente e segredos](#11-variáveis-de-ambiente-e-segredos)
12. [Logs, monitoramento e limites de recursos](#12-logs-monitoramento-e-limites-de-recursos)
13. [Segurança](#13-segurança)
14. [Troubleshooting](#14-troubleshooting)
15. [Quando usar e quando não usar containers](#15-quando-usar-e-quando-não-usar-containers)
16. [Cheat sheet](#16-cheat-sheet)
17. [Checklist de boas práticas](#17-checklist-de-boas-práticas)
18. [Referências](#18-referências)

---

## 1. Introdução

Um **container** é uma unidade leve e isolada que empacota uma aplicação junto com tudo o que ela precisa para rodar (runtime, bibliotecas, dependências e configurações). O objetivo é resolver o clássico problema do *"na minha máquina funciona"*.

**Gerenciar containers** significa dominar todo o ciclo: construir imagens, executar, conectar, persistir dados, monitorar, atualizar e remover com segurança.

### Por que usar containers?

| Benefício | Descrição |
|---|---|
| **Portabilidade** | A mesma imagem roda em qualquer ambiente com um container runtime (dev, CI, produção). |
| **Consistência** | Elimina diferenças entre ambientes. |
| **Isolamento** | Cada aplicação roda em seu próprio espaço, sem conflitar dependências. |
| **Leveza** | Compartilham o kernel do host; sobem em segundos. |
| **Escalabilidade** | Fácil de replicar e orquestrar (Compose, Swarm, Kubernetes). |
| **Reprodutibilidade** | Infraestrutura descrita como código (Dockerfile, Compose). |

---

## 2. Conceitos fundamentais

### 2.1 Container vs Máquina Virtual

```
Máquina Virtual                         Container
┌─────────┐ ┌─────────┐                 ┌─────────┐ ┌─────────┐
│  App A  │ │  App B  │                 │  App A  │ │  App B  │
├─────────┤ ├─────────┤                 ├─────────┤ ├─────────┤
│ Libs    │ │ Libs    │                 │ Libs    │ │ Libs    │
├─────────┤ ├─────────┤                 └────┬────┘ └────┬────┘
│ SO Guest│ │ SO Guest│                 ┌────┴───────────┴────┐
├─────────┴─┴─────────┤                 │   Container Runtime │
│     Hypervisor      │                 ├─────────────────────┤
├─────────────────────┤                 │   SO Host (kernel)  │
│      SO Host        │                 ├─────────────────────┤
└─────────────────────┘                 │      Hardware       │
                                        └─────────────────────┘
```

| Critério | Máquina Virtual | Container |
|---|---|---|
| Isolamento | Forte (kernel próprio) | Médio (kernel compartilhado) |
| Tamanho | GBs | MBs |
| Inicialização | Minutos | Segundos |
| Overhead | Alto | Baixo |
| Caso típico | Isolamento total, SOs diferentes | Microsserviços, apps, CI/CD |

### 2.2 Glossário essencial

| Termo | O que é |
|---|---|
| **Imagem (image)** | Template imutável e somente leitura, composto por camadas. É a "receita pronta". |
| **Container** | Instância em execução de uma imagem (imagem + camada gravável). |
| **Dockerfile** | Arquivo de texto com as instruções para construir uma imagem. |
| **Registry** | Repositório de imagens (Docker Hub, GHCR, ECR, GCR, registry privado). |
| **Tag** | Rótulo de versão da imagem (`postgres:16`, `app:1.0.3`). |
| **Camada (layer)** | Cada instrução do Dockerfile gera uma camada em cache e reaproveitável. |
| **Volume** | Armazenamento persistente gerenciado pelo Docker, independente do ciclo de vida do container. |
| **Bind mount** | Pasta do host montada dentro do container. |
| **Network** | Rede virtual que conecta containers entre si e com o exterior. |
| **Docker Compose** | Ferramenta para definir e rodar aplicações multi-container via YAML. |
| **Daemon** | Serviço (`dockerd`) que gerencia imagens, containers, redes e volumes. |
| **Docker CLI** | Cliente de linha de comando que conversa com o daemon. |

### 2.3 Como o isolamento funciona (resumo técnico)

- **Namespaces**: isolam o que o container *enxerga* (processos, rede, filesystem, usuários, hostname).
- **cgroups**: limitam o que o container *pode usar* (CPU, memória, I/O).
- **Union filesystem (overlayfs)**: empilha camadas da imagem em um único sistema de arquivos.

---

## 3. Ciclo de vida de um container

```
        docker pull / build
              │
              ▼
          [ Imagem ]
              │  docker create
              ▼
          [ Created ] ──docker start──► [ Running ] ◄──┐
                                          │  │  │       │ docker unpause
                       docker pause ──────┘  │  └──► [ Paused ]
                                             │
                    docker stop / kill       │ processo principal termina
                                             ▼
                                         [ Exited ]
                                             │ docker rm
                                             ▼
                                         [ Removido ]
```

**Ponto-chave:** um container vive enquanto seu **processo principal (PID 1)** estiver rodando. Se o processo termina, o container para.

---

## 4. Instalação e verificação

### Linux (Ubuntu/Debian), método recomendado

```bash
# Remover versões antigas (se existirem)
sudo apt remove docker docker-engine docker.io containerd runc

# Instalar via script oficial (ambiente de estudo/dev)
curl -fsSL https://get.docker.com | sudo sh

# Permitir usar docker sem sudo (reinicie a sessão depois)
sudo usermod -aG docker $USER
```

> Para produção, prefira o repositório oficial `apt` da Docker, conforme a documentação, em vez do script.

### Windows / macOS

Instale o **Docker Desktop** (no Windows com backend WSL 2).

### Verificar a instalação

```bash
docker --version          # versão do cliente
docker compose version    # versão do Compose (plugin v2)
docker info               # informações do daemon
docker run hello-world    # teste completo (baixa e roda uma imagem de teste)
```

---

## 5. Passo a passo: do zero ao primeiro container

### Passo 1: Baixar uma imagem

```bash
docker pull nginx:stable
```

### Passo 2: Executar um container

```bash
docker run -d --name meu-nginx -p 8080:80 nginx:stable
```

- `-d`: roda em segundo plano (*detached*)
- `--name`: nome amigável
- `-p 8080:80`: mapeia a porta `8080` do host para a `80` do container

### Passo 3: Verificar que está rodando

```bash
docker ps
```

Acesse `http://localhost:8080`.

### Passo 4: Ver logs

```bash
docker logs -f meu-nginx
```

### Passo 5: Entrar no container

```bash
docker exec -it meu-nginx sh
```

### Passo 6: Parar e remover

```bash
docker stop meu-nginx
docker rm meu-nginx
```

### Passo 7: Limpar a imagem (opcional)

```bash
docker rmi nginx:stable
```

---

## 6. Comandos essenciais

> Legenda: ✅ **Quando usar** · ⚠️ **Quando evitar / cuidado**

### 6.1 Imagens

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker pull <img>:<tag>` | Baixa uma imagem do registry | Antes de rodar, para garantir a versão | Evite `:latest` em produção (não é reproduzível) |
| `docker images` | Lista imagens locais | Ver o que está armazenado e tamanhos | n/a |
| `docker build -t nome:tag .` | Constrói imagem a partir do Dockerfile | Ao criar/atualizar sua aplicação | Cuidado com o contexto (`.`): use `.dockerignore` |
| `docker tag origem destino` | Cria um novo nome/tag para a imagem | Antes de enviar a um registry | n/a |
| `docker push <img>:<tag>` | Envia imagem ao registry | Publicar versão para outros ambientes | Nunca envie imagens com segredos embutidos |
| `docker history <img>` | Mostra camadas e tamanhos | Investigar por que a imagem está grande | n/a |
| `docker inspect <img>` | Metadados detalhados (JSON) | Ver variáveis, portas, entrypoint | n/a |
| `docker rmi <img>` | Remove imagem local | Liberar espaço | Falha se houver container usando a imagem |
| `docker image prune -a` | Remove todas as imagens não usadas | Limpeza geral de disco | Apaga imagens que você talvez precise offline |

### 6.2 Containers: criação e execução

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker run <img>` | Cria e inicia um container | Subir qualquer serviço ou tarefa | Sem `--name` e sem `--rm`, você acumula containers órfãos |
| `docker run -d` | Roda em segundo plano | Serviços (APIs, bancos, web servers) | Sem logs na tela; use `docker logs` |
| `docker run -it <img> sh` | Modo interativo com terminal | Explorar uma imagem, depurar | Não use para serviços permanentes |
| `docker run --rm` | Remove o container ao terminar | Tarefas pontuais, testes, scripts | Você perde o container para inspecionar depois |
| `docker run -p H:C` | Publica porta (host:container) | Expor um serviço ao host | Não publique portas sensíveis (ex.: banco) em `0.0.0.0` sem necessidade |
| `docker run -e VAR=valor` | Define variável de ambiente | Configuração por ambiente | Não passe segredos pela linha de comando (ficam no histórico) |
| `docker run -v vol:/path` | Monta volume/bind mount | Persistir dados | Bind mount pode causar problemas de permissão |
| `docker run --network <rede>` | Conecta a uma rede específica | Comunicação entre containers | n/a |
| `docker run --restart unless-stopped` | Política de reinício | Serviços que devem subir após reboot | Pode mascarar crash loops; monitore |
| `docker create <img>` | Cria sem iniciar | Preparar antes de iniciar | Raramente necessário no dia a dia |

### 6.3 Containers: gerenciamento

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker ps` | Lista containers em execução | Verificar o estado atual | n/a |
| `docker ps -a` | Lista todos (inclusive parados) | Achar containers que saíram com erro | n/a |
| `docker start <c>` | Inicia container parado | Retomar um container existente | n/a |
| `docker stop <c>` | Para com SIGTERM (graceful), depois SIGKILL | **Forma padrão** de parar | n/a |
| `docker restart <c>` | Reinicia | Após mudar configuração | Derruba conexões ativas |
| `docker kill <c>` | Envia SIGKILL imediatamente | Container travado, que não responde ao `stop` | Pode corromper dados (sem shutdown limpo) |
| `docker pause / unpause <c>` | Congela / retoma processos | Diagnóstico pontual | Não é para uso contínuo |
| `docker rm <c>` | Remove container parado | Limpeza | Dados na camada do container são perdidos (use volumes) |
| `docker rm -f <c>` | Força remoção (mesmo rodando) | Remoção rápida em dev | Evite em produção, pois não faz shutdown gracioso |
| `docker rename <c> <novo>` | Renomeia | Organização | n/a |

### 6.4 Containers: inspeção e depuração

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker logs <c>` | Exibe a saída (stdout/stderr) | Primeiro passo de qualquer diagnóstico | Logs crescem sem limite se não configurar rotação |
| `docker logs -f --tail 100 <c>` | Acompanha logs em tempo real | Depurar ao vivo | n/a |
| `docker exec -it <c> sh` | Abre shell dentro do container em execução | Investigar arquivos, rede, processos | **Não** use para "consertar" produção; mudanças se perdem. Corrija na imagem |
| `docker exec <c> <comando>` | Executa um comando pontual | Rodar migrations, scripts, `psql` | n/a |
| `docker inspect <c>` | Configuração completa (IP, mounts, env) | Descobrir IP, volumes, variáveis | Pode expor segredos em variáveis de ambiente |
| `docker stats` | Uso de CPU/RAM/rede em tempo real | Identificar gargalos | n/a |
| `docker top <c>` | Processos rodando no container | Ver o que está ativo | n/a |
| `docker cp <c>:/path ./local` | Copia arquivos entre host e container | Extrair logs/arquivos | Não use como forma de deploy |
| `docker diff <c>` | Mostra alterações no filesystem | Ver o que mudou desde a imagem | n/a |
| `docker events` | Stream de eventos do daemon | Auditoria e depuração avançada | n/a |

### 6.5 Volumes

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker volume create <nome>` | Cria volume | Dados de banco, uploads | n/a |
| `docker volume ls` | Lista volumes | Auditar o que existe | n/a |
| `docker volume inspect <nome>` | Detalhes e ponto de montagem | Localizar dados | n/a |
| `docker volume rm <nome>` | Remove volume | Descartar dados que não são mais necessários | **Perda definitiva de dados** |
| `docker volume prune` | Remove volumes não usados | Limpeza em dev | **Perigoso em produção** |

### 6.6 Redes

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker network create <rede>` | Cria rede bridge customizada | Isolar e permitir DNS entre containers por nome | n/a |
| `docker network ls` | Lista redes | Auditar | n/a |
| `docker network inspect <rede>` | Mostra containers e IPs da rede | Depurar conectividade | n/a |
| `docker network connect/disconnect` | Conecta/desconecta container a uma rede | Ajustes sem recriar o container | n/a |
| `docker network rm <rede>` | Remove rede | Limpeza | Falha se houver containers conectados |

### 6.7 Limpeza e diagnóstico do sistema

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker system df` | Mostra uso de disco | Descobrir o que ocupa espaço | n/a |
| `docker container prune` | Remove todos os containers parados | Limpeza periódica | Remove containers que você possa querer inspecionar |
| `docker system prune` | Remove containers parados, redes e imagens *dangling* | Limpeza rotineira em dev | Confira antes em ambientes compartilhados |
| `docker system prune -a --volumes` | Remove **tudo** que não está em uso, inclusive volumes | Reset total de ambiente de dev | **Nunca em produção**: apaga dados |

### 6.8 Docker Compose

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker compose up -d` | Cria e inicia todos os serviços | Subir o ambiente completo | n/a |
| `docker compose up -d --build` | Recompila imagens antes de subir | Após mudar código/Dockerfile | Mais lento |
| `docker compose down` | Para e remove containers e redes | Encerrar o ambiente | Mantém volumes |
| `docker compose down -v` | Idem, **removendo volumes** | Reset total de dev | **Apaga dados do banco** |
| `docker compose ps` | Estado dos serviços | Verificar saúde | n/a |
| `docker compose logs -f <serviço>` | Logs de um serviço | Depuração | n/a |
| `docker compose exec <serviço> sh` | Shell em um serviço | Investigar | n/a |
| `docker compose restart <serviço>` | Reinicia um serviço | Aplicar mudanças simples | n/a |
| `docker compose config` | Valida e mostra o YAML final resolvido | Depurar variáveis e sintaxe | n/a |
| `docker compose pull` | Atualiza as imagens dos serviços | Atualizar versões | Pode trazer versão inesperada se usar `latest` |

---

## 7. Dockerfile: construindo imagens

### 7.1 Instruções principais

| Instrução | Função |
|---|---|
| `FROM` | Imagem base |
| `WORKDIR` | Define o diretório de trabalho |
| `COPY` / `ADD` | Copia arquivos para a imagem (prefira `COPY`) |
| `RUN` | Executa comandos durante o **build** |
| `ENV` | Define variáveis de ambiente |
| `ARG` | Variáveis disponíveis só no build |
| `EXPOSE` | Documenta a porta usada (não publica) |
| `USER` | Define o usuário de execução |
| `CMD` | Comando padrão ao iniciar (pode ser sobrescrito) |
| `ENTRYPOINT` | Executável fixo do container |
| `HEALTHCHECK` | Verifica se a aplicação está saudável |

### 7.2 Exemplo: aplicação Java com multi-stage build

```dockerfile
# ---------- Estágio 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom primeiro para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Estágio 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuário sem privilégios
RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Por que multi-stage?** A imagem final contém apenas o necessário para executar (JRE + JAR), sem Maven, código-fonte ou cache de build. Resultado: imagem menor, mais rápida e mais segura.

### 7.3 `.dockerignore`

Evita enviar arquivos desnecessários ao contexto de build.

```
.git
node_modules
target
*.log
.env
.idea
.vscode
```

### 7.4 Boas práticas de Dockerfile

- Use imagens base **oficiais e enxutas** (`-slim`, `-alpine`, `distroless`).
- **Fixe versões** (`node:20.11-alpine`), evitando `latest`.
- Ordene as instruções das que **menos mudam** para as que **mais mudam** (otimiza cache).
- Combine comandos `RUN` relacionados e limpe caches no mesmo `RUN`.
- Rode como **usuário não-root**.
- Use **multi-stage builds**.
- Nunca coloque **segredos** no Dockerfile.
- Um processo principal por container.

---

## 8. Volumes e persistência de dados

Por padrão, tudo o que é gravado dentro de um container **se perde** quando ele é removido. Para persistir, use:

| Tipo | Sintaxe | Quando usar | Quando evitar |
|---|---|---|---|
| **Volume nomeado** | `-v dados:/var/lib/postgresql/data` | Bancos de dados, dados de produção | n/a (recomendado) |
| **Bind mount** | `-v $(pwd)/src:/app/src` | Desenvolvimento (hot reload do código) | Produção (depende do caminho do host, problemas de permissão) |
| **tmpfs** | `--tmpfs /tmp` | Dados temporários/sensíveis só em memória | Dados que precisam sobreviver |

```bash
# Criar e usar um volume
docker volume create pgdata
docker run -d --name db -v pgdata:/var/lib/postgresql/data postgres:16

# Backup de um volume
docker run --rm -v pgdata:/dados -v $(pwd):/backup alpine \
  tar czf /backup/pgdata-backup.tar.gz -C /dados .
```

---

## 9. Redes

### 9.1 Drivers principais

| Driver | Descrição | Uso típico |
|---|---|---|
| **bridge** (padrão) | Rede privada interna no host | Containers na mesma máquina |
| **host** | Usa diretamente a rede do host (Linux) | Máxima performance, sem isolamento de rede |
| **none** | Sem rede | Isolamento total |
| **overlay** | Rede entre múltiplos hosts | Swarm / clusters |

### 9.2 Regra de ouro

Use **redes customizadas** (não a bridge padrão). Nelas, os containers se encontram **pelo nome do serviço** via DNS interno.

```bash
docker network create minha-rede
docker run -d --name db  --network minha-rede postgres:16
docker run -d --name api --network minha-rede -e DB_HOST=db minha-api:1.0
# A API acessa o banco em "db:5432"
```

> **Importante:** dentro de uma rede Docker, containers se comunicam pela **porta interna** do serviço. O `-p` (publicação de porta) só é necessário para acesso **a partir do host**.

---

## 10. Docker Compose: múltiplos containers

Exemplo completo: API + banco PostgreSQL com healthcheck, volume e rede.

```yaml
services:
  db:
    image: postgres:16-alpine
    container_name: app-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - backend
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    build: .
    container_name: app-api
    restart: unless-stopped
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/${DB_NAME}
      SPRING_DATASOURCE_USERNAME: ${DB_USER}
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
    ports:
      - "8080:8080"
    networks:
      - backend

volumes:
  pgdata:

networks:
  backend:
```

Arquivo `.env` (nunca versionar):

```
DB_NAME=appdb
DB_USER=appuser
DB_PASSWORD=troque_esta_senha
```

Uso:

```bash
docker compose up -d --build
docker compose ps
docker compose logs -f api
docker compose down
```

**Pontos de atenção:**

- `depends_on` com `condition: service_healthy` garante que a API só sobe quando o banco estiver **pronto**, e não apenas iniciado.
- Dentro do Compose, use o **nome do serviço** (`db`) como host, nunca `localhost`.
- Adicione `.env` ao `.gitignore` e forneça um `.env.example` sem valores reais.

---

## 11. Variáveis de ambiente e segredos

| Abordagem | Segurança | Indicação |
|---|---|---|
| Valores fixos no Dockerfile/Compose | ❌ Ruim | Nunca para segredos |
| Arquivo `.env` (fora do Git) | ⚠️ Razoável | Desenvolvimento local |
| `env_file:` no Compose | ⚠️ Razoável | Dev/homologação |
| Docker/Swarm Secrets, Kubernetes Secrets | ✅ Bom | Produção |
| Gerenciadores de segredos (Vault, AWS Secrets Manager) | ✅ Melhor | Produção em escala |

**Regras:**

- Nunca versionar senhas, tokens ou chaves.
- Nunca usar `ARG`/`ENV` para segredos no build (ficam registrados nas camadas da imagem).
- Rotacionar credenciais caso tenham sido expostas.

---

## 12. Logs, monitoramento e limites de recursos

### 12.1 Logs

```bash
docker logs -f --tail 200 --since 10m meu-container
```

Configure rotação para evitar disco cheio:

```yaml
services:
  api:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

### 12.2 Monitoramento rápido

```bash
docker stats                 # CPU, memória, rede, I/O em tempo real
docker system df             # uso de disco
docker top <container>       # processos internos
```

Para produção, use ferramentas dedicadas: **Prometheus + Grafana**, **cAdvisor**, **Loki**, **Datadog**.

### 12.3 Limites de recursos

Sem limites, um container pode consumir todos os recursos do host.

```bash
docker run -d --name api \
  --memory=512m --cpus=1.0 \
  minha-api:1.0
```

```yaml
services:
  api:
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 512M
```

### 12.4 Healthcheck

```dockerfile
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
```

Verifique com `docker ps` (coluna *STATUS*: `healthy` / `unhealthy`).

---

## 13. Segurança

| Prática | Motivo |
|---|---|
| Rodar como **usuário não-root** (`USER`) | Reduz o impacto de uma invasão |
| Usar imagens **oficiais** e **minimalistas** | Menos superfície de ataque |
| **Fixar versões** e atualizar com frequência | Corrigir vulnerabilidades conhecidas |
| **Escanear imagens** (`docker scout`, Trivy, Grype) | Detectar CVEs antes do deploy |
| Não expor o socket `/var/run/docker.sock` | Dá controle total do host |
| Evitar `--privileged` | Remove isolamento importante |
| Sistema de arquivos **somente leitura** (`--read-only`) | Impede alterações em runtime |
| Remover capabilities (`--cap-drop ALL`) e adicionar só as necessárias | Princípio do menor privilégio |
| Não publicar portas desnecessárias | Reduz a superfície exposta |
| Assinar e verificar imagens | Garantir a origem |

---

## 14. Troubleshooting

### Fluxo de diagnóstico recomendado

```
1. docker ps -a                  → O container existe? Qual o STATUS?
2. docker logs <container>       → O que a aplicação disse?
3. docker inspect <container>    → ExitCode, OOMKilled, env, mounts, rede
4. docker exec -it <c> sh        → Investigar por dentro (se estiver rodando)
5. docker stats                  → Falta de recurso?
```

### Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| Container sai logo após iniciar (`Exited (0)`) | Processo principal terminou (nada para manter rodando) | Verifique `CMD`/`ENTRYPOINT`; processo deve ficar em *foreground* |
| `Exited (1)` / `Exited (137)` | Erro na aplicação / morto por falta de memória (OOM) ou SIGKILL | Veja `docker logs` e `docker inspect` (`OOMKilled`); aumente a memória |
| `port is already allocated` | Porta do host já em uso | Troque a porta (`-p 8081:80`) ou pare o processo que a usa |
| Aplicação não conecta ao banco | Usando `localhost` em vez do nome do serviço; banco ainda subindo | Use o nome do serviço; adicione `healthcheck` e `depends_on` |
| `permission denied` em bind mount | UID/GID do container difere do host | Ajuste o dono da pasta ou use `--user` |
| Dados sumiram após recriar | Dados gravados na camada do container | Use volumes nomeados |
| Imagem enorme | Sem multi-stage, sem `.dockerignore`, base pesada | Aplique multi-stage e imagens `-slim`/`alpine` |
| Alterações no código não aparecem | Imagem em cache / sem rebuild | `docker compose up -d --build` ou `docker build --no-cache` |
| `no space left on device` | Disco cheio de imagens/volumes/logs | `docker system df` e limpeza (`docker system prune`) |
| `Cannot connect to the Docker daemon` | Daemon parado ou sem permissão | `sudo systemctl start docker`; verifique grupo `docker` |

---

## 15. Quando usar e quando não usar containers

### ✅ Quando usar

- **Padronizar ambientes** de desenvolvimento entre a equipe.
- **Microsserviços** e arquiteturas modulares.
- **CI/CD**: builds e testes reproduzíveis.
- **Deploy** consistente entre dev, homologação e produção.
- **Subir dependências** rapidamente (banco, cache, fila) para desenvolvimento.
- Rodar **múltiplas versões** de uma mesma tecnologia lado a lado.
- **Escalar horizontalmente** serviços sem estado (*stateless*).
- Experimentar ferramentas sem "sujar" a máquina.

### ⚠️ Quando evitar ou ter cuidado extra

| Situação | Motivo | Alternativa / cuidado |
|---|---|---|
| **Bancos de dados em produção críticos** sem estratégia de persistência e backup | Risco de perda de dados se mal configurado | Use volumes, backups testados ou banco gerenciado (RDS, Cloud SQL) |
| Aplicações que exigem **isolamento forte de segurança** (multi-tenant hostil) | Kernel compartilhado | Máquinas virtuais, gVisor, Kata Containers |
| Apps que precisam de **kernel diferente** ou módulos específicos | Container usa o kernel do host | VM |
| **Interfaces gráficas desktop** complexas | Configuração trabalhosa | Instalação nativa ou VM |
| Apps **monolíticas legadas** muito acopladas ao SO | Esforço alto de adaptação | Avalie custo-benefício; talvez VM |
| **Equipe sem conhecimento** e projeto muito simples | Complexidade desnecessária | Deploy direto (PaaS) pode bastar |
| Alta performance de **I/O de disco** extrema | Overhead do overlayfs | Volumes/bind mounts ou bare metal |
| **Orquestrar dezenas de serviços** só com Compose | Compose não faz auto-scaling nem alta disponibilidade | Kubernetes / Swarm / serviços gerenciados |

### Compose vs Kubernetes (visão rápida)

| Critério | Docker Compose | Kubernetes |
|---|---|---|
| Complexidade | Baixa | Alta |
| Ideal para | Dev, testes, projetos pequenos, single host | Produção em escala, múltiplos hosts |
| Auto-scaling / self-healing | Não | Sim |
| Curva de aprendizado | Suave | Íngreme |

---

## 16. Cheat sheet

```bash
# ----- IMAGENS -----
docker pull nginx:stable                  # baixar
docker build -t app:1.0 .                 # construir
docker images                             # listar
docker rmi app:1.0                        # remover

# ----- CONTAINERS -----
docker run -d --name web -p 8080:80 nginx # subir em background
docker ps / docker ps -a                  # listar (rodando / todos)
docker stop web && docker rm web          # parar e remover
docker logs -f web                        # logs ao vivo
docker exec -it web sh                    # entrar no container
docker stats                              # consumo de recursos
docker cp web:/etc/nginx/nginx.conf .     # copiar arquivo

# ----- VOLUMES E REDES -----
docker volume create dados
docker network create minha-rede

# ----- COMPOSE -----
docker compose up -d --build              # subir tudo
docker compose ps                         # estado
docker compose logs -f api                # logs de um serviço
docker compose down                       # derrubar (mantém volumes)

# ----- LIMPEZA -----
docker system df                          # ver uso de disco
docker system prune                       # limpeza segura de rotina
```

---

## 17. Checklist de boas práticas

**Imagens**
- [ ] Imagem base oficial, enxuta e com **versão fixa**
- [ ] **Multi-stage build** aplicado
- [ ] `.dockerignore` configurado
- [ ] Sem segredos dentro da imagem
- [ ] Executa como **usuário não-root**
- [ ] Imagem escaneada contra vulnerabilidades

**Execução**
- [ ] Containers com **nome** e **política de restart** definidos
- [ ] **Limites** de CPU e memória configurados
- [ ] **Healthcheck** definido
- [ ] **Rotação de logs** configurada
- [ ] Apenas as portas necessárias publicadas

**Dados e rede**
- [ ] Dados persistentes em **volumes nomeados**
- [ ] **Backup** dos volumes testado
- [ ] **Rede customizada** em vez da padrão
- [ ] Comunicação interna por **nome de serviço**

**Operação**
- [ ] Configuração via **variáveis de ambiente**, `.env` fora do Git
- [ ] Compose versionado no repositório
- [ ] Rotina de limpeza (`docker system prune`) apenas em dev
- [ ] Monitoramento e alertas em produção
- [ ] **Nunca** corrigir produção entrando no container: corrija a imagem e faça novo deploy

---

## 18. Referências

- Documentação oficial do Docker: https://docs.docker.com
- Referência da CLI: https://docs.docker.com/reference/cli/docker/
- Referência do Dockerfile: https://docs.docker.com/reference/dockerfile/
- Documentação do Compose: https://docs.docker.com/compose/
- Boas práticas de build: https://docs.docker.com/build/building/best-practices/
- Docker Hub: https://hub.docker.com
- Trivy (scanner de vulnerabilidades): https://trivy.dev

---

*Documento de referência para estudo e uso profissional. Adapte os valores (versões, portas, credenciais) à sua realidade.*

# Referência Completa de Comandos Docker: Containers e Imagens

> Expansão da **seção 6** do guia "Gerenciamento de Containers com Docker". Cobre a sintaxe moderna (`docker container ...` / `docker image ...`), todos os comandos de ciclo de vida, inspeção, imagens, build, registry, limpeza e receitas prontas.

> Legenda: ✅ **Quando usar** · ⚠️ **Quando evitar / cuidado**

---

## Sumário

1. [Sintaxe moderna x sintaxe antiga (aliases)](#1-sintaxe-moderna-x-sintaxe-antiga-aliases)
2. [Containers: listar](#2-containers-listar)
3. [Containers: criar e executar](#3-containers-criar-e-executar)
4. [Containers: ciclo de vida](#4-containers-ciclo-de-vida)
5. [Containers: interagir](#5-containers-interagir)
6. [Containers: inspeção, logs e métricas](#6-containers-inspeção-logs-e-métricas)
7. [Containers: transformar em imagem ou arquivo](#7-containers-transformar-em-imagem-ou-arquivo)
8. [Containers: limpeza](#8-containers-limpeza)
9. [Imagens: listar e buscar](#9-imagens-listar-e-buscar)
10. [Imagens: baixar e publicar (registry)](#10-imagens-baixar-e-publicar-registry)
11. [Imagens: construir (build e buildx)](#11-imagens-construir-build-e-buildx)
12. [Imagens: inspecionar e analisar](#12-imagens-inspecionar-e-analisar)
13. [Imagens: exportar e importar (offline)](#13-imagens-exportar-e-importar-offline)
14. [Imagens: remover e limpar](#14-imagens-remover-e-limpar)
15. [Segurança de imagens](#15-segurança-de-imagens)
16. [Sistema, contexto e informações gerais](#16-sistema-contexto-e-informações-gerais)
17. [Formatação e filtros (`--format` e `--filter`)](#17-formatação-e-filtros---format-e---filter)
18. [Receitas prontas](#18-receitas-prontas)
19. [Cheat sheet final](#19-cheat-sheet-final)

---

## 1. Sintaxe moderna x sintaxe antiga (aliases)

O Docker tem **duas formas** de escrever a maioria dos comandos. As duas funcionam e fazem exatamente a mesma coisa. A forma moderna (`docker <objeto> <ação>`) é mais organizada e a recomendada na documentação.

### Containers
Exemplo logs ->

> docker container logs rest-with-springboot-java-app-1 

> ou docker container logs 92032fbf43b9

| Forma moderna (recomendada) | Forma antiga (atalho) | Função |
|---|---|---|
| `docker container ls` | `docker ps` | Lista containers em execução |
| `docker container ls -a` | `docker ps -a` | Lista todos os containers |
| `docker container run` | `docker run` | Cria e inicia |
| `docker container create` | `docker create` | Cria sem iniciar |
| `docker container start` | `docker start` | Inicia |
| `docker container stop` | `docker stop` | Para (graceful) |
| `docker container restart` | `docker restart` | Reinicia |
| `docker container kill` | `docker kill` | Mata (SIGKILL) |
| `docker container rm` | `docker rm` | Remove |
| `docker container exec` | `docker exec` | Executa comando |
| `docker container logs` | `docker logs` | Logs |
| `docker container inspect` | `docker inspect` | Detalhes (JSON) |
| `docker container stats` | `docker stats` | Métricas |
| `docker container top` | `docker top` | Processos |
| `docker container cp` | `docker cp` | Copia arquivos |
| `docker container diff` | `docker diff` | Mudanças no filesystem |
| `docker container commit` | `docker commit` | Container → imagem |
| `docker container export` | `docker export` | Container → .tar |
| `docker container port` | `docker port` | Mostra portas mapeadas |
| `docker container rename` | `docker rename` | Renomeia |
| `docker container pause` / `unpause` | `docker pause` / `unpause` | Congela / retoma |
| `docker container wait` | `docker wait` | Aguarda e retorna exit code |
| `docker container attach` | `docker attach` | Anexa ao processo principal |
| `docker container update` | `docker update` | Altera limites em runtime |
| `docker container prune` | n/a | Remove containers parados |

> `docker container list`, `docker container ps` e `docker ps` são todos equivalentes a `docker container ls`.

### Imagens

| Forma moderna (recomendada) | Forma antiga (atalho) | Função |
|---|---|---|
| `docker image ls` | `docker images` | Lista imagens |
| `docker image pull` | `docker pull` | Baixa |
| `docker image push` | `docker push` | Publica |
| `docker image build` | `docker build` | Constrói |
| `docker image tag` | `docker tag` | Cria tag |
| `docker image history` | `docker history` | Camadas |
| `docker image inspect` | `docker inspect` | Detalhes (JSON) |
| `docker image rm` | `docker rmi` | Remove |
| `docker image save` | `docker save` | Exporta para .tar |
| `docker image load` | `docker load` | Importa de .tar |
| `docker image import` | `docker import` | Cria imagem de um filesystem .tar |
| `docker image prune` | n/a | Remove imagens não usadas |

> ⚠️ **Atenção com `docker inspect`:** ele serve para qualquer objeto (container, imagem, volume, rede). Se houver um container e uma imagem com o mesmo nome, pode haver ambiguidade. Use `docker container inspect` ou `docker image inspect` para ser explícito (ou `--type container|image`).

---

## 2. Containers: listar

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker container ls` (ou `docker ps`) | Lista containers **em execução** | Ver o estado atual | Não mostra os parados |
| `docker container ls -a` (ou `docker ps -a`) | Lista **todos** (rodando, parados, criados) | Achar containers que saíram com erro | n/a |
| `docker container ls -q` (ou `docker ps -q`) | Mostra **somente os IDs** | Alimentar outros comandos (`docker stop $(docker ps -q)`) | n/a |
| `docker container ls -aq` | IDs de **todos** os containers | Limpeza em massa | Cuidado ao combinar com `rm -f` |
| `docker container ls -l` | Último container criado | Pegar rápido o mais recente | n/a |
| `docker container ls -n 5` | Os 5 últimos criados | Visão rápida do histórico | n/a |
| `docker container ls -s` | Inclui o **tamanho** (`SIZE`) de cada container | Descobrir quem consome disco | Mais lento em muitos containers |
| `docker container ls --no-trunc` | Mostra IDs e comandos **completos** | Depuração, scripts | Saída larga |
| `docker container ls --filter "status=exited"` | Filtra por estado | Achar containers parados | n/a |
| `docker container ls --format "..."` | Formata a saída (Go template) | Tabelas customizadas, scripts | Sintaxe sensível a aspas |

### Filtros úteis (`--filter` / `-f`)

| Filtro | Exemplo | Resultado |
|---|---|---|
| `status` | `-f status=running` | Valores: `created`, `restarting`, `running`, `removing`, `paused`, `exited`, `dead` |
| `name` | `-f name=api` | Nome contém `api` |
| `ancestor` | `-f ancestor=postgres:16` | Containers criados a partir da imagem |
| `label` | `-f label=env=prod` | Por label |
| `exited` | `-f exited=1` | Por código de saída (use com `-a`) |
| `health` | `-f health=unhealthy` | `starting`, `healthy`, `unhealthy`, `none` |
| `network` | `-f network=backend` | Conectados a uma rede |
| `volume` | `-f volume=pgdata` | Que montam um volume |
| `publish` | `-f publish=8080` | Que publicam a porta |
| `before` / `since` | `-f since=meu-nginx` | Criados antes/depois de outro |

```bash
# Somente os que estão com problema de saúde
docker ps -f health=unhealthy

# Containers que saíram com erro (exit code diferente de 0)
docker ps -a -f status=exited -f exited=1

# Tabela customizada
docker ps --format "table {{.ID}}\t{{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}"

# Saída em JSON (uma linha por container)
docker ps --format json
```

---

## 3. Containers: criar e executar

### 3.1 `run` x `create` x `start`

| Comando | O que faz |
|---|---|
| `docker run` | `create` + `start` (+ `attach` se não usar `-d`) |
| `docker create` | Apenas cria (estado *Created*), não inicia |
| `docker start` | Inicia um container já criado/parado |

### 3.2 Flags do `docker run` (as mais importantes)

| Flag | Função | ✅ Quando usar | ⚠️ Cuidado |
|---|---|---|---|
| `-d` / `--detach` | Segundo plano | Serviços | Sem saída na tela; use `logs` |
| `-it` | Interativo + terminal (TTY) | Shell, depuração | Não para serviços permanentes |
| `--name` | Nome do container | Sempre | Nome deve ser único |
| `--rm` | Remove ao terminar | Tarefas pontuais | Perde o container para inspecionar |
| `-p H:C` | Publica porta | Acesso pelo host | Evite expor bancos em `0.0.0.0` |
| `-p 127.0.0.1:H:C` | Publica **só em localhost** | Banco/serviço interno em dev | n/a (recomendado) |
| `-P` | Publica todas as `EXPOSE` em portas aleatórias | Testes rápidos | Portas imprevisíveis |
| `-e VAR=valor` | Variável de ambiente | Configuração | Não passe segredos na CLI |
| `--env-file arq` | Variáveis de um arquivo | Muitas variáveis | Não versionar o arquivo |
| `-v vol:/path` | Volume nomeado | Persistência | n/a |
| `-v ./dir:/path` | Bind mount | Dev (hot reload) | Permissões; prefira caminho absoluto em `-v` |
| `-v ...:ro` | Montagem **somente leitura** | Configs, código | n/a (recomendado) |
| `--mount type=...` | Sintaxe explícita de montagem | Clareza, scripts | Mais verboso |
| `--tmpfs /tmp` | Diretório em memória | Dados temporários | Somem ao parar |
| `--network` | Conecta a uma rede | Comunicação entre containers | n/a |
| `--network-alias` | Apelido DNS na rede | Múltiplos nomes para o mesmo serviço | n/a |
| `--hostname` | Hostname interno | Apps que dependem dele | n/a |
| `--add-host h:ip` | Entrada em `/etc/hosts` | Resolver nomes fixos | n/a |
| `--dns` | Servidor DNS customizado | Redes corporativas | n/a |
| `--restart` | Política: `no`, `on-failure[:N]`, `always`, `unless-stopped` | Serviços | `always` pode mascarar crash loop |
| `--memory` / `-m` | Limite de RAM | Sempre em produção | Pode causar OOMKill |
| `--memory-swap` | Limite de RAM + swap | Controle fino | n/a |
| `--cpus` | Limite de CPUs (ex.: `1.5`) | Evitar monopólio | n/a |
| `--cpu-shares` | Peso relativo de CPU | Priorização | Só vale sob disputa |
| `--pids-limit` | Limite de processos | Proteção contra *fork bomb* | n/a |
| `--user` / `-u` | Usuário de execução | Rodar sem root | UID precisa existir/ter permissão |
| `-w` / `--workdir` | Diretório de trabalho | Sobrescrever o da imagem | n/a |
| `--entrypoint` | Sobrescreve o ENTRYPOINT | Depurar (`--entrypoint sh`) | n/a |
| `--read-only` | Filesystem raiz somente leitura | Hardening | Combine com `--tmpfs` onde precisar escrever |
| `--cap-drop ALL` / `--cap-add` | Capabilities Linux | Menor privilégio | Teste a aplicação |
| `--security-opt` | Opções de segurança (seccomp, no-new-privileges) | Hardening | n/a |
| `--privileged` | Acesso total ao host | **Quase nunca** | ❌ Remove o isolamento |
| `--device` | Expõe dispositivo do host | Hardware específico | n/a |
| `--gpus all` | Expõe GPUs | Machine learning | Requer NVIDIA Container Toolkit |
| `--label k=v` | Metadados | Organização, filtros | n/a |
| `--health-cmd` etc. | Healthcheck no `run` | Sem Dockerfile | n/a |
| `--init` | Usa um init mínimo (tini) como PID 1 | Evitar processos zumbis, sinais corretos | n/a |
| `--stop-signal` / `--stop-timeout` | Sinal e tempo do `stop` | Apps com shutdown demorado | n/a |
| `--log-driver` / `--log-opt` | Driver e opções de log | Rotação (`max-size`, `max-file`) | n/a |
| `--platform` | Arquitetura (`linux/amd64`, `linux/arm64`) | Rodar imagem de outra arquitetura | Emulação é lenta |
| `--pull` | `always`, `missing` (padrão), `never` | Garantir imagem atualizada | `always` gera tráfego |
| `--ulimit` | Limites (ex.: `nofile=65535`) | Servidores com muitos arquivos | n/a |
| `--sysctl` | Parâmetros de kernel do container | Tuning de rede | Só os permitidos |
| `--ipc` / `--pid` | Compartilha namespace com host/outro container | Casos especiais | ⚠️ Reduz o isolamento |

### 3.3 Exemplos

```bash
# Serviço típico (background, nome, porta só no localhost, volume, restart, limites)
docker run -d --name db \
  -p 127.0.0.1:5432:5432 \
  -e POSTGRES_PASSWORD=senha_dev \
  -v pgdata:/var/lib/postgresql/data \
  --restart unless-stopped \
  --memory 512m --cpus 1 \
  postgres:16-alpine

# Tarefa pontual: roda, mostra e se autodestrói
docker run --rm alpine echo "olá, docker"

# Shell descartável para explorar uma imagem
docker run --rm -it ubuntu:24.04 bash

# Hardening: somente leitura, sem capabilities, sem root
docker run -d --name web --read-only --tmpfs /tmp \
  --cap-drop ALL --security-opt no-new-privileges \
  --user 1000:1000 -p 8080:8080 minha-api:1.0

# Depurar uma imagem cujo ENTRYPOINT quebra
docker run --rm -it --entrypoint sh minha-api:1.0

# Criar sem iniciar, depois iniciar
docker create --name teste nginx:stable
docker start teste
```

> **Dica:** `docker run` baixa a imagem automaticamente se ela não existir localmente. Se a tag já existe localmente, **não** atualiza; use `docker pull` ou `--pull always`.

---

## 4. Containers: ciclo de vida

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker start <c>` | Inicia container parado/criado | Retomar | n/a |
| `docker start -a <c>` | Inicia e **anexa** à saída | Ver a saída ao iniciar | n/a |
| `docker start -ai <c>` | Inicia, anexa e mantém entrada interativa | Retomar um container `-it` | n/a |
| `docker stop <c>` | SIGTERM → espera → SIGKILL (padrão 10 s) | **Forma padrão** de parar | n/a |
| `docker stop -t 30 <c>` | Define o tempo de espera (segundos) | Apps que demoram a encerrar (bancos) | n/a |
| `docker stop $(docker ps -q)` | Para **todos** os containers em execução | Encerrar o ambiente de dev | ❌ Cuidado em hosts compartilhados |
| `docker restart <c>` | Para e inicia | Aplicar mudança de configuração | Derruba conexões |
| `docker restart -t 5 <c>` | Reinicia com timeout de parada | n/a | n/a |
| `docker kill <c>` | SIGKILL imediato | Container travado | Sem shutdown limpo; risco de corrupção |
| `docker kill -s SIGHUP <c>` | Envia **outro sinal** | Recarregar configuração (nginx, etc.) | Depende de a app tratar o sinal |
| `docker pause <c>` | Congela processos (cgroup freezer) | Diagnóstico, snapshot consistente | Não é para uso contínuo |
| `docker unpause <c>` | Retoma | n/a | n/a |
| `docker wait <c>` | Bloqueia até parar e retorna o **exit code** | Scripts/CI | Bloqueia o terminal |
| `docker rename <c> <novo>` | Renomeia | Organização | Referências antigas por nome quebram |
| `docker update --memory 1g --cpus 2 <c>` | Altera **limites em runtime** sem recriar | Ajuste rápido de recursos | Algumas opções exigem `--memory-swap` junto |
| `docker update --restart unless-stopped <c>` | Altera a política de restart | Corrigir após esquecer no `run` | n/a |
| `docker rm <c>` | Remove container **parado** | Limpeza | Dados da camada do container são perdidos |
| `docker rm -f <c>` | Força a remoção (mesmo rodando) | Dev | Sem shutdown gracioso |
| `docker rm -v <c>` | Remove também os **volumes anônimos** associados | Evitar volumes órfãos | Apaga dados desses volumes |
| `docker rm $(docker ps -aq)` | Remove **todos** os containers | Reset de dev | ❌ Nunca em produção |
| `docker rm $(docker ps -aq -f status=exited)` | Remove só os parados | Limpeza seletiva | n/a |

### Políticas de restart (`--restart`)

| Política | Comportamento |
|---|---|
| `no` | Nunca reinicia (padrão) |
| `on-failure[:N]` | Reinicia só se sair com erro (máx. N vezes) |
| `always` | Sempre reinicia (inclusive após reboot do daemon) |
| `unless-stopped` | Como `always`, exceto se você parou manualmente |

---

## 5. Containers: interagir

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker exec -it <c> sh` | Shell interativo em container rodando | Investigar | Imagens enxutas podem não ter `bash`; use `sh` |
| `docker exec <c> <cmd>` | Comando pontual | `psql`, migrations, `ls` | n/a |
| `docker exec -d <c> <cmd>` | Executa em segundo plano | Tarefa longa | Sem saída |
| `docker exec -u root <c> <cmd>` | Executa como outro usuário | Instalar algo para depurar | Não "conserte" produção assim |
| `docker exec -w /app <c> <cmd>` | Define o diretório de trabalho | n/a | n/a |
| `docker exec -e VAR=x <c> <cmd>` | Variável só para esse comando | n/a | n/a |
| `docker attach <c>` | Anexa ao processo principal (PID 1) | Ver/interagir com o processo principal | `Ctrl+C` pode **parar** o container; para sair sem parar use `Ctrl+P` `Ctrl+Q` |
| `docker cp <c>:/path ./local` | Container → host | Extrair logs/arquivos | Não use como deploy |
| `docker cp ./local <c>:/path` | Host → container | Injetar arquivo de teste | Some ao recriar o container |
| `docker cp -a ...` | Preserva UID/GID | n/a | n/a |
| `docker port <c>` | Lista **mapeamentos de porta** | Descobrir a porta do host (`-P`) | n/a |
| `docker port <c> 80` | Mapeamento de uma porta específica | n/a | n/a |

```bash
# Banco: abrir o cliente do Postgres dentro do container
docker exec -it db psql -U appuser -d appdb

# Rodar um comando e sair (migrations, por exemplo)
docker exec api ./migrate.sh

# Copiar um dump para fora
docker exec db pg_dump -U appuser appdb > backup.sql
docker cp db:/var/log/postgresql/ ./logs-db

# Sair de um attach sem matar o container: Ctrl+P seguido de Ctrl+Q
```

> **`exec` x `attach`:** `exec` cria um **novo processo** dentro do container; `attach` conecta ao processo **principal** que já existe.

---

## 6. Containers: inspeção, logs e métricas

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker logs <c>` | Saída (stdout/stderr) | Primeiro passo no diagnóstico | Crescem sem rotação |
| `docker logs -f <c>` | Acompanha em tempo real | Depurar ao vivo | `Ctrl+C` sai do `logs`, não para o container |
| `docker logs --tail 100 <c>` | Últimas N linhas | Evitar saída gigante | n/a |
| `docker logs --since 10m <c>` | Desde um período (`10m`, `2h`, data) | Investigar um incidente | n/a |
| `docker logs --until 5m <c>` | Até um momento | Janela de tempo | n/a |
| `docker logs -t <c>` | Adiciona **timestamps** | Correlacionar eventos | n/a |
| `docker inspect <c>` | JSON completo | Ver IP, mounts, env, healthcheck | Pode expor segredos em variáveis |
| `docker inspect -f '{{...}}' <c>` | Extrai **um campo** | Scripts | Sintaxe de template |
| `docker top <c>` | Processos do container | Ver o que está ativo | n/a |
| `docker top <c> aux` | Com opções do `ps` | Mais detalhes | n/a |
| `docker stats` | CPU/RAM/rede/I/O ao vivo (todos) | Achar gargalos | Atualiza continuamente |
| `docker stats --no-stream` | Uma leitura única | Scripts, logs | n/a |
| `docker stats <c1> <c2>` | Apenas containers específicos | Foco | n/a |
| `docker stats -a` | Inclui parados | n/a | n/a |
| `docker diff <c>` | Alterações no filesystem (`A` add, `C` change, `D` delete) | Ver o que a app escreveu | n/a |
| `docker events` | Stream de eventos do daemon | Auditoria, depuração | Saída contínua |
| `docker events --since 30m --filter container=api` | Eventos filtrados | Descobrir *quando* caiu | n/a |

### Campos úteis do `inspect`

```bash
# Estado, código de saída e se foi morto por falta de memória
docker inspect -f '{{.State.Status}}  exit={{.State.ExitCode}}  oom={{.State.OOMKilled}}' api

# Endereço IP (em cada rede)
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' api

# Variáveis de ambiente
docker inspect -f '{{range .Config.Env}}{{println .}}{{end}}' api

# Mounts (volumes e bind mounts)
docker inspect -f '{{json .Mounts}}' api

# Status do healthcheck
docker inspect -f '{{.State.Health.Status}}' api

# Política de restart e quantas vezes reiniciou
docker inspect -f '{{.HostConfig.RestartPolicy.Name}}  restarts={{.RestartCount}}' api

# Imagem usada (e seu ID)
docker inspect -f '{{.Config.Image}}  {{.Image}}' api

# Caminho do arquivo de log no host (driver json-file)
docker inspect -f '{{.LogPath}}' api

# Início e fim
docker inspect -f 'start={{.State.StartedAt}} end={{.State.FinishedAt}}' api
```

```bash
# Consumo instantâneo em formato de tabela
docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
```

---

## 7. Containers: transformar em imagem ou arquivo

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker commit <c> nome:tag` | Cria **imagem** a partir do estado atual do container | Experimentos, snapshots rápidos, depuração | ❌ Não reproduzível e sem documentação. Em projetos reais use **Dockerfile** |
| `docker commit -m "msg" -a "autor" <c> nome:tag` | Com mensagem e autor | Registro mínimo | n/a |
| `docker commit -c 'CMD ["x"]' <c> nome:tag` | Aplica instrução Dockerfile ao commitar | n/a | n/a |
| `docker export <c> > c.tar` | Exporta o **filesystem** (achatado, sem camadas/metadados) | Inspecionar/arquivar o conteúdo | Perde `ENTRYPOINT`, `ENV`, volumes |
| `docker export -o c.tar <c>` | Idem, com `-o` | n/a | n/a |
| `docker import c.tar nome:tag` | Cria imagem a partir de um filesystem `.tar` | Reconstruir de um export | Imagem de **uma única camada** |

> **`commit/export/import` x `save/load`:** `save/load` preservam **camadas, tags e metadados** de uma *imagem*. `export/import` trabalham com o *filesystem* de um *container* e perdem esses metadados.

---

## 8. Containers: limpeza

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker container prune` | Remove **todos os containers parados** | Limpeza periódica | Pede confirmação; `-f` pula |
| `docker container prune --filter "until=24h"` | Só os parados há mais de 24 h | Limpeza segura | n/a |
| `docker container prune --filter "label=env=dev"` | Por label | Limpeza dirigida | n/a |
| `docker rm $(docker ps -aq -f status=exited)` | Equivalente manual | Scripts | n/a |
| `docker run --rm ...` | Evita acumular containers desde o início | Tarefas pontuais | n/a |

---

## 9. Imagens: listar e buscar

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker image ls` (ou `docker images`) | Lista imagens locais | Ver o armazenado | n/a |
| `docker image ls -a` | Inclui camadas intermediárias | Depuração de build | Saída longa |
| `docker image ls -q` | Só os IDs | Scripts, remoção em massa | n/a |
| `docker image ls --digests` | Mostra o **digest** (`sha256:...`) | Fixar versão imutável | n/a |
| `docker image ls --no-trunc` | IDs completos | n/a | n/a |
| `docker image ls postgres` | Filtra por repositório | Ver versões de uma imagem | n/a |
| `docker image ls --filter dangling=true` | Imagens **sem tag** (`<none>`) | Achar lixo de builds | n/a |
| `docker image ls --filter "reference=minha-api:*"` | Filtro por nome/tag (aceita curinga) | Organização | n/a |
| `docker image ls --filter "before=nome:tag"` / `since=` | Antes/depois de outra imagem | n/a | n/a |
| `docker image ls --filter "label=projeto=x"` | Por label | n/a | n/a |
| `docker image ls --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"` | Tabela customizada | Relatórios | n/a |
| `docker search <termo>` | Busca no Docker Hub | Descobrir imagens | Verifique se é **oficial** |
| `docker search --filter is-official=true postgres` | Só oficiais | Segurança | n/a |
| `docker search --limit 5 nginx` | Limita resultados | n/a | n/a |

```bash
# Imagens ordenadas por tamanho (usando sort do shell)
docker image ls --format "{{.Size}}\t{{.Repository}}:{{.Tag}}" | sort -h

# Quais imagens estão "penduradas" (dangling)?
docker image ls -f dangling=true
```

---

## 10. Imagens: baixar e publicar (registry)

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker pull nginx:stable` | Baixa uma tag | Antes de rodar | Evite `latest` em produção |
| `docker pull nginx@sha256:<digest>` | Baixa por **digest** (imutável) | Produção, reprodutibilidade total | Digest é difícil de ler |
| `docker pull -a nginx` | Baixa **todas as tags** | Raro | Pode baixar muitos GBs |
| `docker pull --platform linux/arm64 img` | Escolhe a arquitetura | Apple Silicon x servidor amd64 | n/a |
| `docker pull -q img` | Saída silenciosa | Scripts | n/a |
| `docker pull ghcr.io/usuario/app:1.0` | Registry diferente do Docker Hub | GHCR, ECR, GCR, privado | Requer `login` se for privado |
| `docker login` | Autentica no Docker Hub | Publicar/baixar privadas | n/a |
| `docker login ghcr.io -u USUARIO --password-stdin` | Autentica em outro registry, lendo a senha/token do stdin | CI/CD | ❌ Evite `-p senha` (fica no histórico do shell) |
| `docker logout [registry]` | Remove credenciais | Máquinas compartilhadas | n/a |
| `docker tag origem:tag destino:tag` | Cria **outro nome** para a mesma imagem | Preparar para publicar | Não duplica dados |
| `docker push usuario/app:1.0` | Envia ao registry | Publicar versão | ❌ Nunca com segredos dentro |
| `docker push -a usuario/app` | Envia **todas as tags** | n/a | n/a |

```bash
# Fluxo clássico: construir, nomear para o registry e publicar
docker build -t minha-api:1.0.3 .
docker tag minha-api:1.0.3 usuario/minha-api:1.0.3
docker tag minha-api:1.0.3 usuario/minha-api:latest
docker push usuario/minha-api:1.0.3
docker push usuario/minha-api:latest

# Login seguro em CI usando token em variável
echo "$GHCR_TOKEN" | docker login ghcr.io -u "$GHCR_USER" --password-stdin
```

**Anatomia do nome de uma imagem:**

```
registry.exemplo.com:5000 / equipe / minha-api : 1.0.3
└──────── registry ───────┘  └namespace┘ └ repo ┘  └ tag ┘
                                           @sha256:abc123...   (ou digest)
```

Sem registry, o padrão é o Docker Hub. Sem tag, o padrão é `latest`.

---

## 11. Imagens: construir (build e buildx)

### 11.1 `docker build` / `docker image build`

| Comando / Flag | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker build -t nome:tag .` | Constrói a partir do Dockerfile do diretório atual | Padrão | O `.` é o **contexto**: use `.dockerignore` |
| `-t nome:tag -t nome:latest` | Várias tags de uma vez | Versionar e atualizar `latest` | n/a |
| `-f caminho/Dockerfile` | Dockerfile em outro local/nome | `Dockerfile.dev`, `Dockerfile.prod` | n/a |
| `--no-cache` | Ignora o cache | Forçar rebuild completo | Build bem mais lento |
| `--pull` | Tenta baixar versão mais nova da imagem base | Pegar correções de segurança da base | n/a |
| `--target estagio` | Constrói só até um estágio do multi-stage | Imagem de teste/debug (`--target build`) | n/a |
| `--build-arg NOME=valor` | Passa um `ARG` | Versões, flags de build | ❌ Não passe segredos |
| `--secret id=...,src=...` | Segredo disponível **só durante o build** (BuildKit) | Tokens para baixar dependências privadas | Requer BuildKit |
| `--ssh` | Encaminha agente SSH para o build | Clonar repositórios privados | Requer BuildKit |
| `--platform linux/amd64` | Arquitetura de destino | Build para servidor amd64 num Mac ARM | Emulação é lenta |
| `--progress=plain` | Saída completa e sem animação | Depurar falhas de build | Saída longa |
| `--label chave=valor` | Metadados na imagem | Rastreabilidade (commit, versão) | n/a |
| `--cache-from` / `--cache-to` | Cache externo (registry, local) | CI/CD | Configuração mais avançada |
| `-q` | Imprime só o ID da imagem | Scripts | n/a |
| `--output type=...` | Exporta o resultado (tar, pasta) em vez de imagem | Artefatos de build | Requer BuildKit |
| `--load` / `--push` | (buildx) Carrega no Docker local / envia ao registry | Multi-arquitetura | n/a |

```bash
# Build comum, com duas tags e rótulo do commit
docker build -t minha-api:1.0.3 -t minha-api:latest \
  --label org.opencontainers.image.revision=$(git rev-parse --short HEAD) .

# Dockerfile alternativo + argumento
docker build -f Dockerfile.prod --build-arg JAVA_VERSION=21 -t minha-api:prod .

# Apenas o estágio "build" de um multi-stage (para depurar)
docker build --target build -t minha-api:build-debug .

# Depurar falha de build com saída completa
docker build --no-cache --progress=plain -t minha-api:dev .

# Segredo disponível só no build (não fica na imagem)
docker build --secret id=npmrc,src=$HOME/.npmrc -t app:1.0 .
# No Dockerfile: RUN --mount=type=secret,id=npmrc,target=/root/.npmrc npm ci
```

### 11.2 `docker buildx` (BuildKit avançado)

| Comando | Para que serve | ✅ Quando usar |
|---|---|---|
| `docker buildx ls` | Lista os *builders* | Ver o que está disponível |
| `docker buildx create --use --name meu-builder` | Cria e seleciona um builder | Habilitar multi-plataforma |
| `docker buildx use <builder>` | Troca de builder | n/a |
| `docker buildx build --platform linux/amd64,linux/arm64 -t user/app:1.0 --push .` | Build **multi-arquitetura** e publicação | Imagens para servidores amd64 **e** ARM |
| `docker buildx build --load -t app:dev .` | Constrói e carrega no Docker local | Build de uma plataforma só |
| `docker buildx bake` | Builds declarativos de vários alvos (`docker-bake.hcl`) | Monorepos / vários serviços |
| `docker buildx imagetools inspect user/app:1.0` | Inspeciona **manifest** multi-arquitetura no registry | Ver plataformas disponíveis |
| `docker buildx du` | Uso de disco do cache de build | Diagnóstico |
| `docker buildx prune` / `docker builder prune` | Limpa o **cache de build** | Liberar espaço |

> ⚠️ Cache de build costuma ocupar muito disco sem aparecer em `docker image ls`. Confira com `docker system df`.

---

## 12. Imagens: inspecionar e analisar

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker image inspect <img>` | Metadados completos (JSON) | Ver ENV, portas, ENTRYPOINT, camadas | Saída longa |
| `docker image inspect -f '{{.Config.ExposedPorts}}' <img>` | Campo específico | Scripts | n/a |
| `docker image history <img>` | Camadas e tamanho de cada uma | Descobrir **qual instrução** engordou a imagem | n/a |
| `docker image history --no-trunc <img>` | Comandos completos de cada camada | Auditar o que foi executado | Pode revelar segredos embutidos (um alerta!) |
| `docker image history --human=false <img>` | Tamanhos em bytes | Comparações | n/a |
| `docker image ls --digests` | Digest de cada imagem | Fixar versões | n/a |
| `docker run --rm -it --entrypoint sh <img>` | Entrar na imagem para explorar | Ver arquivos, usuário, binários | n/a |
| `docker scout quickview <img>` | Resumo de vulnerabilidades | Triagem rápida | Requer Docker Scout |

```bash
# Quais são ENV, ENTRYPOINT e CMD da imagem?
docker image inspect -f 'ENV={{.Config.Env}}  ENTRYPOINT={{.Config.Entrypoint}}  CMD={{.Config.Cmd}}' postgres:16

# Usuário de execução e portas expostas
docker image inspect -f 'USER={{.Config.User}}  PORTS={{.Config.ExposedPorts}}' minha-api:1.0

# Arquitetura e sistema operacional
docker image inspect -f '{{.Os}}/{{.Architecture}}' minha-api:1.0

# Tamanho em MB
docker image inspect -f '{{.Size}}' minha-api:1.0 | awk '{printf "%.1f MB\n", $1/1024/1024}'
```

---

## 13. Imagens: exportar e importar (offline)

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker image save -o app.tar app:1.0` | Salva a imagem (camadas + tags + metadados) em `.tar` | Levar para ambiente **sem internet**, backup | Arquivo grande |
| `docker save app:1.0 \| gzip > app.tar.gz` | Salva já comprimido | Economizar espaço | n/a |
| `docker save img1 img2 -o pacote.tar` | Várias imagens num arquivo | Pacote offline completo | n/a |
| `docker image load -i app.tar` | Restaura a partir do `.tar` | Máquina de destino | Sobrescreve tag existente |
| `docker load < app.tar.gz` | Aceita `.tar.gz` direto | n/a | n/a |
| `docker import c.tar nome:tag` | Cria imagem a partir de **filesystem** | Reconstruir de `export` | Perde metadados |

```bash
# Transferir uma imagem para outro servidor via SSH, sem registry
docker save minha-api:1.0 | gzip | ssh usuario@servidor 'gunzip | docker load'
```

---

## 14. Imagens: remover e limpar

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker image rm <img>` (ou `docker rmi`) | Remove imagem por nome/tag/ID | Liberar espaço | Falha se houver **container** (mesmo parado) usando-a |
| `docker rmi -f <img>` | Força remoção | n/a | Pode deixar containers sem imagem |
| `docker rmi img1 img2 img3` | Várias de uma vez | n/a | n/a |
| `docker rmi $(docker images -q)` | Remove **todas** as imagens | Reset total de dev | ❌ Nunca em produção; reprecisa baixar tudo |
| `docker rmi $(docker images -f dangling=true -q)` | Remove só as sem tag | Limpeza de builds | n/a |
| `docker image prune` | Remove imagens **dangling** (sem tag e sem uso) | Limpeza de rotina | n/a (seguro) |
| `docker image prune -a` | Remove **todas as imagens sem container associado** | Liberar muito espaço | Apaga imagens que você talvez queira offline |
| `docker image prune -a --filter "until=168h"` | Só as criadas há mais de 7 dias | Limpeza **segura** em dev | n/a |
| `docker image prune --filter "label!=keep"` | Poupa imagens com determinada label | Proteger imagens importantes | n/a |
| `docker builder prune` | Limpa **cache de build** | Quando `system df` mostra muito cache | Próximo build será lento |
| `docker builder prune --keep-storage 5GB` | Mantém até 5 GB de cache | Limitar o cache | n/a |

> **Por que "imagem em uso" não sai?** Containers parados ainda referenciam imagens. Remova os containers primeiro (`docker container prune`) e depois as imagens.

> **Imagens com tag duplicada:** `docker rmi nome:tag` remove apenas a **tag**. A imagem só é apagada de fato quando nenhuma tag nem container a referencia.

---

## 15. Segurança de imagens

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker scout quickview <img>` | Resumo de CVEs e base image | Triagem rápida | Requer login/Scout |
| `docker scout cves <img>` | Lista vulnerabilidades (CVEs) | Antes do deploy | n/a |
| `docker scout recommendations <img>` | Sugere imagens base melhores | Reduzir vulnerabilidades | n/a |
| `trivy image <img>` | Scanner open source de CVEs | CI/CD, gratuito | Ferramenta externa |
| `grype <img>` | Outro scanner open source | n/a | Ferramenta externa |
| `docker trust inspect <img>` | Verifica assinaturas (Content Trust) | Supply chain | Nem toda imagem é assinada |
| `export DOCKER_CONTENT_TRUST=1` | Só aceita imagens **assinadas** | Ambientes rígidos | Bloqueia imagens não assinadas |
| `docker image history --no-trunc` | Auditoria de camadas | Procurar segredos vazados | n/a |

---

## 16. Sistema, contexto e informações gerais

| Comando | Para que serve | ✅ Quando usar | ⚠️ Quando evitar / cuidado |
|---|---|---|---|
| `docker version` | Versões do cliente e do servidor | Reportar bug, verificar compatibilidade | n/a |
| `docker info` | Informações do daemon (driver, containers, recursos, root dir) | Diagnóstico geral | n/a |
| `docker system df` | Uso de disco (imagens, containers, volumes, cache) | Descobrir o que ocupa espaço | n/a |
| `docker system df -v` | Detalhado por item | Achar o "vilão" | Saída longa |
| `docker system events` | Eventos do daemon em tempo real | Auditoria | n/a |
| `docker system prune` | Remove containers parados, redes sem uso, imagens dangling e cache dangling | Rotina em dev | Confira antes em ambientes compartilhados |
| `docker system prune -a` | Inclui **todas as imagens sem uso** | Limpeza profunda | Reduz cache de imagens |
| `docker system prune -a --volumes` | Inclui **volumes** sem uso | Reset total de dev | ❌ **Apaga dados**; nunca em produção |
| `docker context ls` | Lista contextos (hosts Docker alvo) | Alternar entre local e servidor remoto | n/a |
| `docker context use <nome>` | Troca o host onde os comandos rodam | Administrar um servidor remoto | ⚠️ Confirme o contexto **antes** de rodar `prune`/`rm` |
| `docker context create remoto --docker "host=ssh://user@servidor"` | Cria contexto via SSH | Gerenciar servidor remoto | n/a |
| `docker -H ssh://user@host ps` | Roda **um comando** em outro host | Consulta pontual | n/a |
| `docker --help` / `docker <cmd> --help` | Ajuda | Sempre que tiver dúvida | n/a |

---

## 17. Formatação e filtros (`--format` e `--filter`)

Quase todos os comandos de listagem e `inspect` aceitam templates **Go**.

### Campos comuns do `docker ps --format`

| Placeholder | Conteúdo |
|---|---|
| `{{.ID}}` | ID curto |
| `{{.Names}}` | Nome |
| `{{.Image}}` | Imagem |
| `{{.Command}}` | Comando |
| `{{.CreatedAt}}` / `{{.RunningFor}}` | Criação / há quanto tempo roda |
| `{{.Status}}` | Estado (ex.: `Up 2 hours (healthy)`) |
| `{{.Ports}}` | Portas |
| `{{.Size}}` | Tamanho (com `-s`) |
| `{{.Networks}}` / `{{.Mounts}}` / `{{.Labels}}` | Redes / montagens / labels |

### Campos comuns do `docker image ls --format`

`{{.Repository}}`, `{{.Tag}}`, `{{.ID}}`, `{{.Digest}}`, `{{.CreatedSince}}`, `{{.CreatedAt}}`, `{{.Size}}`

### Exemplos

```bash
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
docker ps -a --format "{{.Names}}: {{.Status}}"
docker images --format "{{.Repository}}:{{.Tag}}"
docker inspect --format '{{json .State}}' api        # um bloco em JSON
docker inspect --format '{{.Name}}' $(docker ps -q)   # nomes de todos os rodando
```

> **Dica:** no PowerShell/Windows, troque as aspas simples por aspas duplas e escape as internas, ou use o Git Bash/WSL.

---

## 18. Receitas prontas

```bash
# --- ACOMPANHAR ---
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"   # visão limpa
watch -n 2 docker ps                                             # atualiza a cada 2 s (Linux/macOS)
docker stats --no-stream                                         # foto do consumo

# --- PARAR E REMOVER (dev) ---
docker stop $(docker ps -q)                       # para todos os que estão rodando
docker rm $(docker ps -aq)                        # remove todos os containers
docker rm -f $(docker ps -aq)                     # força (mesmo rodando)
docker container prune -f                         # remove os parados, sem perguntar

# --- DESCOBRIR POR QUE CAIU ---
docker ps -a --filter status=exited               # quem saiu
docker logs --tail 100 -t api                     # últimas linhas com horário
docker inspect -f 'exit={{.State.ExitCode}} oom={{.State.OOMKilled}} err={{.State.Error}}' api
docker events --since 1h --filter container=api   # histórico de eventos

# --- CONTAINER ESPECÍFICO ---
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' api   # IP
docker port api                                                                      # portas
docker exec -it api sh                                                               # entrar
docker cp api:/app/logs ./logs-api                                                   # extrair logs

# --- IMAGENS ---
docker images --format "{{.Size}}\t{{.Repository}}:{{.Tag}}" | sort -h   # por tamanho
docker image prune -f                                                    # remove dangling
docker image prune -a --filter "until=168h" -f                           # sem uso há +7 dias
docker rmi $(docker images -f dangling=true -q)                          # alternativa manual
docker rmi $(docker images "minha-api" -q)                               # todas as versões de uma imagem

# --- ATUALIZAR UM SERVIÇO COM IMAGEM NOVA ---
docker pull postgres:16-alpine
docker stop db && docker rm db
docker run -d --name db -v pgdata:/var/lib/postgresql/data postgres:16-alpine
# (no Compose: docker compose pull && docker compose up -d)

# --- BACKUP / TRANSPORTE ---
docker save minha-api:1.0 | gzip > minha-api-1.0.tar.gz     # salvar imagem
gunzip -c minha-api-1.0.tar.gz | docker load                # restaurar
docker export api | gzip > api-fs.tar.gz                    # só o filesystem do container

# --- LIBERAR ESPAÇO COM CRITÉRIO ---
docker system df                          # 1) ver quem ocupa
docker container prune -f                 # 2) containers parados
docker image prune -f                     # 3) imagens dangling
docker builder prune -f                   # 4) cache de build
# Só se tiver certeza de que pode perder dados de volumes:
# docker system prune -a --volumes
```

---

## 19. Cheat sheet final

```bash
# ===== CONTAINERS: LISTAR =====
docker ps                       # em execução          (= docker container ls)
docker ps -a                    # todos
docker ps -q / -aq              # só IDs
docker ps -s                    # com tamanho
docker ps -f status=exited      # filtrar
docker ps --format "table {{.Names}}\t{{.Status}}"

# ===== CONTAINERS: CRIAR/EXECUTAR =====
docker run -d --name x -p 8080:80 img        # serviço
docker run --rm -it img sh                   # descartável e interativo
docker create --name x img                   # só cria
docker start|stop|restart|kill|pause|unpause x
docker rename x y
docker update --memory 1g --cpus 2 x
docker wait x                                # espera e dá o exit code
docker rm x / docker rm -f x / docker rm -v x

# ===== CONTAINERS: INTERAGIR E INSPECIONAR =====
docker exec -it x sh
docker exec x comando
docker attach x                              # sair: Ctrl+P Ctrl+Q
docker logs -f --tail 100 -t x
docker inspect x / docker inspect -f '{{...}}' x
docker top x ; docker stats ; docker stats --no-stream
docker port x ; docker diff x ; docker events
docker cp x:/origem ./destino  /  docker cp ./origem x:/destino
docker commit x nome:tag ; docker export x > x.tar ; docker import x.tar nome:tag

# ===== IMAGENS =====
docker images                                # = docker image ls
docker images -a / -q / --digests / -f dangling=true
docker search --filter is-official=true nome
docker pull nome:tag ; docker pull nome@sha256:...
docker build -t nome:tag .                   # -f, --no-cache, --target, --build-arg, --platform
docker buildx build --platform linux/amd64,linux/arm64 -t user/app:1.0 --push .
docker tag origem destino ; docker push destino
docker login / docker logout
docker history nome:tag ; docker image inspect nome:tag
docker save -o a.tar nome:tag ; docker load -i a.tar
docker rmi nome:tag ; docker image prune ; docker image prune -a

# ===== LIMPEZA E SISTEMA =====
docker container prune ; docker image prune ; docker builder prune
docker system df ; docker system prune        # (-a --volumes = apaga dados!)
docker info ; docker version
docker context ls ; docker context use nome
```

---

*Complemento de estudo e uso profissional. Sempre confirme o contexto (`docker context ls`) e o ambiente (dev x produção) antes de executar comandos destrutivos (`rm -f`, `prune -a`, `--volumes`).*
