# DevJobs: entregas de 18/09/2026

Registro das alterações commitadas em 18/09/2026, considerando o fuso `America/Sao_Paulo`. A documentação acumulada do projeto está no [README](../README.md).

Este registro descreve a revisão daquela data. Para os recursos e a verificação de 30/09/2026, consulte o [registro mais recente](atualizacao-2026-09-30.md). Os links das duas implementações de casos de uso abaixo foram atualizados para os nomes e o pacote atuais.

## O que foi entregue nesta etapa

- **Validações de usuário:** nome completo entre 11 e 100 caracteres, senha entre 15 e 64 e confirmação igual à senha. O usuário passa a ter `confirmPassword` e contador `attempt` inicializado em zero.
- **Contratos de aplicação:** interfaces para cadastro de conta e consulta de disponibilidade de e-mail, acompanhadas dos respectivos gateways.
- **Implementações dos casos de uso:** delegação da consulta de e-mail ao gateway e fluxo de cadastro com verificação de disponibilidade, conflito de e-mail e falha sinalizada pelo gateway de cadastro.
- **Base de tratamento de erros:** quatro exceções próprias e um enum com mensagens e códigos para validações, conflito, falha de cadastro e futura verificação de PIN.
- **Correção em entrevista:** uso de `SituationInterview.SCHEDULED`, em conformidade com a constante existente no enum.
- **Teste unitário:** rejeição de senha com menos de 15 caracteres.
- **Documentação:** estado atual do projeto, organização do código, regras, execução, testes, pendências e este registro com texto para LinkedIn.

### Alteração incompatível

O construtor de `Users` agora recebe cinco argumentos, nesta ordem:

```java
Users(Long id, String password, String confirmPassword, String email, String fullName)
```

Chamadas à assinatura anterior precisam incluir a confirmação de senha. O commit correspondente utiliza `feat(user)!` e um rodapé `BREAKING CHANGE`, que também registra as novas restrições dos campos.

## Commits

Cada commit abaixo altera exatamente um arquivo. As mensagens seguem Conventional Commits.

| Commit | Arquivo | Mensagem |
| --- | --- | --- |
| `706846c` | [Interview.java](../src/main/java/br/costa/DevJobs/core/domain/Interview.java) | `fix(interview): use scheduled status enum constant` |
| `fa0163b` | [ErrorCodeEnum.java](../src/main/java/br/costa/DevJobs/core/exception/enums/ErrorCodeEnum.java) | `feat(errors): define account validation and registration error codes` |
| `e95ce7d` | [BadRequestException.java](../src/main/java/br/costa/DevJobs/core/exception/BadRequestException.java) | `feat(errors): add bad request exception` |
| `6b01fb9` | [ConflictException.java](../src/main/java/br/costa/DevJobs/core/exception/ConflictException.java) | `feat(errors): add conflict exception` |
| `4d3b1ec` | [InternalServerErrorException.java](../src/main/java/br/costa/DevJobs/core/exception/InternalServerErrorException.java) | `feat(errors): add internal server error exception` |
| `5a5d45e` | [NotFoundException.java](../src/main/java/br/costa/DevJobs/core/exception/NotFoundException.java) | `feat(errors): add not found exception` |
| `ff6d4ef` | [Users.java](../src/main/java/br/costa/DevJobs/core/domain/Users.java) | `feat(user)!: validate account fields and track attempts` |
| `2c36822` | [EmailAvaliableGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/EmailAvaliableGateway.java) | `feat(user): add email availability gateway contract` |
| `b812001` | [RegisterUseGateway.java](../src/main/java/br/costa/DevJobs/application/gateway/usersgateway/RegisterUseGateway.java) | `feat(user): add registration gateway contract` |
| `73be54e` | [EmailAvaliableUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/EmailAvaliableUseCase.java) | `feat(user): add email availability use case contract` |
| `4c20a04` | [RegisterAcountUseCase.java](../src/main/java/br/costa/DevJobs/usecase/userusecase/RegisterAcountUseCase.java) | `feat(user): add account registration use case contract` |
| `de28893` | [EmailAvailableUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/EmailAvailableUseCaseImpl.java) | `feat(user): delegate email availability checks to gateway` |
| `caf7ecd` | [RegisterAccountUseCaseImpl.java](../src/main/java/br/costa/DevJobs/application/usecaseimpl/usersusecaseimpl/RegisterAccountUseCaseImpl.java) | `feat(user): implement account registration flow` |
| `f666a83` | [UsersTest.java](../src/test/java/br/costa/DevJobs/core/domain/users/UsersTest.java) | `test(user): reject passwords shorter than fifteen characters` |
| `53acb2b` | [README.md](../README.md) | `docs(project): document current implementation and setup` |

Este relatório recebe um commit próprio com a mensagem `docs(project): record daily changes and LinkedIn draft`, completando 16 commits para 16 arquivos nesta entrega. Os commits foram criados localmente na branch `master`; não foi realizado push.

## Verificação realizada

Comando: `mvnw.cmd test`, usando Temurin 21.0.12 e o Maven Wrapper do projeto.

Resultado: **BUILD SUCCESS**, com **2 testes executados, 0 falhas, 0 erros e 0 ignorados**:

- `DevJobsApplicationTests.contextLoads`.
- `UsersTest.shouldRejectPasswithminor15Characters`.

O teste de contexto inicializou H2 em memória, sem repositories JPA e sem migrações do projeto. Os testes existentes ainda não cobrem os casos de uso de cadastro e disponibilidade de e-mail. Os avisos observados estão descritos no README.

## Limites do estágio atual

Os modelos de vaga, candidatura, entrevista e histórico já existiam antes desta etapa. A entrega de hoje concentra-se nas regras e na estrutura de aplicação para usuários, além da correção pontual em entrevista.

Os gateways ainda são interfaces: consulta real de e-mail e gravação de usuários dependem de implementação. Controllers, configuração dos casos de uso no Spring, persistência, hash de senha, autenticação própria e tradução das exceções para respostas HTTP continuam pendentes. O enum de erros de PIN e o contador de tentativas ainda não formam um fluxo de autenticação.

## Mini post para LinkedIn

Hoje avancei no DevJobs, meu projeto em Java e Spring Boot para organizar candidaturas a vagas! 🚀

Implementei validações de nome, senha e confirmação, estruturei os casos de uso de cadastro e consulta de disponibilidade de e-mail com gateways e adicionei exceções e códigos de erro. Também incluí um teste unitário para rejeitar senhas curtas e corrigi o status inicial das entrevistas.

Os dois testes atuais passaram, e documentei a evolução com commits seguindo Conventional Commits, um por arquivo.

Próximo passo: conectar os casos de uso à persistência e expor os endpoints da API.

#Java #SpringBoot #Backend #TestesUnitarios #DevJobs
