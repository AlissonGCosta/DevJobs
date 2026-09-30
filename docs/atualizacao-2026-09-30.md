# DevJobs: entregas de 30/09/2026

Registro das alterações que estavam pendentes na branch `master`, a partir do commit `734d7b1b996360eb69dd9e16625c5409ece673af`. Data considerada no fuso `America/Sao_Paulo`.

Nesta organização, o código Java recebido foi preservado, as alterações foram commitadas individualmente e a documentação foi atualizada. A verificação encontrou um erro de compilação já presente nas alterações, detalhado abaixo.

## O que mudou

| Área | Alterações desta etapa |
| --- | --- |
| Domínio | Construtor de `Users` com nome e e-mail para transportar dados de atualização. |
| Organização | Quatro implementações migradas de `usersusecase` para `usersusecaseimpl`; `IdAvailableException` renomeada para `IdFoundAvailableException`. |
| Listagem | Contratos `FindAllUsersGateway` e `FindAllUsersUseCase`, implementação `FindAllUsersCaseImpl`, serviço JPA e conversão para DTO em `FindAllConfig`. |
| Atualização | Contratos de gateway e caso de uso, carregamento do usuário, verificação de e-mail, atualização de nome/e-mail/data e persistência transacional. |
| API | `GET /v1/users` e `PUT /v1/users/{id}`, DTOs próprios para atualização e validação dos corpos de cadastro e atualização. |
| Senhas | Bean `BCryptPasswordEncoder` e geração de hashes de senha e confirmação no mapeamento de cadastro para entidade. |
| Erros | Resposta estruturada, record para detalhes por campo e handler HTTP 404 para usuário não encontrado; novos códigos no enum. |
| Spring | Beans dos novos casos de uso e `UserMapper` registrado como componente; regra explícita para permitir PUT. |
| Testes | Ajuste das chamadas ao construtor de cadastro nos seis testes de `UsersTest`. |
| Documentação | README atualizado com o estado atual, links de implementações corrigidos no registro anterior e este relatório. |

O cadastro, a consulta por ID, a entidade JPA, o repository e a configuração do H2 já existiam na revisão anterior. Nesta etapa eles receberam as adaptações indicadas acima.

## Fluxos presentes no código

A listagem consulta todos os registros pelo repository, transforma as entidades em objetos de domínio e devolve DTOs com ID, nome, e-mail, data de criação e perfil. Não há paginação.

A atualização recebe `fullName` e `email`, busca o usuário pelo ID, verifica se o e-mail está cadastrado e altera nome, e-mail e `updatedAt`. O serviço transacional carrega a entidade existente e salva esses campos, preservando os demais dados. O retorno contém nome, e-mail e data de atualização.

Os DTOs de cadastro e atualização usam `@NotNull`, e o controller aplica `@Valid`. O nome é validado no domínio entre 6 e 100 caracteres, a senha entre 8 e 64 e a confirmação deve coincidir. O construtor usado como entrada da atualização apenas transporta nome e e-mail; a validação do nome acontece no setter do usuário carregado.

`GlobalExceptionHandler` trata `IdFoundAvailableException` com HTTP 404 e corpo com `timestamp`, `status`, `erro`, `message`, `path` e `errors`. O campo `errors` fica nulo nesse tratamento; o record de detalhes por campo ainda não é utilizado pelo handler.

## Verificação realizada

Comando executado na raiz do projeto:

```powershell
.\mvnw.cmd test -B -ntp
```

Ambiente: Java 25.0.2, Maven Wrapper 3.9.16 e compilação com `release 21`.

Resultado: **BUILD FAILURE na compilação; nenhum teste executado nesta tentativa**.

A chamada em [UsersUseCaseConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/UsersUseCaseConfig.java), linha 46, passa:

```text
PutUsersGateway, IdAvailableUseCase, EmailAvailableUseCase, FindByIdUsersUseCase
```

O construtor de [PutUsersUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PutUsersUseCaseImpl.java) aceita:

```text
PutUsersGateway, EmailAvailableUseCase, FindByIdUsersUseCase
```

É necessário alinhar a configuração ao construtor antes de executar a suíte ou iniciar a aplicação. Esta entrega registra o estado recebido; não inclui correção dessa incompatibilidade.

Existem seis testes de domínio e um de contexto definidos no código. Os ajustes em `UsersTest` foram commitados, mas a falha de compilação impede confirmar o resultado da suíte nesta revisão.

A conferência dos commits verificou as mensagens Conventional Commits e o isolamento por arquivo, considerando cada renomeação como um único arquivo. Os hashes de conteúdo dos 30 arquivos Java foram comparados antes e depois da criação dos commits, sem alterações adicionais.

## Limitações observadas

- O PUT rejeita também o e-mail que já pertence ao próprio usuário, porque a consulta atual verifica apenas sua existência.
- A listagem não possui paginação, e não há testes específicos dos novos casos de uso, endpoints ou persistência.
- O handler próprio trata apenas usuário não encontrado; a padronização das demais exceções e dos erros de validação continua pendente.
- Senha e confirmação recebem hashes separados e ambas são persistidas. Não há fluxo próprio de autenticação nem JWT.
- A configuração de segurança permanece permissiva, com CSRF desabilitado. A inclusão explícita do PUT não muda a regra anterior de permitir todas as requisições.
- O H2 permanece em memória, com Hibernate gerenciando o esquema e Flyway desabilitado.

Essas observações sobre os fluxos vêm da leitura do código; os endpoints não foram exercitados nesta entrega devido à falha de compilação.

## Commits por arquivo

Foram organizados **33 commits para 33 arquivos lógicos**: 30 arquivos Java e três arquivos de documentação. As cinco renomeações incluem o caminho antigo e o novo do mesmo arquivo em seu commit dedicado. O Git pode apresentar a renomeação da exceção como exclusão e criação na detecção padrão; ela continua isolada dos outros arquivos.

| Commit | Arquivo atual | Mensagem |
| --- | --- | --- |
| `6d4b9eb` | [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java) | `feat(user): add constructor for profile updates` |
| `efa8974` | [IdFoundAvailableException.java](../src/main/java/br/costa/DevJobs/core/exception/IdFoundAvailableException.java) | `refactor(errors): rename missing user id exception` |
| `b64527c` | [ErrorCodeEnum.java](../src/main/java/br/costa/DevJobs/core/exception/enums/ErrorCodeEnum.java) | `feat(errors): add id conflict and missing email codes` |
| `33a435a` | [FindAllUsersGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/FindAllUsersGateway.java) | `feat(user): add user listing gateway contract` |
| `02c3a10` | [PutUsersGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PutUsersGateway.java) | `feat(user): add user update gateway contract` |
| `aed3a9f` | [FindAllUsersUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/FindAllUsersUseCase.java) | `feat(user): add user listing use case contract` |
| `0a2609b` | [PutUsersUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/PutUsersUseCase.java) | `feat(user): add user update use case contract` |
| `b5b7586` | [EmailAvailableUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/EmailAvailableUseCaseImpl.java) | `refactor(user): relocate email availability implementation` |
| `78d2573` | [IdAvaliableUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/IdAvaliableUseCaseImpl.java) | `refactor(user): relocate id availability implementation` |
| `55f7534` | [RegisterAccountUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/RegisterAccountUseCaseImpl.java) | `refactor(user): relocate account registration implementation` |
| `5f22219` | [FindByIdUsersUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/FindByIdUsersUseCaseImpl.java) | `refactor(user): relocate lookup and use renamed id exception` |
| `2669b53` | [FindAllUsersCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/FindAllUsersCaseImpl.java) | `feat(user): implement user listing use case` |
| `26cc500` | [PutUsersUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PutUsersUseCaseImpl.java) | `feat(user): implement profile update flow` |
| `43765fe` | [PasswordConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/PasswordConfig.java) | `feat(security): configure BCrypt password encoder` |
| `3409254` | [UsersRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/UsersRequestDto.java) | `feat(validation): require non-null registration fields` |
| `867bd3e` | [PutUserRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/PutUserRequestDto.java) | `feat(user): add profile update request DTO` |
| `13c4a2d` | [PutUserResponseDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/response/PutUserResponseDto.java) | `feat(user): add profile update response DTO` |
| `3eec380` | [UserMapper.java](../src/main/java/br/costa/DevJobs/infrastructure/mappers/UserMapper.java) | `feat(user): hash registration passwords and map profile updates` |
| `7c1dfa1` | [FindByIdUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/FindByIdUsersService.java) | `fix(user): throw domain exception for missing user` |
| `c3c5e2d` | [IdAvailableService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/IdAvailableService.java) | `style(user): adjust id availability method spacing` |
| `e95aaab` | [FindAllUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/FindAllUsersService.java) | `feat(user): list users through repository gateway` |
| `7f840a4` | [PutUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/PutUsersService.java) | `feat(user): persist profile updates transactionally` |
| `02bd81f` | [FindAllConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/FindAllConfig.java) | `feat(user): map user listings to response DTOs` |
| `4ce8962` | [UsersUseCaseConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/UsersUseCaseConfig.java) | `feat(user): wire listing and update use cases` |
| `cb3e9b7` | [Error.java](../src/main/java/br/costa/DevJobs/infrastructure/exception/Error.java) | `feat(errors): add field error detail record` |
| `dd865be` | [ErrorResponse.java](../src/main/java/br/costa/DevJobs/infrastructure/exception/ErrorResponse.java) | `feat(errors): add structured HTTP error response` |
| `e9bc95f` | [GlobalExceptionHandler.java](../src/main/java/br/costa/DevJobs/infrastructure/exception/GlobalExceptionHandler.java) | `feat(errors): return HTTP 404 for missing users` |
| `40b9ac3` | [SecurityConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/SecurityConfig.java) | `chore(security): explicitly permit PUT requests` |
| `936abd5` | [UsersController.java](../src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java) | `feat(user): expose listing and profile update endpoints` |
| `1fba170` | [UsersTest.java](../src/test/java/br/costa/DevJobs/core/domain/users/UsersTest.java) | `test(user): align validation cases with registration constructor` |
| `016f883` | [atualizacao-2026-09-18.md](../docs/atualizacao-2026-09-18.md) | `docs(history): refresh use case links and reference latest delivery` |
| `1e711e9` | [README.md](../README.md) | `docs(project): document user API and current build status` |

Este relatório recebe o 33º commit, com a mensagem `docs(project): record September 30 changes and validation`. Seu próprio hash não é incluído para evitar uma referência circular.

Os commits foram criados localmente na branch `master`, sem push. Como solicitado, mudanças que dependem umas das outras ficaram em commits individuais; não há garantia de compilação de cada revisão intermediária.

Para conferir o histórico desta entrega:

```powershell
git log --reverse --oneline 734d7b1b996360eb69dd9e16625c5409ece673af..HEAD
git diff --stat 734d7b1b996360eb69dd9e16625c5409ece673af..HEAD
git status --short
```

A visão acumulada está no [README](../README.md). O [registro de 18/09/2026](atualizacao-2026-09-18.md) preserva os resultados e as limitações daquela revisão.
