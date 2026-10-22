# DevJobs: relatório de alterações de 22/10/2026

Registro das alterações recebidas na branch `master`, a partir do commit `8962f7eac397b4261136763cd4a5926c1c2fba32`. Data considerada no fuso `America/Sao_Paulo`.

Esta entrega reúne **17 commits para 17 arquivos**: dez arquivos de produção Java, cinco classes novas de teste, o README atualizado e este relatório. As mensagens seguem Conventional Commits no formato `tipo(escopo): descrição`, mantendo a convenção do repositório de um arquivo por commit. O conteúdo dos 15 arquivos Java recebidos foi preservado durante a organização.

A compilação passou. A suíte executou **107 testes: 98 passaram e 9 falharam, sem erros ou testes ignorados**. O build terminou com `BUILD FAILURE` na fase de testes; os commits registram esse estado e suas pendências.

## Alterações funcionais

| Área | Resultado |
| --- | --- |
| Atualização de perfil | `UpdateUserPersistence` e `UpdateUserResult` passam a receber `fullName` antes de `email`, acompanhando os chamadores de produção. |
| Resposta do PUT | O mapper preenche nome e e-mail corretamente, usa a data de modificação da entidade e inclui o papel no campo JSON `roles`. |
| Troca de senha | `PatchPassUseCaseImpl` constrói `Users` com a nova senha, aplicando o limite de 8 a 64 caracteres antes de persistir. |
| Contrato de persistência de senha | Novo `UpdatePassUserPersistence(password)`, recebido pelo gateway e pelo serviço; a senha continua sendo gravada como hash BCrypt. |
| Cadastro | `UsersRequestDto.email` recebe `@Email`. Os testes confirmam rejeição de e-mail malformado e composto por espaços, mas e-mail vazio ainda é aceito. |
| Testes | Cinco classes novas acrescentam 100 execuções à suíte anterior de sete testes. |
| Documentação | README atualizado com o estado atual e este relatório. A referência ao relatório de 19/10 foi substituída pela desta entrega; aquele arquivo não existe na árvore atual. |

Os cenários válidos de atualização de perfil e troca de senha passaram na integração. O nome `roles` é o campo efetivamente declarado no DTO do PUT; consulta e listagem continuam usando `role`.

O PATCH mantém as verificações de existência do usuário, senha antiga e confirmação antes da validação de tamanho. O serviço lê `password()` do novo record, gera o hash, atualiza `updatedAt` e salva a entidade. Confirmação e senha antiga não fazem parte desse record.

## Verificação realizada

Comando executado na raiz:

```powershell
.\mvnw.cmd clean test -B -ntp
```

Ambiente: Java **25.0.2**, Maven Wrapper **3.9.16**, Spring Boot **4.1.0** e compilação com `release 21`. Foram compilados **84 arquivos principais e sete arquivos de teste**.

| Classe | Executados | Passaram | Falhas | Erros | Ignorados |
| --- | ---: | ---: | ---: | ---: | ---: |
| `UsersUseCasesTest` | 21 | 21 | 0 | 0 | 0 |
| `UsersBoundaryTest` | 13 | 13 | 0 | 0 | 0 |
| `UsersTest` | 6 | 5 | 1 | 0 | 0 |
| `DevJobsApplicationTests` | 1 | 1 | 0 | 0 | 0 |
| `UsersApiIntegrationTest` | 46 | 40 | 6 | 0 | 0 |
| `UserMapperTest` | 8 | 7 | 1 | 0 | 0 |
| `UsersServicesTest` | 12 | 11 | 1 | 0 | 0 |
| **Total** | **107** | **98** | **9** | **0** | **0** |

A integração usa MockMvc, contexto Spring e um H2 em memória exclusivo (`devjobs_users_api_test`), com limpeza antes de cada caso. Não houve requisições a um servidor HTTP externo nem validação com PostgreSQL. Os relatórios brutos ficam em `target/surefire-reports`, ignorado pelo Git.

A suíte foi executada sobre o mesmo conteúdo Java incluído nos commits. As alterações posteriores desta organização foram de documentação.

## Nove falhas identificadas

| Teste | Quantidade | Resultado observado e pendência |
| --- | ---: | --- |
| `UsersTest.shouldRejectdiferentPass` | 1 | Espera `InvalidPasswordException`, mas o domínio lança `PassAvailableException` para confirmação divergente. A divergência já consta no relatório de 08/10. |
| `UserMapperTest.updateResponseDoesNotSwapNameAndEmail` | 1 | O teste constrói `UpdateUserResult` com e-mail antes do nome, usando a ordem antiga. Atualizar os dados de entrada do teste para o contrato atual. |
| `UsersServicesTest.profileUpdatePreservesPasswordRoleAndCreationDate` | 1 | O teste constrói `UpdateUserPersistence` com e-mail antes do nome. Atualizar os dados de entrada do teste para o contrato atual. |
| `UsersApiIntegrationTest.profileUpdateRejectsMalformedOrBlankEmail` | 3 | O PUT responde `200` para `not-an-email`, string vazia e três espaços; o esperado é `400`. O DTO de atualização não valida formato nem conteúdo em branco. |
| `UsersApiIntegrationTest.registrationRejectsMalformedOrBlankEmail` | 1 | O cadastro responde `201` para e-mail vazio; o esperado é `400`. A anotação `@Email` adicionada não rejeita esse caso. |
| `UsersApiIntegrationTest.registrationRejectsWhitespaceOnlyName` | 1 | Um nome composto por seis espaços resulta em `201`; o esperado é `400`. A regra atual verifica somente comprimento. |
| `UsersApiIntegrationTest.inputValidationErrorsHaveTheSameEnvelopeAsDomainErrors` | 1 | A resposta tem status HTTP `400`, mas não traz o corpo JSON com `status`, `path` e `errors` esperado pelo teste. Falta padronizar Bean Validation no handler. |
| **Total** | **9** | **Três falhas de expectativas/dados de teste e seis de validação/resposta HTTP.** |

Exemplos da ordem atual dos records:

```java
new UpdateUserPersistence("Pessoa Atualizada", "updated@example.test");
new UpdateUserResult(42L, "Pessoa Atualizada", "updated@example.test",
        updatedAt, UserRoleEnum.ROLE_USER);
```

As falhas foram documentadas, sem alterar os testes ou acrescentar correções funcionais durante a organização dos commits.

## Commits por arquivo

| Commit | Arquivo | Alteração | Mensagem |
| --- | --- | --- | --- |
| `4f26f6d` | [UpdateUserPersistence.java](../src/main/java/br/costa/DevJobs/application/persistence/user/UpdateUserPersistence.java) | Alinha os componentes do record à ordem fullName, email usada pelo caso de uso. | `fix(user): align profile persistence field order` |
| `a0540d3` | [UpdateUserResult.java](../src/main/java/br/costa/DevJobs/application/result/users/UpdateUserResult.java) | Alinha o resultado da atualização à ordem fullName, email usada pelo mapper. | `fix(user): align profile update result field order` |
| `3250b7b` | [PutUserResponseDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/response/userresponse/PutUserResponseDto.java) | Acrescenta roles à resposta de atualização de perfil. | `feat(user): include role in profile update response` |
| `04b7785` | [UserMapper.java](../src/main/java/br/costa/DevJobs/infrastructure/mappers/UserMapper.java) | Corrige nome/e-mail na resposta e usa updatedAt da entidade. | `fix(user): preserve profile fields and modification timestamp` |
| `96fff2c` | [UpdatePassUserPersistence.java](../src/main/java/br/costa/DevJobs/application/persistence/user/UpdatePassUserPersistence.java) | Cria um modelo de persistência contendo somente a nova senha. | `refactor(user): add password update persistence record` |
| `a3a1c0b` | [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java) | Substitui o construtor de três senhas por um construtor que valida a nova senha. | `refactor(user): add constructor for password validation` |
| `1a4bc69` | [PatchPassUsersGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/PatchPassUsersGateway.java) | Altera o gateway para receber UpdatePassUserPersistence. | `refactor(user): accept password persistence record in gateway` |
| `3b116dc` | [PatchPasswordUserService.java](../src/main/java/br/costa/DevJobs/infrastructure/service/userservice/PatchPasswordUserService.java) | Extrai a nova senha do record antes de gerar o hash BCrypt. | `refactor(user): read new password from persistence record` |
| `8421567` | [PatchPassUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/PatchPassUseCaseImpl.java) | Valida a nova senha no domínio antes de chamar o gateway. | `fix(user): enforce password length policy before updating` |
| `5944844` | [UsersRequestDto.java](../src/main/java/br/costa/DevJobs/infrastructure/dto/request/userrequest/UsersRequestDto.java) | Acrescenta @Email ao e-mail de cadastro. | `fix(user): validate email format during registration` |
| `945e355` | [UsersBoundaryTest.java](../src/test/java/br/costa/DevJobs/core/domain/users/UsersBoundaryTest.java) | Cobre limites de nome/senha, estado inicial e preservação após setters inválidos. | `test(user): cover domain validation boundaries and initial state` |
| `a073937` | [UsersUseCasesTest.java](../src/test/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/UsersUseCasesTest.java) | Cobre cadastro, atualização, troca de senha, exclusão e propagação de falhas. | `test(user): cover use case validation and gateway failures` |
| `d572539` | [UserMapperTest.java](../src/test/java/br/costa/DevJobs/infrastructure/mappers/UserMapperTest.java) | Cobre conversões entre DTOs, comandos, entidade e resultados. | `test(user): cover request and response mappings` |
| `c3f5656` | [UsersServicesTest.java](../src/test/java/br/costa/DevJobs/infrastructure/service/userservice/UsersServicesTest.java) | Cobre geração/verificação de hashes, atualização e retornos da persistência. | `test(user): cover password hashing and persistence behavior` |
| `499afaf` | [UsersApiIntegrationTest.java](../src/test/java/br/costa/DevJobs/infrastructure/controller/UsersApiIntegrationTest.java) | Cobre os endpoints com MockMvc, Spring e H2 isolado. | `test(user): cover user API flows with MockMvc and H2` |
| `ada0467` | [README.md](../README.md) | Atualiza contratos, validações, testes e referência ao relatório. | `docs(project): update user API behavior and validation status` |
| Próprio commit deste arquivo | [atualizacao-2026-10-22.md](atualizacao-2026-10-22.md) | Registra alterações, commits, validação e pendências. | `docs(project): report user changes and test failures` |

O hash do commit deste relatório pode ser consultado com:

```powershell
git log -1 --format="%h %s" -- docs/atualizacao-2026-10-22.md
```

Os commits individuais seguem a convenção por arquivo do README. A compilação e os testes referem-se ao conjunto completo desta entrega; commits intermediários com mudanças de assinatura dependem dos demais arquivos.

## Pendências para a próxima implementação

- Alinhar a expectativa da exceção de confirmação divergente e os dados dos testes à ordem atual dos records.
- Rejeitar e-mail vazio no cadastro, validar e-mail no PUT e rejeitar nome composto apenas por espaços.
- Padronizar o corpo das respostas de Bean Validation.
- Revisar o limite transacional da troca de senha, que ainda não declara uma transação abrangendo consulta e gravação.
- Prosseguir com autenticação, autorização, PostgreSQL, migrações e paginação, conforme o planejamento do projeto.

## Conferência da entrega

A conferência final compara os hashes SHA-256 dos 15 arquivos Java com o conteúdo recebido, verifica mensagens Conventional Commits e um arquivo por commit, executa `git diff --check` no intervalo da entrega e confirma ausência de alterações pendentes. Nenhum push integra esta entrega.
