# Restaurant Management API

Sistema de gestão compartilhada para restaurantes — **POSTech Fase 01** (Arquitetura e Desenvolvimento Java).

## Tecnologias

- Java 21 (Virtual Threads habilitadas)
- Spring Boot 3.2.5
- Spring Security + JWT (JJWT 0.12.5)
- Spring Data JPA
- PostgreSQL 16
- Docker + Docker Compose
- Springdoc OpenAPI (Swagger UI)
- JUnit 5 + Mockito

## Arquitetura

```
controller  →  service  →  repository  →  database
    ↕              ↕
   DTOs        domain/model
    ↕
exception (GlobalExceptionHandler + ProblemDetail RFC 7807)
    ↕
security (JwtAuthenticationFilter → JwtService → UserDetailsServiceImpl)
```

Herança `SINGLE_TABLE` — tabela `users` com coluna discriminadora `user_type` (`RESTAURANT_OWNER` | `CLIENT`).

## Endpoints

### Públicos (sem autenticação)
| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/v1/users` | Cadastrar usuário |
| `POST` | `/api/v1/auth/login` | Autenticar e obter token JWT |

### Protegidos (requerem `Authorization: Bearer <token>`)
| Método | Rota | Descrição |
|--------|------|-----------|
| `GET` | `/api/v1/users/{id}` | Buscar por ID |
| `GET` | `/api/v1/users?name=xxx` | Buscar por nome |
| `PUT` | `/api/v1/users/{id}` | Atualizar dados (exceto senha) |
| `PATCH` | `/api/v1/users/{id}/password` | Trocar senha (endpoint exclusivo) |
| `DELETE` | `/api/v1/users/{id}` | Excluir usuário |

## Executando com Docker Compose

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd restaurant-management

# 2. (Opcional) Copie e ajuste as variáveis de ambiente
cp .env.example .env

# 3. Suba a aplicação e o banco com um único comando
docker compose up --build
```

A API estará disponível em `http://localhost:8080`.

**Variáveis de ambiente disponíveis (com defaults):**

| Variável | Default | Descrição |
|----------|---------|-----------|
| `DB_HOST` | `localhost` | Host do banco de dados |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `restaurant_db` | Nome do banco |
| `DB_USER` | `postgres` | Usuário do banco |
| `DB_PASSWORD` | `postgres` | Senha do banco |
| `JWT_SECRET` | *(valor padrão de dev)* | Chave HMAC-SHA para assinar o JWT |
| `JWT_EXPIRATION` | `86400000` | Expiração do token em ms (padrão: 24h) |

## Executando localmente (sem Docker)

Requisito: PostgreSQL rodando localmente com as configurações padrão.

```bash
./mvnw spring-boot:run
```

## Testes

```bash
./mvnw test
```

15 testes unitários cobrindo `UserService` e `AuthService` com JUnit 5 + Mockito.

## Documentação Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

> Clique em **Authorize** e informe `Bearer <token>` para testar os endpoints protegidos.

## Coleção Postman

Importe o arquivo `postman/restaurant-management.postman_collection.json` no Postman.

Execute as requisições **na ordem das pastas** (1 → 6):

| Pasta | Conteúdo |
|-------|----------|
| 1. Cadastro | Cadastro válido (CLIENT e RESTAURANT_OWNER), e-mail duplicado (409), campos faltando (422) |
| 2. Autenticação | Login válido → salva `{{token}}`, login inexistente (401), senha incorreta (401) |
| 3. Busca | Por ID (sucesso e 404), por nome |
| 4. Atualização | Dados atualizados (sucesso), campos inválidos (422) |
| 5. Troca de Senha | Senha incorreta (422), confirmação divergente (422), sucesso (204) |
| 6. Exclusão | Sucesso (204), não encontrado (404) |

## Padrão de Erros

Todos os erros seguem **RFC 7807 (ProblemDetail)**:

```json
{
  "type": "https://api.restaurantmanagement.com/errors/user-not-found",
  "title": "Usuário não encontrado",
  "status": 404,
  "detail": "Usuário não encontrado com id: ...",
  "timestamp": "2026-04-19T18:00:00Z"
}
```
