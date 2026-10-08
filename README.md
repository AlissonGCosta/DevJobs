# DevJobs

Projeto de back-end para organizar vagas, candidaturas e entrevistas de emprego, desenvolvido com Java e Spring Boot.

**Estado documentado em 08/10/2026:** o código inclui cadastro, consulta por ID, listagem, atualização de perfil, troca de senha e exclusão de usuários. Esta etapa reorganiza os pacotes de usuários, amplia o tratamento HTTP das exceções e passa a consultar e atualizar a senha diretamente na entidade persistida. A compilação passou; a suíte executou sete testes, com uma falha por divergência no tipo de exceção esperado. O PATCH ainda tem pendências de validação e tratamento de falhas, descritas abaixo.

O [planejamento original](DevJobs-Projeto.md) descreve o produto desejado. Os registros de [18/09/2026](docs/atualizacao-2026-09-18.md), [30/09/2026](docs/atualizacao-2026-09-30.md), [04/10/2026](docs/atualizacao-2026-10-04.md) e [08/10/2026](docs/atualizacao-2026-10-08.md) documentam as respectivas etapas.

## Tecnologias presentes

| Tecnologia | Uso no código atual |
| --- | --- |
| Java 21 | Versão de compilação configurada no `pom.xml`. |
| Spring Boot 4.1.0 | Inicialização e configuração da aplicação. |
| Maven Wrapper 3.9.16 | Build, testes e execução. |
| Spring Web MVC | Controller REST de usuários. |
| Spring Data JPA | `UsersEntity`, `UsersRepository` e serviços de persistência. |
| Spring Security | Configuração permissiva de desenvolvimento e `BCryptPasswordEncoder`. |
| Bean Validation | `@Valid` nas entradas do controller e `@NotNull` nos DTOs de cadastro, atualização de perfil e troca de senha. |
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
│       ├── enums/
│       └── usersexception/
├── usecase/userusecase/
├── application/
│   ├── gateway/usersgateway/
│   └── usecaseimpl/usersusecaseimpl/
└── infrastructure/
    ├── Entity/
    ├── config/
    ├── controller/
    ├── dto/
    │   ├── request/userrequest/
    │   └── response/userresponse/
    ├── exception/
    ├── mappers/
    ├── persistence/
    └── service/userservice/
```

O domínio usa classes Java sem anotações JPA. Os casos de uso recebem gateways pelo construtor; os serviços de infraestrutura implementam esses contratos com o repository. `UsersUseCaseConfig` compõe os casos de uso, e `UserMapper` é um componente Spring responsável pelas conversões de DTOs e domínio. `CreateUserEntityService` cria a entidade de cadastro e gera os hashes BCrypt. `FindAllService`, antes chamado `FindAllConfig`, converte a listagem para DTOs no pacote de serviços de usuários.

Os modelos de vaga (`JobVacancy`), candidatura (`JobApplication`), entrevista (`Interview`) e histórico (`HistoryJobApplication`) também estão presentes. Seus fluxos completos de API e persistência continuam pendentes.

## API de usuários

As rotas abaixo estão declaradas em [UsersController](src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java), com base `/v1/users`. Os status representam o contrato declarado; os endpoints não foram exercitados via HTTP nesta verificação.

| Método e rota | Entrada | Resposta de sucesso declarada |
| --- | --- | --- |
| `POST /v1/users` | `fullName`, `email`, `password`, `confirmPassword` | `201`: nome e e-mail. |
| `GET /v1/users/{id}` | ID no caminho | `200`: ID, nome, e-mail, criação e perfil. |
| `GET /v1/users` | Sem corpo | `200`: lista com os mesmos campos da consulta por ID. |
| `PUT /v1/users/{id}` | ID no caminho; `fullName` e `email` no corpo | `200`: nome, e-mail e `updatedAt`. |
| `PATCH /v1/users/{id}` | ID no caminho; `oldPassword`, `newPassword` e `confirmNewPassword` no corpo | `200`, sem corpo; implementação com pendências descritas em “Troca de senha”. |
| `DELETE /v1/users/{id}` | ID no caminho, sem corpo | `204`, sem corpo. |

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

O cadastro verifica a existência do e-mail antes de persistir. Embora o método se chame `emailAvailable`, o serviço usa `existsByEmail`: `true` indica e-mail já cadastrado. `UserRegisterService` delega a criação da entidade a `CreateUserEntityService`, que gera hashes BCrypt separados para senha e confirmação; esses campos não aparecem nos DTOs de resposta.

A consulta por ID verifica a existência do usuário. A listagem usa `findAll` do repository e converte os resultados para DTOs por meio de `FindAllService`, sem paginação.

A atualização carrega o usuário, verifica o e-mail, altera nome, e-mail e data de atualização e salva a entidade em um serviço transacional. No código atual, enviar o próprio e-mail já cadastrado também dispara `EmailAvaliableException`; ainda não há uma exceção à verificação de duplicidade para o titular.

A exclusão passa por `DeleteUserUseCaseImpl`, que verifica a existência do ID e lança `IdFoundAvailableException` quando ele não existe. `DeleteUserService` chama `UsersRepository.deleteById` em um método transacional. O controller declara HTTP `204` no sucesso; a exceção de ID ausente é tratada como `404`.

### Troca de senha

Exemplo do corpo aceito por `PATCH /v1/users/{id}`:

```json
{
  "oldPassword": "senha-exemplo-123",
  "newPassword": "nova-senha-456",
  "confirmNewPassword": "nova-senha-456"
}
```

`PatchUserPassRequestDto` exige os três campos não nulos. O controller agora passa as três strings diretamente para `PatchPassUseCase`, sem chamar `UserMapper.patchPassRequestToUsers`. Esse método do mapper e o construtor de domínio de troca de senha continuam presentes, mas suas validações não são executadas no caminho atual do endpoint.

`PatchPassUseCaseImpl` verifica a existência do ID, chama `PassAvailableUseCase.availablePass(oldPassword, id)` e delega as novas senhas a `PatchPassUsersGateway`. A conferência retorna `void` e sinaliza falhas por exceção. `PassAvailableService` consulta `UsersRepository.findById` e compara a senha antiga com o hash da entidade via BCrypt; uma divergência lança `PassAvailableException`, tratada como HTTP `409`, interrompendo o fluxo. `PatchPasswordUserService` carrega a entidade existente, substitui os hashes de senha e confirmação e chama `save`. Os casos de uso são registrados em `UsersUseCaseConfig`.

**Pendências identificadas pela leitura do código:**

- O caminho atual não verifica o tamanho da nova senha nem a igualdade entre `newPassword` e `confirmNewPassword`: ambos os valores são codificados e atribuídos diretamente à entidade. A Bean Validation exige somente que não sejam nulos.
- `PatchPasswordUserService` ainda captura qualquer exceção e retorna `false`; o caso de uso ignora esse retorno. O controller pode terminar com HTTP `200` mesmo sem ter alterado a senha.
- O serviço de troca de senha não atualiza `updatedAt` nem declara uma transação que englobe a consulta e a gravação.
- Não há testes específicos do fluxo de senha, dos endpoints ou da exclusão de usuários.

O acesso ao hash armazenado, a interrupção após senha antiga incorreta e o uso da entidade existente foram ajustados nesta etapa. As pendências acima foram identificadas por leitura do código; os endpoints não foram exercitados via HTTP nesta entrega.

## Validações e erros

O construtor de cadastro é `Users(String fullName, String email, String password, String confirmPassword)`.

| Campo | Regra de domínio | Exceção e código |
| --- | --- | --- |
| Nome completo | Entre 6 e 100 caracteres. | `ValidateFullNameException`, `VPN0001`. |
| Senha | Entre 8 e 64 caracteres. | `InvalidPasswordException`, `IPN0001`. |
| Confirmação | Igual à senha. | `PassAvailableException`, `IPN0002`. |

Os DTOs exigem campos não nulos, mas não usam `@NotBlank` ou validação de formato de e-mail. Não há normalização de espaços. O construtor de dois argumentos usado para transportar a atualização apenas atribui nome e e-mail; o nome é validado posteriormente pelo setter do usuário carregado. O construtor de troca de senha reaplica as regras de senha e confirmação, sem validar por conta própria a senha antiga, mas não é chamado pelo PATCH atual.

`GlobalExceptionHandler` responde com `timestamp`, `status`, `erro`, `message`, `path` e `errors`. Os tratamentos próprios mantêm `errors` nulo; o record `Error` prepara a estrutura para detalhes por campo, mas ainda não é preenchido por esse handler. O código da exceção também não aparece no corpo dessa resposta.

| Exceção tratada | HTTP |
| --- | --- |
| `IdFoundAvailableException` | `404` |
| `PassAvailableException` | `409` |
| `EmailAvaliableException` | `409` |
| `InternalServerErrorException` | `500` |
| `InvalidPasswordException` | `400` |
| `ValidateFullNameException` | `400` |

A confirmação divergente no cadastro agora lança `PassAvailableException` e é tratada como `409`. As respostas de Bean Validation ainda não são padronizadas pelo handler próprio.

| Constante | Código definido no enum | Uso |
| --- | --- | --- |
| `IAI0001` | `IAI0001` | Usuário não encontrado. |
| `IAI0002` | `IAI0002` | ID já existente; definido, sem uso no fluxo atual. |
| `EAA0001` | `CML0001` | E-mail já cadastrado. |
| `EAA0002` | `CML0002` | E-mail não encontrado; definido, sem uso no fluxo atual. |
| `ISE0001` | `ISE0001` | Falha sinalizada pelo gateway de cadastro. |
| `PSE0001` | `PSS0001` | Senha antiga divergente; usado em `PassAvailableException`, propagada pelo serviço e tratada como HTTP `409`. |
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

A API usa a porta padrão `8080`, sem porta personalizada no projeto. A configuração local usa `jdbc:h2:mem:devjobs;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`, usuário `sa` e senha vazia. O console H2 fica em `/h2-console`, e o Hibernate gerencia as tabelas com `ddl-auto: update`. Os dados são temporários e não sobrevivem ao encerramento do processo.

A configuração de segurança permite todas as requisições e desabilita CSRF. Há regras explícitas para `POST /v1/**`, `GET`, `PUT`, `PATCH` e agora `DELETE`, além de `anyRequest().permitAll()`. A inclusão de `DELETE` mantém o comportamento já permissivo. Autenticação própria, autorização por usuário e JWT permanecem pendentes.

## Verificação

Em **08/10/2026**, foi executado `.\mvnw.cmd test -B -ntp` com Java **25.0.2**, compilação configurada para Java 21 e Maven Wrapper **3.9.16**.

Resultado: **BUILD FAILURE na etapa de testes**, com **7 testes executados, 1 falha, 0 erros e 0 ignorados**. A compilação principal e de testes passou, e `DevJobsApplicationTests.contextLoads` passou. O teste `UsersTest.shouldRejectdiferentPass` espera `InvalidPasswordException`, mas `Users.validateEqualsPassword` agora lança `PassAvailableException`. Esta entrega preserva o código Java recebido e registra essa divergência.

A suíte contém seis casos em `UsersTest` (senha curta, senha longa, nome curto, nome longo, confirmação diferente e cadastro válido) e `DevJobsApplicationTests.contextLoads`. Não há testes específicos dos casos de uso, endpoints, persistência do PATCH ou exclusão. A inicialização do contexto confirma a composição Spring atual, sem validar os contratos HTTP ou os efeitos desses endpoints no banco.

Os resultados históricos de 18/09, 30/09 e 04/10 estão preservados nos respectivos registros. As limitações descritas neste README foram identificadas por inspeção do código, sem execução dos endpoints nesta entrega.

## Próximas etapas

- Alinhar o teste de confirmação divergente ao contrato de exceção pretendido pelo domínio.
- Reaplicar no PATCH as validações de tamanho e confirmação da nova senha, propagar falhas de gravação e atualizar `updatedAt`.
- Cobrir a troca de senha com testes de senha antiga incorreta, confirmação divergente, persistência bem-sucedida e falha de gravação.
- Permitir atualização mantendo o e-mail do próprio usuário.
- Completar testes de casos de uso, controller, persistência, exclusão e respostas de erro.
- Padronizar as respostas de Bean Validation e ampliar as validações dos dados de entrada.
- Implementar autenticação e autorização; revisar a persistência da confirmação de senha.
- Configurar PostgreSQL e migrações, adicionar paginação e desenvolver os fluxos de vagas, candidaturas e entrevistas.

## Convenção dos commits

As mensagens seguem `tipo(escopo): descrição`, com um commit dedicado por arquivo alterado ou criado. Uma renomeação reúne o caminho antigo e o novo do mesmo arquivo em um único commit. A relação de arquivos e commits desta entrega está no [registro de 08/10/2026](docs/atualizacao-2026-10-08.md).
