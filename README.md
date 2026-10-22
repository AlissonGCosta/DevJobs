# DevJobs

Back-end em Java e Spring Boot para organizar vagas de emprego, candidaturas e entrevistas.

**Estado em 22/10/2026:** a API de usuários possui cadastro, consulta, listagem, atualização de perfil, troca de senha e exclusão. A atualização de perfil teve os campos nome/e-mail e a data de modificação corrigidos, e sua resposta inclui `roles`. O PATCH aplica a regra de tamanho de senha do domínio e usa um record próprio de persistência. A compilação passou; dos 107 testes executados, 98 passaram e 9 falharam. As pendências estão detalhadas abaixo.

O [planejamento original](DevJobs-Projeto.md) descreve o produto desejado. O [relatório de 22/10/2026](docs/atualizacao-2026-10-22.md) apresenta as mudanças desta etapa, as nove falhas de teste e os commits por arquivo. Etapas anteriores disponíveis: [18/09](docs/atualizacao-2026-09-18.md), [30/09](docs/atualizacao-2026-09-30.md), [04/10](docs/atualizacao-2026-10-04.md) e [08/10](docs/atualizacao-2026-10-08.md).

## Tecnologias

| Tecnologia | Uso atual |
| --- | --- |
| Java 21 | Versão de compilação definida no `pom.xml`. |
| Spring Boot 4.1.0 | Inicialização e configuração da aplicação. |
| Maven Wrapper 3.9.16 | Build, testes e execução. |
| Spring Web MVC | API REST de usuários. |
| Spring Data JPA | Entidade de usuários, repository e persistência. |
| Spring Security | Configuração permissiva de desenvolvimento e BCrypt. |
| Bean Validation | `@Valid` no controller, `@NotNull` nos DTOs de entrada e `@Email` no cadastro. |
| H2 | Banco em memória configurado para desenvolvimento. |
| PostgreSQL e Flyway | Dependências presentes; conexão PostgreSQL e migrações ainda pendentes. Flyway desabilitado localmente. |
| Lombok | Construtores, getters e setters da infraestrutura. |
| JUnit Jupiter e Mockito | 107 execuções de testes de domínio, casos de uso, mappers, serviços, contexto Spring e API com MockMvc/H2. |

## Execução local

Use JDK 21 ou superior compatível com o projeto. O Maven compila com `release 21`; a verificação desta entrega usou Java 25.0.2. Configure `JAVA_HOME` ou disponibilize `java` no `PATH`.

No Windows, na raiz do projeto:

```powershell
.\mvnw.cmd test -B -ntp
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw test -B -ntp
./mvnw spring-boot:run
```

O wrapper baixa o Maven e as dependências quando necessário. A aplicação usa a porta padrão `8080`, sem personalização no projeto.

| Configuração local | Valor |
| --- | --- |
| JDBC | `jdbc:h2:mem:devjobs;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` |
| Usuário / senha | `sa` / vazia |
| Console H2 | `http://localhost:8080/h2-console` |
| Hibernate | `ddl-auto: update` |
| Flyway | `enabled: false` |

Os dados do H2 não sobrevivem ao encerramento do processo. A configuração Spring Security permite todas as requisições e desabilita CSRF. Autenticação própria, JWT e autorização por usuário continuam pendentes.

## Organização do código

```text
src/main/java/br/costa/DevJobs/
|-- DevJobsApplication.java
|-- core/
|   |-- domain/
|   |   `-- enumerated/
|   `-- exception/
|       |-- enums/
|       `-- usersexception/
|-- usecase/userusecase/
|-- application/
|   |-- command/users/
|   |-- persistence/user/
|   |-- result/users/
|   |-- gateway/usersgateway/
|   `-- usecaseimpl/usersusecaseimpl/
`-- infrastructure/
    |-- Entity/
    |-- config/
    |-- controller/
    |-- dto/
    |   |-- request/userrequest/
    |   `-- response/userresponse/
    |-- exception/
    |-- mappers/
    |-- persistence/
    `-- service/userservice/
```

O fluxo de escrita segue DTO de entrada -> `UserMapper` -> comando -> caso de uso -> modelo de persistência -> gateway -> serviço JPA. As leituras e os retornos das escritas usam resultados da aplicação, convertidos para DTOs de resposta. A troca de senha usa um comando e um gateway sem retorno; a exclusão continua recebendo apenas o ID.

| Grupo | Records presentes |
| --- | --- |
| Comandos | `RegisterUserRequestCommand`, `UpdateUserCommand`, `PatchUserPassCommand`. |
| Persistência | `PostUserPersistence`, `UpdateUserPersistence`, `UpdatePassUserPersistence`. |
| Resultados | `RegisterUserResult`, `UpdateUserResult`, `UserDetailsResult`, `FindAllUsersResult`. |

O domínio permanece sem anotações JPA. `UsersUseCaseConfig` compõe os casos de uso com gateways. `CreateUserEntityService` transforma os dados de cadastro em `UsersEntity` e gera o hash BCrypt da senha. `confirmPassword` continua na entrada e no domínio para validação, mas não está em `PostUserPersistence` nem em `UsersEntity`.

A listagem do controller chama `FindAllUsersUseCase` e mapeia `FindAllUsersResult` para `FindAllResponseDto`. O componente intermediário `FindAllService` foi removido, assim como suas referências no controller. A camada `usecase` importa os records de `application`; essa é a dependência presente no código atual.

Os modelos de vaga (`JobVacancy`), candidatura (`JobApplication`), entrevista (`Interview`) e histórico (`HistoryJobApplication`) estão presentes no domínio. Seus fluxos completos de API e persistência permanecem pendentes.

## API de usuários

Base: `http://localhost:8080/v1/users`. Os contratos abaixo vêm de [UsersController](src/main/java/br/costa/DevJobs/infrastructure/controller/UsersController.java). Os fluxos foram exercitados por MockMvc com H2 isolado, sem servidor HTTP externo; seis das 46 execuções de integração falharam por validação ou formato da resposta de erro.

| Método e rota | Entrada | Sucesso declarado |
| --- | --- | --- |
| `POST /v1/users` | `fullName`, `email`, `password`, `confirmPassword` | `201`: `id`, `fullName`, `email`. |
| `GET /v1/users/{id}` | ID no caminho | `200`: `id`, `fullName`, `email`, `createdAt`, `role`. |
| `GET /v1/users` | Sem corpo | `200`: lista com os mesmos campos da consulta por ID, sem paginação. |
| `PUT /v1/users/{id}` | ID; `fullName`, `email` no corpo | `200`: `id`, `fullName`, `email`, `updatedAt`, `roles`. |
| `PATCH /v1/users/{id}` | ID; `oldPassword`, `newPassword`, `confirmNewPassword` no corpo | `200`, sem corpo. |
| `DELETE /v1/users/{id}` | ID no caminho, sem corpo | `204`, sem corpo. |

### Cadastro

```json
{
  "fullName": "Pessoa Exemplo",
  "email": "pessoa@example.com",
  "password": "senha-exemplo-123",
  "confirmPassword": "senha-exemplo-123"
}
```

`RegisterAccountUseCaseImpl` constrói `Users` para validar nome, senha e confirmação, verifica se o e-mail já existe e cria `PostUserPersistence`. O gateway retorna `RegisterUserResult` com o ID atribuído à entidade e os dados do cadastro. A resposta passa a vir do resultado da persistência. O nome `emailAvailable` corresponde a uma consulta `existsByEmail`: `true` indica e-mail já cadastrado.

`UserRegisterService` não captura mais qualquer exceção para retornar `false`. Falhas de persistência se propagam; o caso de uso não converte um retorno booleano em `InternalServerErrorException`.

### Atualização de perfil

```json
{
  "fullName": "Pessoa Atualizada",
  "email": "pessoa.atualizada@example.com"
}
```

O caso de uso consulta o usuário, reconstrói o domínio e valida o nome pelo setter. A verificação de duplicidade só ocorre quando o novo e-mail difere do atual, permitindo passar pela verificação com o próprio e-mail. `PutUsersService` altera a entidade em método transacional e atualiza `updatedAt`.

`UpdateUserPersistence` e `UpdateUserResult` agora recebem `fullName` antes de `email`, acompanhando o caso de uso e o mapper. A resposta preserva os campos e usa `updatedAt` da entidade; inclui o papel no campo JSON `roles`. Os cenários de integração de atualização válida passaram. Permanecem dois testes unitários com dados montados na ordem antiga, além da ausência de validação de formato de e-mail no PUT, que aceita os três valores inválidos testados.

### Troca de senha

```json
{
  "oldPassword": "senha-exemplo-123",
  "newPassword": "nova-senha-456",
  "confirmNewPassword": "nova-senha-456"
}
```

1. O controller valida os campos não nulos e cria `PatchUserPassCommand`.
2. O caso de uso verifica a existência do ID e confere a senha antiga contra o hash persistido por BCrypt.
3. O caso de uso compara `newPassword` e `confirmNewPassword`; divergência lança `PassAvailableException`, tratada como HTTP `409`.
4. O caso de uso constrói `Users` com a nova senha e aplica o limite de 8 a 64 caracteres antes de criar `UpdatePassUserPersistence`.
5. O gateway recebe esse record e o ID. O serviço carrega a entidade, grava um hash BCrypt, atualiza `updatedAt` e salva.

O serviço não captura mais qualquer exceção para retornar `false`; falhas interrompem o fluxo. A confirmação não é persistida. Os testes de casos de uso e integração cobrem a rejeição de senhas de 0, 7 e 65 caracteres. O serviço ainda não declara uma transação que englobe a consulta e a gravação.

### Exclusão

`DeleteUserUseCaseImpl` verifica a existência do ID e delega ao gateway. `DeleteUserService` executa `deleteById` em método transacional. ID ausente lança `IdFoundAvailableException`, tratada como HTTP `404`.

## Validações e erros

| Regra de domínio | Uso atual | Exceção |
| --- | --- | --- |
| Nome entre 6 e 100 caracteres | Cadastro e setter usado na atualização. | `ValidateFullNameException`, código `VPN0001`. |
| Senha entre 8 e 64 caracteres | Cadastro e PATCH. | `InvalidPasswordException`, código `IPN0001`. |
| Confirmação igual à senha | Cadastro pelo domínio; PATCH por comparação no caso de uso. | `PassAvailableException`: código `IPN0002` no cadastro e `PSS0001` no PATCH. |

Os DTOs de entrada exigem campos não nulos. O cadastro também usa `@Email`, mas ainda aceita e-mail vazio; o PUT não valida o formato. Não há `@NotBlank` nem normalização de espaços, e um nome com seis espaços é aceito. `GlobalExceptionHandler` produz `timestamp`, `status`, `erro`, `message`, `path` e `errors`; os handlers atuais deixam `errors` nulo e não expõem o código da exceção.

| Exceção tratada | HTTP |
| --- | --- |
| `IdFoundAvailableException` | `404` |
| `PassAvailableException` | `409` |
| `EmailAvaliableException` | `409` |
| `InternalServerErrorException` | `500` |
| `InvalidPasswordException` | `400` |
| `ValidateFullNameException` | `400` |

O handler de `InternalServerErrorException` permanece, embora o cadastro não lance mais essa exceção a partir de um booleano. Exceções de persistência propagadas não têm tratamento específico nesse handler. As respostas de Bean Validation também não são padronizadas por ele.

## Verificação em 22/10/2026

Comando final: `.\mvnw.cmd clean test -B -ntp`, com Java **25.0.2**, Maven **3.9.16** e compilação em **release 21**.

**Resultado: BUILD FAILURE na etapa de testes; 107 executados, 9 falhas, 0 erros e 0 ignorados.** A compilação dos 84 arquivos principais e dos sete arquivos de teste passou. `DevJobsApplicationTests.contextLoads` passou com H2.

As nove falhas são: uma expectativa antiga de exceção em `UsersTest`; dois testes unitários que constroem records com nome/e-mail na ordem antiga; três entradas de e-mail inválido aceitas no PUT; e-mail vazio aceito no cadastro; nome com espaços aceito no cadastro; e ausência do envelope próprio nos erros de Bean Validation. O [relatório desta etapa](docs/atualizacao-2026-10-22.md) apresenta os métodos e resultados por classe. Os relatórios brutos estão em `target/surefire-reports`, ignorado pelo Git.

Foram acrescentadas cinco classes de teste, com 100 novas execuções. A integração usa MockMvc e um banco H2 em memória exclusivo dos testes da API, com limpeza antes de cada caso. Os arquivos Java recebidos foram preservados durante a organização dos commits; as falhas encontradas ficam registradas para correção posterior.

## Próximas etapas

- Atualizar os dados dos testes unitários para a nova ordem `fullName, email` dos records.
- Alinhar o teste de confirmação divergente ao contrato de exceção definido pelo domínio.
- Rejeitar e-mail vazio no cadastro, e-mail inválido no PUT e nome composto apenas por espaços.
- Revisar os limites transacionais do fluxo de troca de senha.
- Padronizar as respostas de Bean Validation e de falhas de persistência.
- Revisar os componentes e imports sem uso após a refatoração.
- Implementar autenticação, autorização, PostgreSQL, migrações e paginação.
- Desenvolver os fluxos de vagas, candidaturas e entrevistas.

## Convenção dos commits

As mensagens seguem Conventional Commits no formato `tipo(escopo): descrição`, com um commit dedicado para cada arquivo alterado ou criado, inclusive documentação. Esta etapa reúne 15 caminhos Java, este README e um novo relatório: **17 commits para 17 arquivos**. A relação está no [relatório de 22/10/2026](docs/atualizacao-2026-10-22.md).
