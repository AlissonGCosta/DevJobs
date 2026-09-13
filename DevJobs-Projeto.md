# DevJobs — Organizador de candidaturas

Uma aplicação para registrar e acompanhar vagas de emprego:

- Empresa e nome da vaga.
- Link da candidatura.
- Modelo presencial, híbrido ou remoto.
- Status: salva, candidatura enviada, entrevista, aprovada ou recusada.
- Data da candidatura.
- Anotações.
- Filtros por empresa, status e tecnologia.
- Dashboard com estatísticas.

É uma boa escolha porque resolve um problema real e permite demonstrar muito mais do que um CRUD básico.

## Tecnologias

### Back-end

- Java 21.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Spring Security com JWT.
- Bean Validation.
- PostgreSQL.
- Flyway.
- Swagger/OpenAPI.
- JUnit e Mockito.
- Docker.

### Front-end

Para começar de maneira simples:

- Thymeleaf.
- Bootstrap.

Depois, o back-end poderá ser transformado em uma API consumida por React ou Angular.

## Estrutura principal

```text
Usuário
├── cria uma conta
├── realiza login
└── gerencia suas candidaturas
    ├── cadastra
    ├── edita
    ├── altera o status
    ├── adiciona observações
    └── acompanha estatísticas
```

## Endpoints interessantes

```text
POST   /auth/register
POST   /auth/login

POST   /applications
GET    /applications
GET    /applications/{id}
PUT    /applications/{id}
PATCH  /applications/{id}/status
DELETE /applications/{id}

GET    /applications?status=INTERVIEW
GET    /dashboard/statistics
```

## Diferenciais para chamar atenção

Depois do MVP funcionando, você pode adicionar:

- Histórico das mudanças de status.
- Lembrete de entrevistas.
- Exportação das candidaturas em CSV ou PDF.
- Gráficos de candidaturas por mês.
- Taxa de retorno das empresas.
- Modo escuro.
- Deploy público.
- Pipeline de testes com GitHub Actions.

## Ordem de desenvolvimento

1. Criar o projeto Spring Boot.
2. Configurar PostgreSQL e Flyway.
3. Criar as entidades `User`, `Application` e `StatusHistory`.
4. Implementar o CRUD de candidaturas.
5. Adicionar validações e tratamento de erros.
6. Implementar autenticação com JWT.
7. Documentar a API com Swagger.
8. Criar testes.
9. Montar uma interface simples.
10. Fazer deploy e publicar no LinkedIn.

Esse projeto combina com o momento de quem procura uma oportunidade: além de enriquecer o portfólio, pode ser usado para organizar as próprias candidaturas de estágio.
