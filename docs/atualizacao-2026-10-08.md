# DevJobs: entregas de 08/10/2026

Registro das alterações pendentes na branch `master`, a partir do commit `60dcc61c0d258824ea0ed688dee9fae6dd79c25a`. Data considerada no fuso `America/Sao_Paulo`.

Esta entrega organiza os commits por arquivo e atualiza a documentação. O código Java recebido foi preservado. A compilação passou, mas a execução dos sete testes encontrou uma falha de expectativa de exceção, detalhada abaixo.

## O que mudou

| Área | Alterações desta etapa |
| --- | --- |
| Organização | Seis exceções movidas para `core.exception.usersexception`; três DTOs de entrada para `request.userrequest` e três de saída para `response.userresponse`; nove componentes existentes movidos para `service.userservice`. |
| Listagem | `FindAllConfig` passou a se chamar `FindAllService` e saiu de `config` para o pacote de serviços de usuários. Controller e referência de documentação foram ajustados. |
| Cadastro | Criação da entidade e geração dos hashes BCrypt extraídas de `UserMapper` para `CreateUserEntityService`, usado por `UserRegisterService`. |
| Domínio | Confirmação de senha divergente agora lança `PassAvailableException`, mantendo o código `IPN0002`. Imports foram alinhados aos novos pacotes. |
| Troca de senha | Contratos recebem os campos de senha diretamente; conferência retorna `void`, consulta o hash persistido e propaga exceções; atualização salva os hashes na entidade existente. |
| Exclusão | Novos gateway, contrato de caso de uso, implementação e serviço transacional; validação de existência do usuário antes de `deleteById`. |
| Spring e segurança | Bean de exclusão, nova dependência do caso de uso de senha e regra explícita para permitir DELETE. |
| API | `DELETE /v1/users/{id}` com HTTP 204; PATCH passa as strings do DTO diretamente ao caso de uso. |
| Erros | Handler ampliado para conflitos de senha/e-mail, erro interno, senha inválida e nome inválido; tratamento de ID ausente preservado. |
| Testes | Imports de `UsersTest` ajustados aos novos pacotes; a expectativa do teste de confirmação divergente ainda não acompanha a mudança no domínio. |
| Documentação | README atualizado; referência histórica de listagem esclarecida no registro de 30/09; este relatório relaciona arquivos, mudanças, commits e verificação. |

As grafias `PassAvailableGatway`, `PassAvilableUseCaseImpl` e `EmailAvaliableException` correspondem aos nomes presentes no código.

## Exclusão de usuários

Contrato declarado em [UsersController](../src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java):

```http
DELETE /v1/users/{id}
```

A requisição não recebe corpo. `DeleteUserUseCaseImpl` consulta `IdAvailableUseCase`; quando o retorno é `false`, lança `IdFoundAvailableException` com o código `IAI0001`, tratado como HTTP 404. Caso o ID exista, delega a `DeleteUserGateway`. `DeleteUserService` executa `UsersRepository.deleteById` em método anotado com `@Transactional`. O controller declara HTTP 204 e corpo vazio no sucesso.

A regra explícita de DELETE não altera a configuração que já libera todas as requisições com `anyRequest().permitAll()`. Autenticação e autorização por usuário continuam pendentes. O endpoint não foi exercitado via HTTP nesta entrega.

## Ajustes na troca de senha

O contrato HTTP continua sendo `PATCH /v1/users/{id}`, com HTTP 200 e corpo vazio no sucesso declarado:

```json
{
  "oldPassword": "senha-exemplo-123",
  "newPassword": "nova-senha-456",
  "confirmNewPassword": "nova-senha-456"
}
```

A sequência presente no código passou a ser:

1. O controller valida que os três campos do DTO não são nulos e os passa diretamente a `PatchPassUseCase`.
2. `PatchPassUseCaseImpl` verifica a existência do ID via `IdAvailableUseCase`.
3. `PassAvailableService` carrega a entidade no repository e compara a senha antiga com o hash armazenado por BCrypt.
4. Senha antiga divergente lança `PassAvailableException`, propagada até o handler HTTP 409. A conferência não captura mais a exceção nem retorna `false`.
5. `PatchPasswordUserService` busca a entidade existente, atribui hashes BCrypt separados para nova senha e confirmação e chama `save`.

O uso da entidade persistida remove os problemas anteriores de hash ausente no domínio, ordem dos setters e criação de outra entidade sem ID durante a troca. Essas conclusões vêm da leitura do código, sem teste HTTP ou integração específica do PATCH.

Permanecem pendentes:

- O controller deixou de chamar `UserMapper.patchPassRequestToUsers`, portanto a validação do domínio de tamanho e igualdade das novas senhas não é executada. O serviço grava os dois valores diretamente na entidade.
- O serviço de gravação captura qualquer exceção e retorna `false`; o caso de uso ignora o retorno, permitindo HTTP 200 mesmo quando a gravação falha.
- O serviço não atualiza `updatedAt` nem declara uma transação que englobe consulta e gravação.
- Senha e confirmação continuam persistidas com hashes próprios. Não existem testes específicos do novo fluxo.

## Respostas de erro

[GlobalExceptionHandler](../src/main/java/br/costa/DevJobs/infrastructure/exception/GlobalExceptionHandler.java) usa a estrutura `timestamp`, `status`, `erro`, `message`, `path` e `errors`:

| Exceção | HTTP | Situação |
| --- | --- | --- |
| `IdFoundAvailableException` | 404 | Tratamento existente, com texto de erro alterado para `Id not Found`. |
| `PassAvailableException` | 409 | Novo tratamento; cobre senha antiga divergente e confirmação divergente quando validada no domínio. |
| `EmailAvaliableException` | 409 | Novo tratamento de e-mail já cadastrado. |
| `InternalServerErrorException` | 500 | Novo tratamento da falha sinalizada pelo cadastro. |
| `InvalidPasswordException` | 400 | Novo tratamento de tamanho inválido de senha no domínio. |
| `ValidateFullNameException` | 400 | Novo tratamento de nome inválido no domínio. |

Os tratamentos mantêm `errors` nulo e não incluem o código da exceção no corpo. A padronização própria de Bean Validation permanece pendente. No cadastro, confirmação divergente passou de `InvalidPasswordException` para `PassAvailableException` e recebe HTTP 409 pelo novo handler.

## Verificação realizada

Comando executado na raiz em 08/10/2026:

```powershell
.\mvnw.cmd test -B -ntp
```

Ambiente: Java **25.0.2**, Maven Wrapper **3.9.16**, Spring Boot **4.1.0** e compilação com `release 21`.

Resultado: **BUILD FAILURE na etapa de testes — 7 testes executados, 1 falha, 0 erros e 0 ignorados**. A compilação principal e de testes passou.

| Classe | Executados | Falhas | Resultado |
| --- | --- | --- | --- |
| `UsersTest` | 6 | 1 | Cinco testes passaram; `shouldRejectdiferentPass` falhou por tipo de exceção. |
| `DevJobsApplicationTests` | 1 | 0 | `contextLoads` passou com a configuração Spring e o H2 atuais. |

A falha está em [UsersTest.java](../src/test/java/br/costa/DevJobs/core/domain/users/UsersTest.java), linha 74: o teste espera `InvalidPasswordException`, mas [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java), linha 178, lança `PassAvailableException` para confirmação diferente. O tipo da exceção mudou nas alterações recebidas; o teste recebeu apenas novos imports. Esta entrega documenta a divergência e preserva ambos os arquivos como recebidos.

Os relatórios da execução ficam em `target/surefire-reports`, ignorados pelo Git. Nenhum teste novo foi acrescentado nesta organização. Os endpoints de cadastro, consulta, listagem, atualização, troca de senha e exclusão não foram exercitados via HTTP.

Além dos testes, a conferência da entrega verifica mensagens Conventional Commits, caminhos isolados em cada commit, hashes dos arquivos Java antes e depois dos commits e ausência de alterações pendentes. `git diff --check` verifica os espaços em branco.

## Commits por arquivo

Foram organizados **45 commits para 45 arquivos lógicos**: 42 arquivos Java e três arquivos de documentação. As 21 renomeações incluem os caminhos antigo e novo do mesmo arquivo no commit dedicado. A detecção padrão do Git pode apresentar `PatchPasswordUserService` como exclusão e criação por causa da extensão das mudanças; seu par de caminhos permanece isolado.

| Commit | Arquivo atual | Alteração | Mensagem |
| --- | --- | --- | --- |
| `33aa545` | [EmailAvaliableException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/EmailAvaliableException.java) | Move a exceção de e-mail duplicado para o pacote usersexception. | `refactor(errors): move EmailAvaliableException to user exception package` |
| `3e95adf` | [IdFoundAvailableException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/IdFoundAvailableException.java) | Move a exceção de ID ausente para o pacote usersexception. | `refactor(errors): move IdFoundAvailableException to user exception package` |
| `fbe1ab2` | [InternalServerErrorException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/InternalServerErrorException.java) | Move a exceção de erro interno para o pacote usersexception. | `refactor(errors): move InternalServerErrorException to user exception package` |
| `87f0f12` | [InvalidPasswordException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/InvalidPasswordException.java) | Move a exceção de senha inválida para o pacote usersexception. | `refactor(errors): move InvalidPasswordException to user exception package` |
| `bb0e96f` | [PassAvailableException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/PassAvailableException.java) | Move a exceção de divergência de senha para o pacote usersexception. | `refactor(errors): move PassAvailableException to user exception package` |
| `d2414c5` | [ValidateFullNameException.java](../src/main/java/br/costa/DevJobs/core/exception/usersexception/ValidateFullNameException.java) | Move a exceção de nome inválido para o pacote usersexception. | `refactor(errors): move ValidateFullNameException to user exception package` |
| `4d98d74` | [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java) | Atualiza imports e lança PassAvailableException com IPN0002 quando a confirmação diverge. | `fix(user): signal password confirmation mismatch with conflict exception` |
| `e24c7b6` | [DeleteUserGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/DeleteUserGateway.java) | Define deleteUser(Long id) para exclusão na infraestrutura. | `feat(user): add user deletion gateway contract` |
| `c38c757` | [PassAvailableGatway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PassAvailableGatway.java) | Altera a conferência de senha de Boolean para void. | `refactor(user): make password verification gateway return void` |
| `f5e4271` | [PatchPassUsersGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PatchPassUsersGateway.java) | Recebe nova senha, confirmação e ID em vez de Users. | `refactor(user): accept password fields in update gateway` |
| `1e609b3` | [DeleteUserUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/DeleteUserUseCase.java) | Define delete(Long id) para a aplicação. | `feat(user): add user deletion use case contract` |
| `3ca1dc5` | [PassAvailableUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/PassAvailableUseCase.java) | Alinha a assinatura de conferência ao gateway sem retorno. | `refactor(user): make password verification use case return void` |
| `d401d31` | [PatchPassUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/PatchPassUseCase.java) | Recebe senha antiga, nova senha, confirmação e ID. | `refactor(user): accept password fields in change use case` |
| `8465a30` | [PatchUserPassRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/userrequest/PatchUserPassRequestDto.java) | Move o DTO de troca de senha para request.userrequest, preservando seus campos. | `refactor(dto): move PatchUserPassRequestDto to user request package` |
| `fd31b77` | [PutUserRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/userrequest/PutUserRequestDto.java) | Move o DTO de atualização de perfil para request.userrequest, preservando seus campos. | `refactor(dto): move PutUserRequestDto to user request package` |
| `2c52ea8` | [UsersRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/userrequest/UsersRequestDto.java) | Move o DTO de cadastro para request.userrequest, preservando seus campos. | `refactor(dto): move UsersRequestDto to user request package` |
| `385b1b3` | [PutUserResponseDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/response/userresponse/PutUserResponseDto.java) | Move o DTO de resposta de atualização de perfil para response.userresponse. | `refactor(dto): move PutUserResponseDto to user response package` |
| `74e2c44` | [UserResponseFindByIdDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/response/userresponse/UserResponseFindByIdDto.java) | Move o DTO de resposta de consulta de usuário para response.userresponse. | `refactor(dto): move UserResponseFindByIdDto to user response package` |
| `a776bef` | [UsersResponseDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/response/userresponse/UsersResponseDto.java) | Move o DTO de resposta de cadastro para response.userresponse. | `refactor(dto): move UsersResponseDto to user response package` |
| `344d6a1` | [FindByIdUsersUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/FindByIdUsersUseCaseImpl.java) | Atualiza o import de IdFoundAvailableException. | `refactor(user): update missing id exception import in lookup` |
| `5b6d7aa` | [PassAvilableUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PassAvilableUseCaseImpl.java) | Delega ao gateway sem retornar Boolean, permitindo propagação de exceções. | `refactor(user): align password verification delegation with void contract` |
| `4fdbc4a` | [PatchPassUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PatchPassUseCaseImpl.java) | Verifica existência do ID, confere a senha antiga e delega os novos valores ao gateway. | `fix(user): verify user and current password before updating` |
| `cf5fdbf` | [PutUsersUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PutUsersUseCaseImpl.java) | Atualiza o import de EmailAvaliableException. | `refactor(user): update email exception import in profile changes` |
| `1c89ac3` | [RegisterAccountUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/RegisterAccountUseCaseImpl.java) | Usa os novos pacotes das exceções de e-mail e erro interno. | `refactor(user): update registration exception imports` |
| `99a5660` | [DeleteUserUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/DeleteUserUseCaseImpl.java) | Verifica o ID e delega a exclusão; sinaliza IAI0001 para usuário ausente. | `feat(user): validate user existence before deletion` |
| `e562ae6` | [CreateUserEntityService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/CreateUserEntityService.java) | Extrai a criação da entidade e a codificação BCrypt de senha e confirmação. | `refactor(user): extract registration entity creation service` |
| `bc17945` | [UserMapper.java](../src/main/java/br/costa/DevJobs/infrastructure/mappers/UserMapper.java) | Remove a criação da entidade do mapper e atualiza imports dos DTOs. | `refactor(user): extract registration entity creation from mapper` |
| `ad82fb0` | [EmailAvailableService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/EmailAvailableService.java) | Move o serviço de consulta de e-mail para userservice. | `refactor(user): move email availability service to user package` |
| `80799dd` | [FindAllService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/FindAllService.java) | Renomeia FindAllConfig para FindAllService, move o componente e atualiza o DTO. | `refactor(user): move listing response conversion into service package` |
| `12552c7` | [FindAllUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/FindAllUsersService.java) | Move o serviço JPA de listagem para userservice. | `refactor(user): move user listing service to user package` |
| `7792e6c` | [FindByIdUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/FindByIdUsersService.java) | Move a consulta por ID e atualiza o import da exceção. | `refactor(user): relocate lookup service and update id exception import` |
| `7255aab` | [IdAvailableService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/IdAvailableService.java) | Move o serviço de existência de ID para userservice. | `refactor(user): move id availability service to user package` |
| `041307f` | [PassAvailableService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/PassAvailableService.java) | Move o serviço, consulta a entidade no repository e propaga falhas de conferência. | `fix(user): check current password against persisted BCrypt hash` |
| `ee7defa` | [PatchPasswordUserService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/PatchPasswordUserService.java) | Move o serviço e salva novos hashes na entidade carregada; mantém captura genérica e retorno Boolean. | `fix(user): update password hashes on existing user entity` |
| `902e661` | [PutUsersService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/PutUsersService.java) | Move o serviço de atualização e atualiza o import da exceção de ID. | `refactor(user): relocate profile update service and exception import` |
| `7864d0a` | [UserRegisterService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/UserRegisterService.java) | Move o serviço e usa CreateUserEntityService para montar a entidade de cadastro. | `refactor(user): use entity creation service for registration` |
| `4208861` | [DeleteUserService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/DeleteUserService.java) | Implementa DeleteUserGateway com deleteById e @Transactional. | `feat(user): delete users transactionally through repository` |
| `a6d6f76` | [UsersUseCaseConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/UsersUseCaseConfig.java) | Registra DeleteUserUseCase e injeta IdAvailableUseCase no fluxo de troca de senha. | `feat(user): wire deletion and revise password change dependencies` |
| `c7d6dfd` | [SecurityConfig.java](../src/main/java/br/costa/DevJobs/infrastructure/config/SecurityConfig.java) | Adiciona regra explícita de DELETE à configuração já permissiva. | `chore(security): explicitly permit DELETE requests` |
| `a6bc2d5` | [GlobalExceptionHandler.java](../src/main/java/br/costa/DevJobs/infrastructure/exception/GlobalExceptionHandler.java) | Mantém 404 e adiciona tratamentos 409, 500 e 400 para exceções de usuário. | `feat(errors): map user domain exceptions to HTTP responses` |
| `a2fc4e3` | [UsersController.java](../src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java) | Atualiza imports, usa FindAllService, recebe strings no PATCH e declara DELETE com 204. | `feat(user): expose deletion and pass password fields to use case` |
| `beec33a` | [UsersTest.java](../src/test/java/br/costa/DevJobs/core/domain/users/UsersTest.java) | Atualiza imports das exceções; a expectativa de confirmação divergente continua desatualizada. | `test(user): update validation imports to user exception package` |
| `7638860` | [atualizacao-2026-09-30.md](../docs/atualizacao-2026-09-30.md) | Atualiza a referência de FindAllConfig para FindAllService e esclarece a data da renomeação. | `docs(history): align listing service reference with current name` |
| `e841ee3` | [README.md](../README.md) | Documenta pacotes, exclusão, fluxo de senha, erros HTTP, pendências e resultado dos testes. | `docs(project): update user API and October 8 validation status` |

Este relatório recebe o 45º commit, com a mensagem `docs(history): record October 8 changes and per-file commits`. Seu próprio hash não é incluído para evitar uma referência circular.

Os commits foram criados localmente na branch `master`. Como as mudanças dependem umas das outras e foram separadas por arquivo conforme solicitado, não há garantia de compilação de cada revisão intermediária. O resultado dos testes acima corresponde ao conjunto final de arquivos Java.

Para conferir a entrega:

```powershell
git log --reverse --oneline 60dcc61c0d258824ea0ed688dee9fae6dd79c25a..HEAD
git diff --stat 60dcc61c0d258824ea0ed688dee9fae6dd79c25a..HEAD
git diff --check 60dcc61c0d258824ea0ed688dee9fae6dd79c25a..HEAD
git status --short
```

A documentação acumulada está no [README](../README.md). Os registros de [18/09/2026](atualizacao-2026-09-18.md), [30/09/2026](atualizacao-2026-09-30.md) e [04/10/2026](atualizacao-2026-10-04.md) preservam os resultados das respectivas etapas.
