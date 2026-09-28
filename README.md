# DevJobs

Projeto de back-end para organizar vagas, candidaturas e entrevistas de emprego, desenvolvido com Java e Spring Boot.

**Estado documentado em 18/09/2026:** modelos de domínio e primeiros casos de uso de usuário implementados. A integração com persistência e a API de negócio ainda estão em desenvolvimento.

O [planejamento original](DevJobs-Projeto.md) descreve o produto desejado e os endpoints previstos. Este README registra o que existe no código. As entregas desta etapa e o texto para LinkedIn estão no [registro de 18/09/2026](docs/atualizacao-2026-09-18.md).

## Tecnologias presentes

| Tecnologia | Uso no estado atual |
| --- | --- |
| Java 21 | Versão configurada no `pom.xml`. |
| Spring Boot 4.1.0 | Inicialização e configuração automática da aplicação. |
| Maven Wrapper | Build e execução; distribuição configurada na versão 3.9.16. |
| Spring Web MVC | Dependência adicionada; controllers de negócio pendentes. |
| Spring Data JPA | Dependência adicionada; entidades de persistência e repositories pendentes. |
| Spring Security | Configuração automática padrão; autenticação própria e JWT pendentes. |
| Bean Validation | Dependência adicionada; as validações atuais de usuário são métodos do domínio. |
| H2 e PostgreSQL | Drivers disponíveis; o teste de contexto usa H2 em memória. Não há conexão PostgreSQL configurada. |
| Flyway | Dependência adicionada; ainda não existem scripts de migração. |
| Lombok | Dependência e processador de anotações configurados. |
| JUnit Jupiter | Teste de contexto e primeiro teste unitário do domínio. |

## Organização do código

As responsabilidades estão distribuídas entre domínio, contratos de casos de uso e aplicação. O domínio utiliza classes Java sem anotações de persistência.

```text
src/main/java/br/costa/DevJobs/
├── DevJobsApplication.java
├── core/
│   ├── domain/
│   │   ├── Users.java
│   │   ├── JobVacancy.java
│   │   ├── JobApplication.java
│   │   ├── Interview.java
│   │   ├── HistoryJobApplication.java
│   │   └── enumarated/
│   └── exception/
│       └── enums/ErrorCodeEnum.java
├── usecase/userusecase/
└── application/
    ├── gateway/usersgateway/
    └── usecaseimpl/usersusecase/
```

Os casos de uso recebem gateways pelo construtor. Esses contratos permitem conectar a lógica à infraestrutura posteriormente. Ainda não existem implementações dos gateways nem configuração desses casos de uso como beans do Spring.

## Modelagem já implementada

| Classe | Responsabilidade e comportamento atual |
| --- | --- |
| `Users` | Dados do usuário, senha e confirmação, perfil e contador de tentativas. O construtor valida nome e senha, atribui `ROLE_USER`, inicia `attempt` em zero e define `createdAt` e `updatedAt` com o mesmo instante. |
| `JobVacancy` | Título, empresa, descrição, modelo de trabalho, endereço, nível de experiência, tecnologia, salário, link, observação e usuário responsável. O construtor define `createdAt`; `updatedAt` ainda depende de atribuição explícita. |
| `JobApplication` | Dados da candidatura, canal utilizado, contato do recrutador, observação e usuário. Inicia com `APPLICATION_SUBMITTED` e datas de criação e atualização iguais. Ainda não possui referência a `JobVacancy`. |
| `Interview` | Data, horário, tipo, entrevistador, endereço/link e observação. Está vinculada a `JobApplication` e inicia com `SituationInterview.SCHEDULED`. |
| `HistoryJobApplication` | Estrutura para registrar eventos, valores anterior e novo, descrição, data, hora e candidatura relacionada. A geração automática desse histórico ainda não foi implementada. |

Os enums representam perfis de usuário, modelo de trabalho, nível de experiência, status de candidatura, tipo e situação da entrevista e eventos de histórico.

Os status disponíveis de candidatura são `APPLICATION_SUBMITTED`, `UNDER_REVIEW`, `INTERVIEW_SCHEDULED`, `INTERVIEW_COMPLETED`, `OFFER_RECEIVED`, `APPROVED`, `REJECTED` e `WITHDRAWN`. A existência desses valores ainda não inclui regras para transições entre eles.

## Validações de usuário

As regras abaixo são executadas no construtor de `Users` e nos setters correspondentes.

| Campo | Regra implementada | Erro |
| --- | --- | --- |
| Nome completo | Entre 11 e 100 caracteres, inclusive. | `BadRequestException`, código `BRN0001`. |
| Senha | Entre 15 e 64 caracteres, inclusive. | `BadRequestException`, código `IPN0002`. |
| Confirmação de senha | Deve ser igual à senha atual do objeto. | `BadRequestException`, código `BRN0003`. |

O construtor atual é `Users(Long id, String password, String confirmPassword, String email, String fullName)`. A inclusão de `confirmPassword` altera a assinatura anterior de quatro argumentos.

Essas validações ainda não tratam valores nulos, formato de e-mail ou normalização de espaços. Alterar a senha pelo setter não revalida automaticamente a confirmação já armazenada. O contador `attempt` possui valor inicial e acesso por getter/setter; não existe fluxo de login ou bloqueio associado. Também não há implementação de hash de senha nesta etapa.

## Casos de uso de usuário

### Consulta de disponibilidade de e-mail

`EmailAvaliableUseCase.emailAvaliable(String email)` é implementado por `EmailAvaliableUseCaseImpl`, que delega a consulta a `EmailAvailableGateway` e devolve seu resultado.

Os nomes das classes e métodos acima correspondem à grafia existente no código.

### Cadastro de conta

`RegisterAcountUseCase.create(Users user)` é implementado por `RegisterAccountUseCaseImpl`:

1. Consulta `EmailAvailableGateway` com o e-mail do usuário.
2. Se o gateway retornar `false`, lança `ConflictException` com código `CML0001` e interrompe o cadastro.
3. Se houver disponibilidade, chama `RegisterUseGateway.registerUser(user)`.
4. Se o cadastro retornar `false`, lança `InternalServerErrorException` com código `ISE0001`.
5. Quando o cadastro retorna `true`, conclui sem valor de retorno (`void`).

O fluxo depende dos contratos dos gateways. A gravação em banco e a consulta real de e-mail ainda precisam de adaptadores. Os retornos usam `Boolean` e não têm tratamento explícito para `null`.

## Exceções e códigos de erro

Foram criadas `BadRequestException`, `ConflictException`, `InternalServerErrorException` e `NotFoundException`, todas derivadas de `RuntimeException`, com mensagem e campo interno de código. Ainda não existe um handler que converta essas exceções em respostas HTTP.

| Constante de `ErrorCodeEnum` | Significado | Uso atual |
| --- | --- | --- |
| `BRN0001` | Nome inválido. | Validação de `Users`. |
| `IPN0002` | Senha inválida. | Validação de `Users`. |
| `BRN0003` | Senha e confirmação diferentes. | Validação de `Users`. |
| `CML0001` | E-mail já existente. | Caso de uso de cadastro. |
| `ISE0001` | Falha ao criar a conta. | Caso de uso de cadastro. |
| `PIN0002` | PIN incorreto, com quantidade de tentativas na mensagem. | Apenas código e formatador definidos; fluxo de PIN pendente. O valor retornado por `getCode()` é `PIN-0002`. |

`NotFoundException` está disponível, mas ainda não é utilizada por um caso de uso.

## Como executar

Pré-requisito: JDK 21 instalado e `JAVA_HOME` apontando para ele. O wrapper baixa o Maven e as dependências quando necessário.

No Windows, a partir da raiz do projeto:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw test
./mvnw spring-boot:run
```

O arquivo `src/main/resources/application.yaml` define apenas o nome `DevJobs`. O teste de contexto inicializa H2 em memória e usa a segurança padrão do Spring. Iniciar a aplicação ainda não disponibiliza os endpoints planejados de cadastro, login ou candidaturas.

## Testes e verificação

| Teste existente | O que verifica |
| --- | --- |
| `DevJobsApplicationTests.contextLoads` | Inicialização do contexto Spring com a configuração atual. |
| `UsersTest.shouldRejectPasswithminor15Characters` | Uma senha de seis caracteres é rejeitada com `BadRequestException`. |

Em **18/09/2026**, `mvnw.cmd test` foi executado com **Temurin 21.0.12** e terminou com **BUILD SUCCESS: 2 testes, 0 falhas, 0 erros e 0 ignorados**.

A execução apresentou avisos sobre a versão do H2 em relação à versão verificada pelo Flyway, ausência de migrações, `open-in-view` e carregamento dinâmico do agente do Mockito. Esses avisos não impediram os testes. A cobertura atual ainda não verifica os demais limites das validações, os fluxos de cadastro ou a persistência.

## Evolução até aqui

| Data dos commits | Entregas |
| --- | --- |
| 20/08/2026 | Estrutura inicial Spring Boot, dependências, Maven Wrapper, configuração da aplicação e teste de contexto. |
| 13/09/2026 | Planejamento do produto, modelos de usuário, vaga, candidatura e entrevista, além dos primeiros enums. |
| 14/09/2026 | Perfis de usuário, status de candidatura, modelo de histórico, vínculo entre entrevista e candidatura e reorganização dos enums. |
| 18/09/2026 | Validações de usuário, confirmação de senha, contador inicial de tentativas, contratos e implementações dos primeiros casos de uso, gateways, exceções, códigos de erro, primeiro teste unitário do domínio e correção da constante `SCHEDULED`. Documentação do estado atual. |

## Próximas etapas do projeto

- Implementar os adaptadores dos gateways, entidades de persistência, repositories e migrações.
- Configurar PostgreSQL e a composição dos casos de uso no Spring.
- Expor controllers e DTOs, com tratamento centralizado de erros HTTP.
- Completar validações e testes dos caminhos de sucesso, conflito, falha e valores de limite.
- Implementar hash de senha e autenticação, incluindo o fluxo de JWT previsto no planejamento.
- Desenvolver o CRUD de candidaturas, mudanças de status e geração de histórico.
- Adicionar documentação OpenAPI, interface, dashboard e demais funcionalidades previstas no escopo.

## Convenção dos commits

Esta entrega utiliza Conventional Commits no formato `tipo(escopo): descrição`, com um arquivo por commit. A alteração incompatível do construtor de `Users` está indicada por `!` e pelo rodapé `BREAKING CHANGE`.
