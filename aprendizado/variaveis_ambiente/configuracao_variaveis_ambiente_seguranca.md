# 🔐 Variáveis de Ambiente no PowerShell para Proteger Credenciais

## Objetivo

Evitar que informações sensíveis, como senhas de banco de dados, sejam escritas diretamente em arquivos versionados.

Exemplo que deve ser evitado:

```xml
<password>1985</password>
```

O problema dessa abordagem é que a senha fica exposta dentro do projeto. Caso o arquivo seja enviado para um repositório como GitHub, qualquer pessoa com acesso ao código poderá visualizar a credencial.

A abordagem recomendada é utilizar **variáveis de ambiente** e referenciar o valor somente durante a execução.

Exemplo:

```xml
<password>${env.DB_PASSWORD}</password>
```

O Maven irá buscar uma variável de ambiente chamada:

```text
DB_PASSWORD
```

e utilizar seu valor durante a execução do Flyway.

---

# Como funciona

Fluxo da configuração:

```
Windows
   |
   | DB_PASSWORD=1985
   ↓
Variável de Ambiente
   |
   ↓
Maven
   |
   | ${env.DB_PASSWORD}
   ↓
Flyway Maven Plugin
   |
   ↓
PostgreSQL
```

A senha real não fica armazenada no `pom.xml`.

---

# Verificar se a variável existe

No PowerShell:

```powershell
echo $env:DB_PASSWORD
```

Caso a variável não exista, o retorno será vazio.

Exemplo:

```text
PS> echo $env:DB_PASSWORD

```

---

# Criar variável temporária no PowerShell

Criar uma variável somente para a sessão atual:

```powershell
$env:DB_PASSWORD="1985"
```

Verificar:

```powershell
echo $env:DB_PASSWORD
```

Resultado:

```text
1985
```

Agora o Maven consegue acessar:

```xml
<password>${env.DB_PASSWORD}</password>
```

---

# Executar o Flyway usando a variável

Depois de criar a variável:

```powershell
mvn flyway:migrate
```

O Maven faz a substituição:

Antes:

```xml
<password>${env.DB_PASSWORD}</password>
```

Durante a execução:

```xml
<password>1985</password>
```

O valor é enviado para o Flyway, que realiza a conexão com o PostgreSQL.

---

# Criar variável permanente no Windows

A variável criada com:

```powershell
$env:DB_PASSWORD="1985"
```

existe somente enquanto o terminal estiver aberto.

Para criar uma variável permanente no usuário do Windows:

```powershell
[System.Environment]::SetEnvironmentVariable(
    "DB_PASSWORD",
    "1985",
    "User"
)
```

Depois:

1. Feche o PowerShell.
2. Abra uma nova janela.
3. Verifique:

```powershell
echo $env:DB_PASSWORD
```

Resultado esperado:

```text
1985
```

---

# Exemplo aplicado ao Flyway Maven Plugin

No `pom.xml`:

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>

    <configuration>

        <url>
            jdbc:postgresql://localhost:5432/rest_with_springboot_java
        </url>

        <user>
            postgres
        </user>

        <!--
        A senha não fica escrita no arquivo.
        O Maven busca a variável de ambiente DB_PASSWORD.
        -->
        <password>${env.DB_PASSWORD}</password>

    </configuration>

</plugin>
```

---

# Diferença entre arquivos de configuração

É importante entender que cada ambiente possui sua própria fonte de configuração.

## Maven / Flyway Plugin

Usa:

```text
Variável de ambiente do sistema operacional
```

Exemplo:

```text
DB_PASSWORD=1985
```

Acessada por:

```xml
${env.DB_PASSWORD}
```

---

## Spring Boot

Pode usar:

```properties
spring.datasource.password=${DB_PASSWORD}
```

A variável pode vir de:

- `application-local.properties`
- variável de ambiente
- Docker Compose

---

## Docker Compose

Usa:

```env
DB_PASSWORD=1985
```

através do arquivo:

```text
.env
```

O Docker injeta essa variável dentro do container.

---

# Boas práticas de segurança

## Nunca faça:

```xml
<password>1985</password>
```

Motivos:

- senha fica exposta no código;
- pode ser enviada para o GitHub;
- dificulta trocar credenciais entre ambientes.

---

## Prefira:

```xml
<password>${env.DB_PASSWORD}</password>
```

Benefícios:

✅ senha fora do código  
✅ mais seguro para versionamento  
✅ facilita configuração por ambiente  
✅ padrão usado em projetos profissionais

---

# Arquivos que normalmente não devem ser enviados ao Git

Adicionar ao `.gitignore`:

```gitignore
.env
application-local.properties
```

Esses arquivos podem conter:

- senhas;
- tokens;
- chaves privadas;
- configurações específicas da máquina.

---

# Resumo dos comandos

## Ver variável existente

```powershell
echo $env:DB_PASSWORD
```

---

## Criar variável temporária

```powershell
$env:DB_PASSWORD="1985"
```

---

## Criar variável permanente

```powershell
[System.Environment]::SetEnvironmentVariable(
    "DB_PASSWORD",
    "1985",
    "User"
)
```

---

## Executar Flyway

```powershell
mvn flyway:migrate
```

---

# Conclusão

O uso de variáveis de ambiente permite separar:

```
Código da aplicação
        +
Configuração sensível
```

O projeto mantém apenas a referência:

```
${env.DB_PASSWORD}
```

Enquanto a senha real permanece fora do código, sendo fornecida pelo ambiente onde a aplicação está sendo executada.