# DevJobs

Projeto de back-end para organizar vagas, candidaturas e entrevistas de emprego, desenvolvido com Java e Spring Boot.

**Estado documentado em 30/09/2026:** o código inclui cadastro, consulta por ID, listagem e atualização de usuários, persistência JPA, hash BCrypt e tratamento de usuário não encontrado. A verificação desta entrega encontrou um erro de compilação na configuração do caso de uso de atualização; os testes não chegaram a executar.

O [planejamento original](DevJobs-Projeto.md) descreve o produto desejado. Os registros de [18/09/2026](docs/atualizacao-2026-09-18.md) e [30/09/2026](docs/atualizacao-2026-09-30.md) documentam as respectivas etapas.

## Tecnologias presentes

| Tecnologia | Uso no código atual |
| --- | --- |
| Java 21 | Versão de compilação configurada no `pom.xml`. |
| Spring Boot 4.1.0 | Inicialização e configuração da aplicação. |
| Maven Wrapper 3.9.16 | Build, testes e execução. |
| Spring Web MVC | Controller REST de usuários. |
| Spring Data JPA | `UsersEntity`, `UsersRepository` e serviços de persistência. |
| Spring Security | Configuração permissiva de desenvolvimento e `BCryptPasswordEncoder`. |
| Bean Validation | `@Valid` nas entradas do controller e `@NotNull` nos DTOs de cadastro e atualização. |
| H2 e PostgreSQL | H2 em memória configurado; driver PostgreSQL disponível, sem conexão configurada. |
| Flyway | Dependência presente, desabilitada na configuração local; sem migrações do projeto. |
| Lombok | Construtores, getters e setters da infraestrutura. |
| JUnit Jupiter | Seis testes de domínio e um teste de contexto definidos. |

## Organização do código

```text
src/main/java/br/costa/DevJobs/
├── DevJobsApplication.java
├── core/
│   ├── domain/
│   │   └── enumerated/
│   └── exception/
│       └── enums/
├── usecase/userusecase/
├── application/
│   ├── gateway/usersgateway/
│   └── usecaseimpl/usersusecaseimpl/
└── infrastructure/
    ├── Entity/
    ├── config/
    ├── controller/
    ├── dto/
    │   ├── request/
    │   └── response/
    ├── exception/
    ├── mappers/
    ├── persistence/
    └── service/
```

O domínio usa classes Java sem anotações JPA. Os casos de uso recebem gateways pelo construtor; os serviços de infraestrutura implementam esses contratos com o repository. `UsersUseCaseConfig` compõe os casos de uso, e `UserMapper` é um componente Spring responsável pelas conversões.

Os modelos de vaga (`JobVacancy`), candidatura (`JobApplication`), entrevista (`Interview`) e histórico (`HistoryJobApplication`) também estão presentes. Seus fluxos completos de API e persistência continuam pendentes.

## API de usuários

As rotas abaixo estão declaradas em `UsersController`, com base `/v1/users`. Sua execução nesta revisão depende da correção do erro de compilação descrito em “Verificação”.

| Método e rota | Entrada | Resposta de sucesso declarada |
| --- | --- | --- |
| `POST /v1/users` | `fullName`, `email`, `password`, `confirmPassword` | `201`: nome e e-mail. |
| `GET /v1/users/{id}` | ID no caminho | `200`: ID, nome, e-mail, criação e perfil. |
| `GET /v1/users` | Sem corpo | `200`: lista com os mesmos campos da consulta por ID. |
| `PUT /v1/users/{id}` | ID no caminho; `fullName` e `email` no corpo | `200`: nome, e-mail e `updatedAt`. |

Exemplo de corpo de cadastro:

```json
{
  "fullName": "Pessoa Exemplo",
  "email": "pessoa@example.com",
  "password": "senha-exemplo-123",
  "confirmPassword": "senha-exemplo-123"
}
```

Exemplo de corpo de atualização:

```json
{
  "fullName": "Pessoa Atualizada",
  "email": "pessoa.atualizada@example.com"
}
```

O cadastro verifica a existência do e-mail antes de persistir. Embora o método se chame `emailAvailable`, o serviço usa `existsByEmail`: `true` indica e-mail já cadastrado. `UserMapper` gera hashes BCrypt para senha e confirmação ao criar a entidade; esses campos não aparecem nos DTOs de resposta.

A consulta por ID verifica a existência do usuário. A listagem usa `findAll` do repository e converte os resultados para DTOs por meio de `FindAllConfig`, sem paginação.

A atualização carrega o usuário, verifica o e-mail, altera nome, e-mail e data de atualização e salva a entidade em um serviço transacional. No código atual, enviar o próprio e-mail já cadastrado também dispara `EmailAvaliableException`; ainda não há uma exceção à verificação de duplicidade para o titular.

## Validações e erros

O construtor de cadastro é `Users(String fullName, String email, String password, String confirmPassword)`.

| Campo | Regra de domínio | Exceção e código |
| --- | --- | --- |
| Nome completo | Entre 6 e 100 caracteres. | `ValidateFullNameException`, `VPN0001`. |
| Senha | Entre 8 e 64 caracteres. | `InvalidPasswordException`, `IPN0001`. |
| Confirmação | Igual à senha. | `InvalidPasswordException`, `IPN0002`. |

Os DTOs exigem campos não nulos. Não há validação de formato de e-mail nem normalização de espaços. O construtor de dois argumentos usado para transportar a atualização apenas atribui nome e e-mail; o nome é validado posteriormente pelo setter do usuário carregado.

`GlobalExceptionHandler` traduz `IdFoundAvailableException` em HTTP `404`, com `timestamp`, `status`, `erro`, `message`, `path` e `errors`. No tratamento atual, `errors` recebe `null`; o record `Error` prepara a estrutura para detalhes por campo, mas ainda não é preenchido por esse handler.

O handler próprio ainda não padroniza as demais exceções de domínio nem as respostas de Bean Validation.

| Constante | Código retornado | Uso |
| --- | --- | --- |
| `IAI0001` | `IAI0001` | Usuário não encontrado. |
| `IAI0002` | `IAI0002` | ID já existente; definido, sem uso no fluxo atual. |
| `EAA0001` | `CML0001` | E-mail já cadastrado. |
| `EAA0002` | `CML0002` | E-mail não encontrado; definido, sem uso no fluxo atual. |
| `ISE0001` | `ISE0001` | Falha sinalizada pelo gateway de cadastro. |
| `PIN0002` | `PIN-0002` | Mensagem de PIN com tentativas; fluxo de autenticação pendente. |

## Execução local

Use JDK 21 e `JAVA_HOME` configurado. O wrapper baixa o Maven e as dependências quando necessário.

No Windows:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw test
./mvnw spring-boot:run
```

Após resolver a compilação, a configuração local usa `jdbc:h2:mem:devjobs;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`, usuário `sa` e senha vazia. O console H2 fica em `/h2-console`, e o Hibernate gerencia as tabelas com `ddl-auto: update`. Os dados são temporários e não sobrevivem ao encerramento do processo.

A configuração de segurança permite todas as requisições e desabilita CSRF. A regra explícita para `PUT` adicionada nesta etapa mantém esse comportamento já permissivo. Autenticação própria, autorização por usuário e JWT permanecem pendentes.

## Verificação

Em **30/09/2026**, foi executado `.\mvnw.cmd test -B -ntp` com Java **25.0.2**, compilação configurada para Java 21 e Maven Wrapper **3.9.16**.

Resultado: **BUILD FAILURE na compilação**, antes da execução dos testes. Em `UsersUseCaseConfig.java:46`, a criação de `PutUsersUseCaseImpl` fornece quatro argumentos, incluindo `IdAvailableUseCase`, mas o construtor recebe apenas `PutUsersGateway`, `EmailAvailableUseCase` e `FindByIdUsersUseCase`. Essa incompatibilidade já estava nas alterações recebidas e foi mantida nesta entrega de documentação e commits.

A suíte definida contém seis casos em `UsersTest` (senha curta, senha longa, nome curto, nome longo, confirmação diferente e cadastro válido) e `DevJobsApplicationTests.contextLoads`. Não há resultado aprovado da suíte para esta revisão nem validação HTTP dos novos endpoints.

O resultado histórico de dois testes aprovados em 18/09/2026 pertence àquela revisão e está preservado no registro da data.

## Próximas etapas

- Alinhar a configuração de `PutUsersUseCaseImpl` ao seu construtor e executar a suíte.
- Permitir atualização mantendo o e-mail do próprio usuário.
- Completar testes de casos de uso, controller, persistência e respostas de erro.
- Expandir o tratamento HTTP das exceções e as validações dos dados de entrada.
- Implementar autenticação e autorização; revisar a persistência da confirmação de senha.
- Configurar PostgreSQL e migrações, adicionar paginação e desenvolver os fluxos de vagas, candidaturas e entrevistas.

## Convenção dos commits

As mensagens seguem `tipo(escopo): descrição`, com um commit dedicado por arquivo alterado ou criado. Uma renomeação reúne o caminho antigo e o novo do mesmo arquivo em um único commit. A relação de arquivos e commits desta entrega está no [registro de 30/09/2026](docs/atualizacao-2026-09-30.md).
