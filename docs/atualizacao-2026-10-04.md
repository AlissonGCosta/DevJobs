# DevJobs: entregas de 04/10/2026

Registro das alterações pendentes na branch `master`, a partir do commit `da92617604ff63b06993d3875a1f2fb1f35e896d`. Data considerada no fuso `America/Sao_Paulo`.

Esta entrega documenta o estado atual do código e organiza as alterações em commits individuais por arquivo. Foram recebidos 16 arquivos Java com mudanças: seis modificados e dez novos. O código recebido foi preservado; as edições desta organização abrangem o README e este relatório.

## O que mudou

| Área | Alterações desta etapa |
| --- | --- |
| Domínio | Campo `oldPassword`, getter, setter e construtor de três argumentos em `Users`, com validação da nova senha e da confirmação. |
| Contratos | `PassAvailableGatway`, `PatchPassUsersGateway`, `PassAvailableUseCase` e `PatchPassUseCase`. |
| Casos de uso | `PassAvilableUseCaseImpl` delega a verificação da senha; `PatchPassUseCaseImpl` coordena busca, conferência e solicitação de gravação. |
| Entrada HTTP | `PatchUserPassRequestDto` recebe `oldPassword`, `newPassword` e `confirmNewPassword`, todos com `@NotNull`. |
| Mapeamento | `patchPassRequestToUsers` converte o DTO; três métodos usados por GET e PUT passam a iniciar com letra minúscula, com chamadas ajustadas no controller. |
| Serviços | `PassAvailableService` compara a senha via BCrypt; `PatchPasswordUserService` tenta persistir a troca com uma nova data de atualização. |
| Erros | `PassAvailableException` e constante `PSE0001`, cujo código é `PSS0001`. |
| Spring | Beans dos dois novos casos de uso em `UsersUseCaseConfig`; regra explícita para permitir PATCH em `SecurityConfig`. |
| API | `PATCH /v1/users/{id}`, declarado com HTTP 200 e corpo vazio; status 200 explicitado também no PUT. |
| Documentação | README atualizado com API, fluxo de senha, limitações e resultado atual dos testes; este registro da entrega. |

As grafias `PassAvailableGatway` e `PassAvilableUseCaseImpl` correspondem aos nomes presentes no código.

## Contrato e fluxo de troca de senha

Requisição declarada em [UsersController](../src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java):

```http
PATCH /v1/users/{id}
Content-Type: application/json
```

```json
{
  "oldPassword": "senha-exemplo-123",
  "newPassword": "nova-senha-456",
  "confirmNewPassword": "nova-senha-456"
}
```

O corpo passa pela Bean Validation e pelo mapper antes do caso de uso. O novo construtor de `Users` exige nova senha entre 8 e 64 caracteres e confirmação igual. A senha antiga é apenas atribuída ao domínio; sua conferência foi delegada ao serviço de infraestrutura. Não foi adicionada uma coluna para `oldPassword`.

A sequência implementada em [PatchPassUseCaseImpl](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PatchPassUseCaseImpl.java) é:

1. Carregar o usuário por ID.
2. Chamar `PassAvailableUseCase.availablePass(oldPassword, id)`.
3. Atribuir a nova senha e a confirmação ao usuário carregado.
4. Chamar `PatchPassUsersGateway.patchPass(user, id)`.

O controller retorna `void` e declara HTTP 200. A busca inicial pode lançar `IdFoundAvailableException`, tratada como HTTP 404 pelo handler existente. Não há tratamento HTTP específico para a nova exceção de senha.

## Limitações observadas no código

O fluxo de troca de senha ainda não está concluído. Os seguintes problemas foram identificados por inspeção estática; o endpoint não foi exercitado via HTTP nesta entrega.

| Ponto | Comportamento atual e consequência |
| --- | --- |
| Hash da senha ausente | `UserMapper.userEntityToDomain` preenche somente ID, nome, e-mail, criação e perfil. Assim, a comparação de BCrypt em `PassAvailableService` recebe uma senha armazenada nula no domínio. |
| Resultado da conferência ignorado | `PassAvailableService` captura qualquer exceção, inclusive `PassAvailableException`, e retorna `false`. O caso de uso ignora esse booleano, prosseguindo sem exigir uma verificação bem-sucedida. |
| Ordem dos setters | `PatchPasswordUserService` busca novamente o usuário e chama `setConfirmPassword` antes de `setPassword`. A confirmação é comparada à senha nula, gerando `InvalidPasswordException` antes da gravação. |
| Falha de gravação ocultada | O serviço captura a exceção e retorna `false`; esse resultado também é ignorado. O controller pode terminar com HTTP 200 mesmo sem atualização. |
| Entidade de gravação sem ID | O mapper utilizado na tentativa de gravação cria uma nova `UsersEntity` sem ID. Se a etapa anterior for corrigida, essa abordagem ainda tentará inserir um registro com e-mail já existente, em vez de atualizar a entidade original. |
| Segurança HTTP | `anyRequest().permitAll()` continua liberando as requisições, com CSRF desabilitado. A nova regra explícita de PATCH não adiciona autenticação nem autorização por usuário. |

Também permanecem as limitações anteriores: o PUT rejeita o próprio e-mail do usuário como duplicado, a listagem não possui paginação, a confirmação de senha é persistida com hash próprio, e o handler personalizado cobre apenas usuário não encontrado. PostgreSQL possui driver, mas o banco configurado é H2 em memória; Flyway está desabilitado. Vagas, candidaturas, entrevistas e histórico possuem modelos de domínio, sem fluxos completos de API e persistência.

## Verificação realizada

Comando executado na raiz do projeto em 04/10/2026:

```powershell
.\mvnw.cmd test -B -ntp
```

Ambiente: Java **25.0.2**, Maven Wrapper **3.9.16**, Spring Boot **4.1.0** e compilação com `release 21`.

Resultado: **BUILD SUCCESS — 7 testes executados, 0 falhas, 0 erros e 0 ignorados**.

| Classe | Testes | Cobertura existente |
| --- | --- | --- |
| `UsersTest` | 6 | Senha curta, senha longa, nome curto, nome longo, confirmação divergente e cadastro válido. |
| `DevJobsApplicationTests` | 1 | Inicialização do contexto Spring com a configuração local e H2. |

A configuração atual de `PutUsersUseCaseImpl` passa os três argumentos esperados pelo construtor. A falha de compilação registrada em 30/09 não foi reproduzida nesta revisão. O registro antigo mantém o resultado daquela execução.

Nenhum teste específico de troca de senha, controller ou persistência foi adicionado nesta entrega. A aprovação dos testes existentes não cobre os problemas listados acima.

## Commits por arquivo

Foram organizados **18 commits para 18 arquivos**: 16 arquivos Java, o README e este relatório, seguindo `tipo(escopo): descrição`. Cada commit contém exatamente um arquivo.

| Commit | Arquivo | Mensagem |
| --- | --- | --- |
| `0df326f` | [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java) | `feat(user): add password change constructor and old password field` |
| `0001d96` | [ErrorCodeEnum.java](../src/main/java/br/costa/DevJobs/core/exception/enums/ErrorCodeEnum.java) | `feat(errors): add current password mismatch code` |
| `a02b235` | [PassAvailableException.java](../src/main/java/br/costa/DevJobs/core/exception/PassAvailableException.java) | `feat(errors): add password verification exception` |
| `ed89eb3` | [PassAvailableGatway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PassAvailableGatway.java) | `feat(user): add current password verification gateway` |
| `0eb469d` | [PatchPassUsersGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PatchPassUsersGateway.java) | `feat(user): add password update gateway contract` |
| `7d74b66` | [PassAvailableUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/PassAvailableUseCase.java) | `feat(user): add password verification use case contract` |
| `dba15c3` | [PatchPassUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/PatchPassUseCase.java) | `feat(user): add password change use case contract` |
| `d4b1baf` | [PatchUserPassRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/PatchUserPassRequestDto.java) | `feat(user): add password change request DTO` |
| `a5053f4` | [UserMapper.java](../src/main/java/br/costa/DevJobs/infrastructure/mappers/UserMapper.java) | `feat(user): map password change requests and align method names` |
| `f92fdb8` | [PassAvilableUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PassAvilableUseCaseImpl.java) | `feat(user): delegate current password verification to gateway` |
| `96bc0f1` | [PatchPassUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PatchPassUseCaseImpl.java) | `feat(user): add password change use case implementation` |
| `5728093` | [PassAvailableService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/PassAvailableService.java) | `feat(user): add BCrypt password verification service` |
| `38c5ccc` | [PatchPasswordUserService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/PatchPasswordUserService.java) | `feat(user): add password update persistence service` |
| `5b1f846` | [UsersUseCaseConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/UsersUseCaseConfig.java) | `feat(user): wire password verification and update use cases` |
| `ee43afa` | [SecurityConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/SecurityConfig.java) | `chore(security): explicitly permit PATCH requests` |
| `6fd33d8` | [UsersController.java](../src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java) | `feat(user): expose password change endpoint` |
| `89b32f3` | [README.md](../README.md) | `docs(project): update API documentation and validation status` |

Este relatório recebe o 18º commit, com a mensagem `docs(history): record October 4 changes and validation`. Seu próprio hash não é incluído para evitar uma referência circular.

Os commits foram criados localmente na branch `master`, sem push. Como as mudanças dependem umas das outras e foram separadas por arquivo conforme solicitado, não há garantia de compilação de cada revisão intermediária.

Para conferir o histórico desta entrega:

```powershell
git log --reverse --oneline da92617604ff63b06993d3875a1f2fb1f35e896d..HEAD
git diff --stat da92617604ff63b06993d3875a1f2fb1f35e896d..HEAD
git status --short
```

A documentação acumulada está no [README](../README.md). Os registros de [18/09/2026](atualizacao-2026-09-18.md) e [30/09/2026](atualizacao-2026-09-30.md) preservam as respectivas etapas.
