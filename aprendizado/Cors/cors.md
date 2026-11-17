# CORS (Cross-Origin Resource Sharing)

## Índice

1. [O que é CORS](#1-o-que-é-cors)
2. [O que é uma "origem"](#2-o-que-é-uma-origem)
3. [Same-Origin Policy (SOP)](#3-same-origin-policy-sop)
4. [Como o CORS funciona](#4-como-o-cors-funciona)
5. [Requisições simples](#5-requisições-simples)
6. [Preflight (requisições não simples)](#6-preflight-requisições-não-simples)
7. [Headers de CORS](#7-headers-de-cors)
8. [CORS com credenciais](#8-cors-com-credenciais)
9. [Exemplos de configuração](#9-exemplos-de-configuração)
10. [Erros comuns e soluções](#10-erros-comuns-e-soluções)
11. [Boas práticas de segurança](#11-boas-práticas-de-segurança)
12. [Resumo rápido](#12-resumo-rápido)

---

## 1. O que é CORS

**CORS** é um mecanismo de segurança dos **navegadores** que controla se uma página web
pode fazer requisições para um servidor de **outra origem**.

Pontos importantes:

- É uma regra aplicada pelo **navegador**, não pelo servidor.
- O servidor apenas **informa** (via headers) quais origens ele permite.
- Ferramentas como Postman, cURL e chamadas servidor-a-servidor **não sofrem** CORS.
- CORS **não é** um mecanismo de autenticação. Ele só relaxa uma restrição do navegador.

---

## 2. O que é uma "origem"

Uma origem é a combinação de **protocolo + domínio + porta**.

```
https://meusite.com:443/pagina
└─┬──┘   └────┬─────┘└┬┘
protocolo  domínio  porta
```

### Comparando com `https://meusite.com`

| URL                          | Mesma origem? | Motivo                |
|------------------------------|:-------------:|-----------------------|
| `https://meusite.com/api`    | ✅ Sim        | Só muda o caminho     |
| `http://meusite.com`         | ❌ Não        | Protocolo diferente   |
| `https://api.meusite.com`    | ❌ Não        | Subdomínio diferente  |
| `https://meusite.com:8080`   | ❌ Não        | Porta diferente       |
| `https://outrosite.com`      | ❌ Não        | Domínio diferente     |

---

## 3. Same-Origin Policy (SOP)

Por padrão, o navegador bloqueia que um JavaScript carregado de uma origem **leia**
respostas de outra origem. Isso impede, por exemplo, que um site malicioso leia os dados
da sua conta bancária usando a sua sessão.

O **CORS é a forma controlada de flexibilizar** essa regra: o servidor diz
"aceito requisições vindas dessa origem".

---

## 4. Como o CORS funciona

Cenário comum de desenvolvimento:

- **Front-end:** `http://localhost:3000` (React, Vite, etc.)
- **Back-end:** `http://localhost:8080` (Spring Boot, Express, etc.)

Portas diferentes = origens diferentes = **CORS entra em ação**.

```
Navegador (localhost:3000)                    Servidor (localhost:8080)
        │                                              │
        │  GET /produtos                               │
        │  Origin: http://localhost:3000               │
        │─────────────────────────────────────────────▶│
        │                                              │
        │  200 OK                                      │
        │  Access-Control-Allow-Origin: http://localhost:3000
        │◀─────────────────────────────────────────────│
        │                                              │
   Navegador confere o header e libera o acesso ao JS
```

Se o header `Access-Control-Allow-Origin` não existir ou não bater com a origem,
o navegador **bloqueia a leitura da resposta** e mostra o erro no console.

> A requisição normalmente **chega** ao servidor e é processada. O que o navegador
> bloqueia é o JavaScript ler a resposta.

---

## 5. Requisições simples

Uma requisição é "simples" (não gera preflight) quando cumpre **todas** estas condições:

- Método: `GET`, `HEAD` ou `POST`
- Headers manuais limitados a: `Accept`, `Accept-Language`, `Content-Language`, `Content-Type`
- `Content-Type` apenas: `text/plain`, `multipart/form-data` ou `application/x-www-form-urlencoded`

### Exemplo

```js
// Requisição simples: GET sem headers customizados
fetch("http://localhost:8080/produtos")
  .then((res) => res.json())
  .then((dados) => console.log(dados));
```

**Requisição enviada:**

```http
GET /produtos HTTP/1.1
Host: localhost:8080
Origin: http://localhost:3000
```

**Resposta esperada:**

```http
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:3000
Content-Type: application/json
```

---

## 6. Preflight (requisições não simples)

Se a requisição **não** for simples, o navegador envia antes uma requisição de verificação
com o método **`OPTIONS`**, chamada **preflight**, perguntando ao servidor se pode enviar a real.

Casos que disparam preflight:

- Métodos `PUT`, `PATCH`, `DELETE`
- `Content-Type: application/json`
- Headers customizados, como `Authorization`

### Exemplo

```js
fetch("http://localhost:8080/produtos", {
  method: "POST",
  headers: {
    "Content-Type": "application/json",
    Authorization: "Bearer meu-token",
  },
  body: JSON.stringify({ nome: "Teclado", preco: 150 }),
});
```

### Fluxo completo

```
Navegador                                         Servidor
    │                                                │
    │ 1) PREFLIGHT                                   │
    │ OPTIONS /produtos                              │
    │ Origin: http://localhost:3000                  │
    │ Access-Control-Request-Method: POST            │
    │ Access-Control-Request-Headers: content-type,authorization
    │───────────────────────────────────────────────▶│
    │                                                │
    │ 204 No Content                                 │
    │ Access-Control-Allow-Origin: http://localhost:3000
    │ Access-Control-Allow-Methods: GET, POST, PUT, DELETE
    │ Access-Control-Allow-Headers: Content-Type, Authorization
    │ Access-Control-Max-Age: 3600                   │
    │◀───────────────────────────────────────────────│
    │                                                │
    │ 2) REQUISIÇÃO REAL (só se o preflight passar)  │
    │ POST /produtos                                 │
    │───────────────────────────────────────────────▶│
    │                                                │
    │ 201 Created                                    │
    │ Access-Control-Allow-Origin: http://localhost:3000
    │◀───────────────────────────────────────────────│
```

O header `Access-Control-Max-Age` define por quantos segundos o navegador
**guarda em cache** o resultado do preflight, evitando repeti-lo a cada chamada.

---

## 7. Headers de CORS

### Enviados pelo navegador

| Header                             | Função                                              |
|------------------------------------|-----------------------------------------------------|
| `Origin`                           | Origem de quem está fazendo a requisição            |
| `Access-Control-Request-Method`    | Método que será usado (no preflight)                |
| `Access-Control-Request-Headers`   | Headers que serão enviados (no preflight)           |

### Enviados pelo servidor

| Header                               | Função                                                        | Exemplo                          |
|--------------------------------------|---------------------------------------------------------------|----------------------------------|
| `Access-Control-Allow-Origin`        | Origem permitida                                              | `https://meusite.com`            |
| `Access-Control-Allow-Methods`       | Métodos permitidos                                            | `GET, POST, PUT, DELETE`         |
| `Access-Control-Allow-Headers`       | Headers permitidos                                            | `Content-Type, Authorization`    |
| `Access-Control-Allow-Credentials`   | Permite cookies/credenciais                                   | `true`                           |
| `Access-Control-Expose-Headers`      | Headers da resposta que o JS pode ler                         | `X-Total-Count`                  |
| `Access-Control-Max-Age`             | Tempo (s) de cache do preflight                               | `3600`                           |

---

## 8. CORS com credenciais

Para enviar **cookies** ou **sessão** entre origens, é preciso configurar os dois lados.

**Front-end:**

```js
fetch("http://localhost:8080/perfil", {
  credentials: "include", // envia cookies
});
```

**Back-end (headers da resposta):**

```http
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Credentials: true
```

### Regras importantes

- Com credenciais, **não pode** usar `Access-Control-Allow-Origin: *`.
  É obrigatório informar a origem **exata**.
- O mesmo vale para `Allow-Headers: *` e `Allow-Methods: *`: o curinga não funciona com credenciais.

---

## 9. Exemplos de configuração

### 9.1 Spring Boot: configuração global (recomendada)

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "https://meusite.com")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
```

### 9.2 Spring Boot: por controller ou método

```java
@RestController
@RequestMapping("/produtos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProdutoController {

    @GetMapping
    public List<Produto> listar() { /* ... */ }

    // Sobrescreve a configuração da classe para este método
    @CrossOrigin(origins = "https://admin.meusite.com")
    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) { /* ... */ }
}
```

### 9.3 Spring Boot com Spring Security

Com Spring Security, o CORS precisa ser habilitado **também no filtro de segurança**,
senão o preflight pode ser bloqueado antes de chegar à configuração do MVC.

```java
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())   // usa o bean corsConfigurationSource
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

### 9.4 Node.js + Express (pacote `cors`)

```bash
npm install cors
```

```js
const express = require("express");
const cors = require("cors");

const app = express();

app.use(
  cors({
    origin: ["http://localhost:3000", "https://meusite.com"],
    methods: ["GET", "POST", "PUT", "PATCH", "DELETE"],
    allowedHeaders: ["Content-Type", "Authorization"],
    credentials: true,
    maxAge: 3600,
  })
);

app.get("/produtos", (req, res) => {
  res.json([{ id: 1, nome: "Teclado" }]);
});

app.listen(8080);
```

### 9.5 Node.js sem biblioteca (manual)

```js
app.use((req, res, next) => {
  res.header("Access-Control-Allow-Origin", "http://localhost:3000");
  res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
  res.header("Access-Control-Allow-Headers", "Content-Type, Authorization");

  // Responde o preflight imediatamente
  if (req.method === "OPTIONS") {
    return res.sendStatus(204);
  }
  next();
});
```

### 9.6 Python + Flask

```python
from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
CORS(app, origins=["http://localhost:3000"], supports_credentials=True)
```

### 9.7 Nginx

```nginx
location /api/ {
    add_header Access-Control-Allow-Origin  "https://meusite.com" always;
    add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
    add_header Access-Control-Allow-Headers "Content-Type, Authorization" always;

    if ($request_method = OPTIONS) {
        return 204;
    }

    proxy_pass http://backend:8080;
}
```

### 9.8 Alternativa no desenvolvimento: proxy do Vite

Em vez de configurar CORS, o front-end pode redirecionar as chamadas pelo próprio servidor de dev,
fazendo o navegador enxergar tudo como **mesma origem**.

```js
// vite.config.js
export default {
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
};
```

```js
// O front chama a própria origem; o Vite encaminha para o back-end
fetch("/api/produtos");
```

> Serve apenas para desenvolvimento. Em produção, configure CORS no back-end
> ou use um reverse proxy (Nginx, etc.).

---

## 10. Erros comuns e soluções

### Erro 1: `No 'Access-Control-Allow-Origin' header is present`

```
Access to fetch at 'http://localhost:8080/produtos' from origin
'http://localhost:3000' has been blocked by CORS policy:
No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

**Causa:** o servidor não enviou o header (CORS não configurado).
**Solução:** habilitar CORS no back-end com a origem do front.

### Erro 2: o preflight falha

```
Response to preflight request doesn't pass access control check:
It does not have HTTP ok status.
```

**Causa:** o servidor não trata `OPTIONS` ou o Spring Security/autenticação bloqueia o preflight.
**Solução:** responder `OPTIONS` com 2xx e habilitar CORS no filtro de segurança (veja 9.3).

### Erro 3: curinga com credenciais

```
The value of the 'Access-Control-Allow-Origin' header must not be the
wildcard '*' when the request's credentials mode is 'include'.
```

**Causa:** `Allow-Origin: *` combinado com `credentials: "include"`.
**Solução:** informar a origem exata do front.

### Erro 4: header não permitido

```
Request header field authorization is not allowed by
Access-Control-Allow-Headers in preflight response.
```

**Causa:** o header `Authorization` não está em `Access-Control-Allow-Headers`.
**Solução:** adicioná-lo à lista de headers permitidos.

### Erro 5: configurou, mas continua falhando

Checklist:

- [ ] A origem bate **exatamente**? (`http` vs `https`, porta, barra no final)
- [ ] O método está em `allowedMethods`?
- [ ] O `OPTIONS` está sendo respondido com sucesso?
- [ ] Se usa Spring Security, o `.cors()` está ativo?
- [ ] Um proxy/gateway no meio não está removendo os headers?
- [ ] O servidor realmente está no ar? (erro de rede também aparece como CORS às vezes)

---

## 11. Boas práticas de segurança

- ❌ **Evite `*` em produção** quando a API tem dados sensíveis. Liste as origens permitidas.
- ✅ Use **whitelist** de origens conhecidas.
- ✅ Não reflita o header `Origin` da requisição sem validar contra uma lista.
- ✅ Libere só os **métodos e headers** realmente necessários.
- ✅ Use `allowCredentials` apenas quando precisar de cookies/sessão.
- ✅ Defina `maxAge` para reduzir o número de preflights.
- ⚠️ CORS **não protege o servidor**: qualquer cliente fora do navegador ignora essa regra.
  Continue validando autenticação e autorização em toda rota.
- ⚠️ CORS **não substitui proteção contra CSRF**.

---

## 12. Resumo rápido

| Conceito          | Resumo                                                                      |
|-------------------|-----------------------------------------------------------------------------|
| O que é           | Regra do navegador para requisições entre origens diferentes                |
| Origem            | protocolo + domínio + porta                                                 |
| Quem bloqueia     | O **navegador**, não o servidor                                             |
| Quem libera       | O **servidor**, via headers `Access-Control-*`                              |
| Preflight         | Requisição `OPTIONS` automática antes de requisições "não simples"          |
| Credenciais       | Exigem origem exata + `Allow-Credentials: true` + `credentials: "include"`  |
| Dev               | Configurar CORS no back-end ou usar proxy do bundler                        |
| Prod              | Whitelist de origens, sem `*` em APIs sensíveis                             |

### Fluxo de decisão

```
O front e o back têm a mesma origem?
 ├─ Sim → CORS não se aplica.
 └─ Não → A requisição é simples?
          ├─ Sim → Servidor responde com Access-Control-Allow-Origin.
          └─ Não → Navegador faz preflight (OPTIONS)
                    └─ Servidor aprova → requisição real é enviada.
```
