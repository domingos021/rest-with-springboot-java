# Guia do Engenheiro de Software: prever o que pode dar errado

> Objetivo: sair do "funciona" e chegar no "sei por que funciona, sei como pode quebrar e provei que está protegido".
> Base deste guia: o seu `PersonService`, os testes `PersonServiceV1Test`/`V2Test` e os mocks `MockPerson`/`MockPersonV2`.

---

## 0. Como usar este guia

1. Leia a **Parte 1** uma vez, devagar. Ela é a mentalidade.
2. Use a **Parte 2** (o método) em TODA funcionalidade nova, na ordem.
3. A **Parte 3** é consulta: perguntas por camada e a matriz "o que acontece se...".
4. A **Parte 4** aplica tudo no seu código real. Tem achados concretos.
5. A **Parte 5** é treino: quebrar o código de propósito. **Preveja antes de rodar.**
6. A **Parte 6** é a rotina de estudo e as leituras.

Regra de ouro para aprender: **prever, rodar, comparar**. Se você só roda, não aprende. O aprendizado está na diferença entre o que você previu e o que aconteceu.

---

# Parte 1. A mentalidade

## 1.1 O que significa "prever as previsibilidades"

Todo sistema falha. A diferença entre júnior e engenheiro não é errar menos, é **ter pensado nas falhas antes de elas acontecerem**. Falhas previsíveis se agrupam em cinco famílias:

| Família | Pergunta-chave | Exemplo no seu código |
|---|---|---|
| Entrada inválida | E se vier lixo? | `create(null)`, senha nula, CPF repetido |
| Estado inesperado | E se o mundo não for como eu assumi? | ID que não existe, registro já removido |
| Concorrência e tempo | E se duas coisas acontecerem juntas ou repetidas? | dois `update` simultâneos, `POST` reenviado |
| Falha de dependência | E se o que eu uso falhar? | banco fora do ar, mapper lançando exceção |
| Abuso e segurança | E se alguém tentar de má-fé? | cliente enviando `role = ADMIN` |

## 1.2 Quatro conceitos que sustentam tudo

- **Invariante**: algo que precisa ser verdadeiro SEMPRE. Ex.: "nenhuma senha é persistida em texto puro". Todo teste importante protege uma invariante.
- **Contrato**: o que um método promete (entrada aceita, saída garantida, erros possíveis). `findById` promete: ou devolve um DTO, ou lança `ResourceNotFoundException`. Nunca `null`.
- **Fronteira de confiança**: tudo que vem de fora (requisição HTTP, arquivo, banco, outra API) é **não confiável** até ser validado. O service não pode assumir que o controller validou.
- **Defesa em profundidade**: nenhuma camada sozinha basta. Validação no DTO, regra no service, restrição no banco. Se uma falhar, a próxima segura.

## 1.3 Teste prova o que você pensou, não o que você esqueceu

Um teste verde significa "os casos que eu imaginei funcionam". Ele não diz nada sobre os casos que você não imaginou. Por isso o método abaixo gasta tanto esforço em **imaginar falhas** (Fase 2) e em **provar que os testes pegariam um defeito** (Fase 6).

---

# Parte 2. O método, passo a passo

Siga a ordem. Cada fase tem as perguntas que você deve **escrever e responder** (em um arquivo, não de cabeça).

## Fase 0. Entender o problema (antes de qualquer código)

Responda por escrito:

1. Quem usa isso? (cliente anônimo, usuário logado, administrador, outro sistema)
2. O que é "funcionar" aqui, em uma frase?
3. Quais dados são **sensíveis**? (senha, CPF, e-mail, telefone, endereço. CPF é dado pessoal sob a LGPD)
4. O que **nunca** pode acontecer? (senha vazar, usuário virar admin sozinho, apagar dado de outro usuário)
5. O que acontece se isso ficar fora do ar uma hora? E se perder um dado?

> Se você não consegue responder 1 e 4, ainda não está pronto para codar.

## Fase 1. Definir o contrato

Para cada operação (ex.: `POST /person`):

| Pergunta | Resposta (exemplo para criar pessoa) |
|---|---|
| Quais campos entram? Quais são obrigatórios? | nome, CPF, e-mail, senha |
| Quais campos o cliente **NÃO** pode enviar? | `id`, `role` (se o cadastro é público) |
| Qual é o formato e o limite de cada campo? | senha mínimo 8, e-mail válido, CPF válido |
| O que a resposta devolve? O que **nunca** devolve? | dados públicos; nunca senha |
| Quais erros são possíveis e com qual status HTTP? | 400 inválido, 409 duplicado, 404 não existe, 401/403 sem permissão |
| A operação pode ser repetida sem efeito colateral (idempotência)? | `PUT`/`DELETE` sim, `POST` não, a menos que tratado |
| Lista grande? | paginação obrigatória |

## Fase 2. Pré-mortem: imaginar como vai quebrar

Imagine que o sistema já está em produção e **deu problema**. Escreva as 10 causas mais prováveis. Depois use o **STRIDE**, que é um roteiro clássico de ameaças:

| Letra | Ameaça | Pergunta | No seu domínio |
|---|---|---|---|
| S | Spoofing (se passar por outro) | Como sei que é quem diz ser? | login, token JWT |
| T | Tampering (alterar dado indevido) | Alguém pode mudar o que não devia? | `update` alterando campos protegidos |
| R | Repudiation (negar autoria) | Consigo provar quem fez o quê? | log de auditoria |
| I | Information disclosure (vazar) | Algum dado sai onde não devia? | senha no JSON, no log, na exceção |
| D | Denial of service (derrubar) | Alguém pode sobrecarregar? | `findAll` sem paginação |
| E | Elevation of privilege (virar mais do que é) | Alguém pode ganhar permissão? | `role` enviado no cadastro |

## Fase 3. Desenhar as responsabilidades

Para cada classe, complete a frase: "Esta classe é responsável por ___ e **não** é responsável por ___".

| Camada | É responsável por | NÃO é responsável por |
|---|---|---|
| Controller | HTTP, status, validação de formato, autenticação/autorização de rota | regra de negócio |
| Service | regra de negócio, orquestração, transação | HTTP, SQL |
| Repository | acesso a dados | regra de negócio |
| Mapper | conversão DTO <-> entidade | decidir regra (ex.: role padrão) |
| DTO | contrato de entrada/saída | lógica |

> Teste de sanidade: se mudar o formato do JSON, quantas classes mudam? Se mudar a regra de role padrão, quantas? O ideal é uma de cada.

## Fase 4. Listar os testes ANTES de codar

Escreva só os **nomes** dos testes, no formato `metodo_quandoCondicao_resultado`. Para cada método, preencha as 4 colunas:

| Método | Caminho feliz | Bordas | Erros | Segurança |
|---|---|---|---|---|
| `create` | cria e devolve DTO | senha nula, role nula, campos no limite | duplicado, DTO nulo, banco falha | role enviada pelo cliente, senha nunca em texto puro |
| `findById` | acha e devolve | id nulo, id negativo | não existe | acessar dado de outro usuário |
| `update` | atualiza e salva | DTO parcial (campos nulos) | não existe | tentar alterar campo protegido |
| `delete` | remove | já removido | não existe | remover dado de outro usuário |
| `findAll` | lista | lista vazia | banco falha | exposição de campos, volume |

Se uma coluna está vazia, **você ainda não pensou nela**. Isso é o sinal.

## Fase 5. Implementar pequeno (vermelho, verde, refatorar)

1. **Vermelho**: escreva UM teste que falha, pelo motivo certo (leia a mensagem de erro).
2. **Verde**: escreva o mínimo de código para passar.
3. **Refatorar**: limpe sem mudar comportamento, com os testes verdes.

Se o teste passou de primeira sem você escrever código, desconfie: ele pode não estar testando nada.

## Fase 6. Quebrar de propósito (mutação manual)

Veja a **Parte 5**. É a fase que separa quem tem testes de quem tem testes **confiáveis**.

## Fase 7. Revisar com checklist

Antes de considerar pronto, passe pela **Definição de Pronto** (Parte 3.4).

## Fase 8. Depois do deploy

Perguntas: como eu saberia que quebrou? (log, métrica, alerta). Como eu volto atrás? (rollback, migração reversível). O que eu aprendi? (**Diário de falhas**, Parte 6.2).

---

# Parte 3. Consulta rápida

## 3.1 Matriz "o que acontece se...?"

Aplique a CADA método público. Responda "o que o sistema faz" e "é isso mesmo que eu quero?".

| Categoria | Perguntas |
|---|---|
| **Entrada** | e se for `null`? vazio? só espaços? enorme (10 MB)? com acento, emoji, aspas, `<script>`? negativo? tipo errado? |
| **Estado** | e se já existir? e se não existir? e se estiver em uso por outra entidade? e se tiver mudado desde que li? |
| **Tempo** | e se duas requisições chegarem juntas? e se a mesma for reenviada (retry)? e se demorar 30 s? |
| **Ambiente** | e se o banco estiver fora? e se uma dependência externa for lenta? e se o disco encher? |
| **Permissão** | e se não estiver logado? logado mas sem permissão? token expirado? **logado, mas pedindo o dado de OUTRO usuário?** |

> Essa última (acessar o recurso de outro usuário trocando o ID) tem nome: **IDOR**. É um dos erros de segurança mais comuns em APIs REST.

## 3.2 Perguntas por camada

**Controller**
- Toda entrada tem validação (`@Valid`, `@NotBlank`, `@Email`, `@Size`)?
- O status HTTP está certo para cada erro (400, 401, 403, 404, 409, 500)?
- A resposta de erro vaza detalhe interno (stack trace, SQL, nome de tabela)?
- A rota exige autenticação? Autorização por papel (`@PreAuthorize`)?

**Service**
- Quais invariantes este método garante?
- Ele confia em algo que veio de fora sem validar?
- Precisa de `@Transactional`? (várias escritas que devem ser tudo-ou-nada)
- Todo caminho de erro lança uma exceção **de negócio** clara?

**Repository / banco**
- Há restrições no banco (`UNIQUE` em CPF/e-mail, `NOT NULL`, chaves estrangeiras)? Não dependa só do código Java.
- Consultas usam parâmetros (JPA faz isso) e nunca concatenam texto?
- `findAll` pode devolver milhões de linhas?
- O que acontece ao remover um registro referenciado por outros?

**Mapper / DTO**
- O DTO de saída contém algum campo sensível?
- O DTO de entrada contém algum campo que o cliente **não** deveria controlar (`id`, `role`)?
- No `update`, campo `null` no DTO apaga o valor existente ou é ignorado? (decisão consciente)
- O `toString()` do DTO imprime a senha? (veja Lombok `@ToString.Exclude`)

**Segurança geral**
- Senha: hash com BCrypt, nunca em texto puro, nunca em log, nunca na resposta.
- Segredos (chaves, senhas de banco) fora do código, em variáveis de ambiente.
- Logs: sem senha, sem token, sem CPF completo.
- Limite de tentativas (rate limit) em login?

## 3.3 Onde cada tipo de teste mora

| Camada | Ferramenta | O que prova | O que NÃO prova |
|---|---|---|---|
| Service | JUnit + Mockito | regra de negócio isolada | comportamento real do banco |
| Mapper | JUnit puro | conversão correta, campo sensível não vaza | regra de negócio |
| Repository | `@DataJpaTest` (idealmente Testcontainers) | consultas, restrições `UNIQUE`, mapeamento JPA | regra de negócio |
| Controller | `@WebMvcTest` + MockMvc | status, JSON, validação, segurança de rota | regra de negócio |
| Integração | `@SpringBootTest` + Testcontainers | o fluxo completo funciona junto | cada detalhe interno |

> **Atenção, lição importante:** em teste de service, o mock devolve o que VOCÊ mandou. Se `repository.findById(null)` num mock devolve `Optional.empty()`, o teste passa. No banco real, o Spring Data lança exceção para `null`. **Mock não revela comportamento real de dependência.** Por isso existem os outros níveis.

## 3.4 Definição de Pronto (checklist de revisão)

- [ ] Contrato definido (entradas, saídas, erros, status).
- [ ] Toda entrada externa validada na borda (`@Valid`) **e** o service não depende disso para ser seguro.
- [ ] Nenhum campo sensível na resposta, no log ou na exceção.
- [ ] Campos que o cliente não pode controlar não existem no DTO de entrada.
- [ ] Restrições críticas existem também no banco (`UNIQUE`, `NOT NULL`).
- [ ] Erros de negócio têm exceção própria e status HTTP correto.
- [ ] Testes: feliz, bordas, erros e segurança (a tabela da Fase 4 sem colunas vazias).
- [ ] Os testes pegam defeitos (Fase 6: mutações propostas foram detectadas).
- [ ] Sem `System.out`, sem código comentado, sem segredo no repositório.
- [ ] Nomes revelam intenção; comentários explicam o **porquê**, não o óbvio.

---

# Parte 4. Aplicando ao seu código: análise do PersonService

> Leitura crítica baseada nos arquivos que você enviou. Itens marcados **[verificar]** dependem de código que não vi (controller, entidade, configuração de segurança). Confirme antes de agir.

## 4.1 Achados, do mais grave ao menos grave

### A) Possível escalada de privilégio pelo cadastro  **[verificar]**

- `PersonInsertDTO` tem o campo `role` (o `mockInsertDTO` faz `setRole`).
- O service só define `CLIENT` **se a role vier nula** (`if (person.getRole() == null)`). Se vier `ADMIN`, ele mantém `ADMIN`.
- **Se o endpoint de cadastro for público**, qualquer pessoa pode enviar `{"role": "ADMIN"}` e se criar como administrador. Isso é **Elevation of Privilege** (STRIDE-E) e **Mass Assignment**.
- Perguntas a responder: o `POST /person` é público? Existe restrição na `SecurityConfig`? Quem deve poder criar um ADMIN?
- Correções possíveis (escolha conscientemente): (1) DTO de cadastro público **sem** `role`, o service sempre usa `CLIENT`; (2) criação de ADMIN em endpoint separado com `@PreAuthorize("hasRole('ADMIN')")`.

### B) Conta sem senha

- `if (personDto.getPassword() != null)` criptografa. Se vier `null`, o service **cria a pessoa sem senha** e segue. Na prática, é uma conta com senha nula no banco.
- Pergunta: criar pessoa sem senha é um caso de negócio válido? Se não, o certo é **rejeitar** (`@NotBlank` + tamanho mínimo no DTO, e/ou exceção no service).
- Note: seu teste `create_whenPasswordIsNull_doesNotCallPasswordEncoder` documenta o comportamento **atual**. Se você decidir rejeitar, esse teste muda para `assertThrows`.

### C) CPF/e-mail duplicado  **[verificar]**

- O service não checa duplicidade. Se houver `UNIQUE` no banco, o resultado é uma `DataIntegrityViolationException` que, sem tratamento, vira **500**. Se **não** houver `UNIQUE`, você terá dois cadastros com o mesmo CPF.
- Pergunta: o que deve acontecer? Resposta típica: **409 Conflict** com mensagem clara. Defesa em profundidade: `UNIQUE` no banco **e** tratamento da exceção (ou checagem prévia, sabendo que checagem prévia sozinha tem condição de corrida).

### D) `findAll` sem paginação (negação de serviço)

- `repository.findAll().stream()` carrega a tabela inteira em memória. Com muitos registros, derruba a aplicação. Isso é **DoS** (STRIDE-D).
- Correção: `Pageable`/`Page<PersonDTO>`, com limite máximo de tamanho de página.

### E) IDOR em `findById`/`update`/`delete`  **[verificar]**

- O service não recebe quem está pedindo. Qualquer usuário autenticado pode consultar, alterar ou apagar qualquer ID? Se o negócio diz que "cada um só mexe nos próprios dados", essa checagem precisa existir (no controller com `@PreAuthorize`, ou no service comparando o usuário logado com o dono).

### F) Atualização concorrente (lost update)

- Dois `update` simultâneos no mesmo registro: o último sobrescreve o primeiro sem aviso.
- Correção clássica: **bloqueio otimista** com campo `@Version` na entidade.

### G) `update` com campos nulos  **[verificar no mapper]**

- No `PersonMapper_MapStruct.updateEntityFromDTO`, um campo `null` no DTO **apaga** o valor atual ou é **ignorado**? Depende de `nullValuePropertyMappingStrategy`. Decida e **teste** (teste de mapper).

### H) Vazamento de senha em log  **[verificar]**

- Se `PersonInsertDTO` usa Lombok `@Data`, o `toString()` inclui a senha. Qualquer `logger.info("... {}", dto)` vaza. Teste simples: `assertFalse(dto.toString().contains("password"))`, ou use `@ToString.Exclude`.

### I) Consistência e legibilidade (baixo risco)

- Mensagens de exceção em dois idiomas e dois textos. Centralize numa constante ou num método `getPersonOrThrow(id)`.
- `delete` faz `findById` + `delete` (duas idas ao banco). Aceitável, mas saiba que existe a janela entre as duas.

## 4.2 O que seu código já faz bem (mantenha)

- `PersonUpdateDTO` **não** tem `id`, `cpf`, `email`, `password`, `role`: é uma **lista branca** de campos editáveis. Isso é a defesa certa contra Mass Assignment no `update`.
- DTO de resposta **sem senha**.
- Hash de senha com `PasswordEncoder` (BCrypt), no service.
- Injeção por construtor.
- Exceção de negócio tratada de forma global (`ControllerAdvice`).

---

# Parte 5. Quebrar de propósito: mutação manual

## 5.1 A técnica

1. Faça uma cópia do projeto ou use um branch.
2. Introduza **um** defeito pequeno no service (uma "mutação").
3. **ANTES de rodar**, escreva: "o teste X vai falhar" ou "nenhum vai falhar".
4. Rode `mvn test`.
5. Compare. Três resultados possíveis:
    - **Falhou exatamente onde você previu**: ótimo, você entende o código e os testes.
    - **Falhou onde você não previu**: seu modelo mental estava errado. Estude o porquê.
    - **Nenhum teste falhou**: a mutação **sobreviveu**. Há um buraco nos testes. Escreva o teste que o fecha.
6. Desfaça a mutação (`git checkout .`).

## 5.2 Tabela de mutações no PersonService (V1)

Preencha a coluna "Sua previsão" **antes** de rodar. A coluna "Esperado" é a minha análise; confira se bate.

| # | Mutação no service | Sua previsão | Esperado |
|---|---|---|---|
| M1 | Apagar a linha `person.setPassword(passwordEncoder.encode(...))` | | Falha: `create_whenPasswordIsProvided...` (captor e `InOrder`), e os testes de role dão `UnnecessaryStubbingException` (o `encode` stubado nunca é chamado) |
| M2 | Mover `repository.save(person)` para **antes** do `if` da senha | | Falha por `InOrder` no teste de senha. **Lição:** o `ArgumentCaptor` sozinho NÃO pegaria, porque guarda a referência e a senha é trocada depois no mesmo objeto. Só a ordem pega |
| M3 | Apagar o `if (person.getRole() == null) ... setRole(CLIENT)` | | Falha: `create_whenRoleIsNull_setsDefaultRoleClient` |
| M4 | Trocar o `if` por `person.setRole(PersonRole.CLIENT)` sempre | | Falha: `create_whenRoleIsProvided_keepsOriginalRole` (ADMIN virou CLIENT) |
| M5 | Inverter a checagem: `personDto.getPassword() == null` | | Falham os testes de senha |
| M6 | Em `findById`, trocar `.orElseThrow(...)` por `.orElse(null)` | | Falha: `findById_whenPersonDoesNotExist...` (não lança exceção) |
| M7 | Em `update`, apagar `repository.save(entity)` | | Falha: `update_whenPersonExists...` |
| M8 | Em `update`, chamar `updateEntityFromDTO` **depois** do `save` | | Falha por `InOrder` |
| M9 | Em `delete`, trocar por `repository.deleteById(id)` sem buscar | | Falham os dois testes de `delete` |
| M10 | Em `findAll`, devolver `.limit(1)` | | Falha: `findAll_whenThereArePeople...` |
| M11 | Em `create`, trocar `mapEntityToDto(savedPerson)` por `mapEntityToDto(person)` | | **SOBREVIVE.** Veja 5.3 |
| M12 | Em `update`, trocar `mapEntityToDto(updatedPerson)` por `mapEntityToDto(entity)` | | **SOBREVIVE.** Mesma causa |

## 5.3 Um buraco real nos testes que escrevemos (M11 e M12)

Nos testes, fizemos `when(repository.save(entity)).thenReturn(entity)`. O `save` devolve **a mesma instância** que recebeu. Então `person` e `savedPerson` são o mesmo objeto, e trocar um pelo outro no service não muda nada para o teste.

Na vida real o `save` pode devolver **outra instância** (com `id` gerado, versão, valores do banco). O service precisa converter **a que o banco devolveu**.

**Como fechar o buraco**: faça o `save` devolver um objeto **diferente** e stubbe o mapper para esse:

```java
@Test
@DisplayName("create: converte a entidade DEVOLVIDA pelo save, não a enviada")
void create_mapsTheEntityReturnedBySave() {
    Person beforeSave = input.mockEntity(1);
    Person afterSave  = input.mockEntity(2);          // instância diferente, simula o banco
    PersonInsertDTO insertDto = input.mockInsertDTO(1);
    PersonDTO responseDto = input.mockDTO(2);

    when(mapper.mapDtoToEntity(insertDto)).thenReturn(beforeSave);
    when(passwordEncoder.encode(insertDto.getPassword())).thenReturn("hash");
    when(repository.save(beforeSave)).thenReturn(afterSave);
    when(mapper.mapEntityToDto(afterSave)).thenReturn(responseDto);

    PersonDTO result = service.create(insertDto);

    assertSame(responseDto, result);
    verify(mapper, never()).mapEntityToDto(beforeSave);
}
```

Com esse teste, M11 passa a **falhar**. Repita a ideia para o `update` (M12).

> Aprendizado: um teste pode estar verde e inútil para um defeito específico. Só a mutação mostra isso.

## 5.4 Automatizar: mutation testing com PIT

O que você fez à mão, a ferramenta **PIT (Pitest)** faz sozinha: gera dezenas de mutações, roda os testes e dá um relatório de quais sobreviveram. Há plugin Maven e extensão para JUnit 5 (`pitest-junit5-plugin`). Consulte a documentação oficial para a versão atual e a configuração. Faça à mão primeiro; a ferramenta faz sentido quando você já entende o que ela mede.

## 5.5 Testes novos que expõem os riscos da Parte 4

**Teste vermelho de segurança (achado A).** Ele deve **falhar hoje**. Isso é o objetivo: ele prova a vulnerabilidade e depois guia a correção.

```java
@Test
@DisplayName("create: cliente que envia role ADMIN no cadastro público é salvo como CLIENT")
void create_whenClientSendsAdminRole_savesAsClient() {
    Person entity = input.mockEntity(1);              // índice ímpar = ADMIN
    PersonInsertDTO insertDto = input.mockInsertDTO(1);

    when(mapper.mapDtoToEntity(insertDto)).thenReturn(entity);
    when(passwordEncoder.encode(insertDto.getPassword())).thenReturn("hash");
    when(repository.save(entity)).thenReturn(entity);
    when(mapper.mapEntityToDto(entity)).thenReturn(input.mockDTO(1));

    service.create(insertDto);

    ArgumentCaptor<Person> saved = ArgumentCaptor.forClass(Person.class);
    verify(repository).save(saved.capture());
    assertEquals(PersonRole.CLIENT, saved.getValue().getRole());   // hoje vai falhar
}
```

> Atenção: este teste **contradiz** `create_whenRoleIsProvided_keepsOriginalRole`. Isso é uma **decisão de negócio** que você precisa tomar e registrar: "o cadastro público respeita a role enviada?" (quase certamente não). Quando decidir, um dos dois testes é removido e a regra fica explícita.

**Teste de DTO nulo (contrato do método).**

```java
@Test
@DisplayName("create: DTO nulo é rejeitado com exceção clara")
void create_whenDtoIsNull_throws() {
    assertThrows(IllegalArgumentException.class, () -> service.create(null));
    verifyNoInteractions(repository, mapper, passwordEncoder);
}
```

Hoje vai falhar com `NullPointerException` em `personDto.getPassword()`. Decida o contrato (`IllegalArgumentException`, `Objects.requireNonNull`, ou garantir que o controller nunca passa `null`) e escreva o teste que o fixa.

**Teste de repositório: restrição de CPF único** (precisa de `UNIQUE` na entidade; **[verificar]**).

```java
@DataJpaTest
class PersonRepositoryTest {

    @Autowired PersonRepository repository;

    @Test
    void save_whenCpfAlreadyExists_throwsDataIntegrityViolation() {
        MockPerson input = new MockPerson();
        Person first = input.mockEntity(1);
        Person second = input.mockEntity(2);
        second.setCpf(first.getCpf());                 // mesmo CPF
        first.setId(null); second.setId(null);         // deixa o banco gerar os ids

        repository.saveAndFlush(first);

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(second));
    }
}
```

> `saveAndFlush` (e não `save`) força o SQL a rodar na hora; com `save`, o erro só apareceria no commit. Se o teste passar sem exceção, você descobriu que **não há** `UNIQUE` no mapeamento. Outra lição: H2 em memória pode se comportar diferente do PostgreSQL. Para confiança real, use Testcontainers com o mesmo banco da produção.

**Teste de controller: validação e vazamento** (esqueleto; ajuste à sua configuração de segurança).

```java
@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean PersonService service;   // em versões antigas do Spring Boot: @MockBean

    @Test
    void create_whenPasswordIsMissing_returns400() throws Exception {
        mockMvc.perform(post("/person/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Ana","cpf":"000.000.001-00","email":"a@a.com"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_neverReturnsPassword() throws Exception {
        when(service.findById(1L)).thenReturn(new MockPerson().mockDTO(1));

        mockMvc.perform(get("/person/v1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
```

---

# Parte 6. Rotina de estudo

## 6.1 Exercícios graduados (faça na ordem)

1. **Mutação à mão**: faça M1 a M12 da tabela, com previsão escrita antes. Registre quantas você acertou.
2. **Fechar M11/M12** com o teste da seção 5.3.
3. **Decidir e testar** a regra de role no cadastro (achado A). Escreva a decisão em uma frase, depois o teste e a correção.
4. **Decidir e testar** a senha nula (achado B): rejeitar com validação no DTO e teste de controller `400`.
5. **Teste de mapper**: `updateEntityFromDTO` com campo nulo (achado G) e `mapEntityToDto` sem senha.
6. **Paginação**: troque `findAll()` por `Page<PersonDTO>`; escreva o teste do limite de página.
7. **`getPersonOrThrow(id)`**: extraia o método privado e prove, rodando os testes, que nada quebrou (refatoração segura).
8. **Teste de repositório** com `UNIQUE` e depois com Testcontainers.
9. **Bloqueio otimista**: adicione `@Version` e escreva o teste de duas atualizações concorrentes.
10. **Pré-mortem** do ProStore: liste 10 causas prováveis de incidente em produção e classifique cada uma em STRIDE.

## 6.2 Diário de falhas

Mantenha um arquivo `diario-de-falhas.md`. Cada vez que um teste falhar de forma inesperada ou um bug aparecer, registre:

```
Data:
O que eu esperava:
O que aconteceu:
Causa raiz (por que de fato aconteceu):
Que pergunta eu deveria ter feito antes?
Que teste teria pegado isso?
```

A coluna "que pergunta eu deveria ter feito" é a que treina o seu olhar de engenheiro. Em dois ou três meses você terá a sua própria checklist, construída com erros reais.

## 6.3 Autoavaliação semanal (5 minutos)

- Consigo explicar, sem olhar, o fluxo completo de `create` (do JSON ao banco e de volta)?
- Consigo apontar a invariante que cada teste protege?
- Para um bug novo, consigo prever qual camada deveria ter barrado?
- Estou escrevendo testes que **poderiam falhar** por um motivo real?

## 6.4 Leituras (em ordem de custo-benefício)

- **OWASP Top 10** e **OWASP Cheat Sheet Series** (gratuitos): segurança de aplicação, direto ao ponto.
- **Effective Java**, Joshua Bloch: como escrever Java correto e robusto.
- **Unit Testing: Principles, Practices, and Patterns**, Vladimir Khorikov: o que vale testar e como.
- **Test-Driven Development: By Example**, Kent Beck: o ciclo vermelho, verde, refatorar.
- **Release It!**, Michael Nygard: como sistemas falham em produção e como se proteger.
- **Designing Data-Intensive Applications**, Martin Kleppmann: dados, concorrência e consistência (mais avançado).
- **Documentação do Spring Security**: autenticação, autorização, `@PreAuthorize`.
- **LGPD (Lei 13.709/2018)**: o básico sobre dados pessoais, porque CPF, e-mail e telefone entram nela.

---

## Resumo em uma página

1. Entenda quem usa e o que **nunca** pode acontecer.
2. Defina o contrato: entradas, saídas, erros, o que o cliente **não** controla.
3. Faça o pré-mortem (STRIDE): como isso quebra? Como é atacado?
4. Liste os testes antes: feliz, bordas, erros, segurança. Coluna vazia é falta de pensamento.
5. Implemente pequeno: vermelho, verde, refatorar.
6. **Quebre de propósito** e confira se os testes pegam. Mutação que sobrevive é um buraco.
7. Passe pela Definição de Pronto.
8. Registre o que aprendeu no diário de falhas.

> Código que funciona é o começo. Engenharia é saber por que funciona, saber como quebra e ter a prova de que está protegido.