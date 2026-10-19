package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.command.users.UpdateUserCommand;
import br.costa.DevJobs.application.gateway.usersgateway.PutUsersGateway;
import br.costa.DevJobs.application.persistence.user.UpdateUserPersistence;
import br.costa.DevJobs.application.result.users.UpdateUserResult;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.usersexception.EmailAvaliableException;

import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.EmailAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.PutUsersUseCase;

import java.time.Instant;

public class PutUsersUseCaseImpl implements PutUsersUseCase {

    private final PutUsersGateway putUsersGateway;
    private final EmailAvailableUseCase emailAvailableUseCase;
    private final FindByIdUsersUseCase findByIdUsersUseCase;

    public PutUsersUseCaseImpl(PutUsersGateway putUsersGateway,
                               EmailAvailableUseCase emailAvailableUseCase,
                               FindByIdUsersUseCase findByIdUsersUseCase) {
        this.putUsersGateway = putUsersGateway;
        this.emailAvailableUseCase = emailAvailableUseCase;
        this.findByIdUsersUseCase = findByIdUsersUseCase;
    }

    @Override
    public UpdateUserResult putUsers(UpdateUserCommand userCommand, Long id) {

        var actual = findByIdUsersUseCase.findById(id);

        Users users = new Users(
                actual.id(),
                actual.fullName(),
                actual.email(),
                actual.createdAt(),
                actual.role()
        );

        if(!users.getEmail().equals(userCommand.email())){

                  if(emailAvailableUseCase.emailAvailable(userCommand.email()))  {
                      throw new EmailAvaliableException(ErrorCodeEnum.EAA0001.getMessage(),
                              ErrorCodeEnum.EAA0001.getCode());
                  }

            }




        users.setEmail(userCommand.email());
        users.setFullName(userCommand.fullName());

        UpdateUserPersistence persistence = new UpdateUserPersistence(
                users.getFullName(),
                users.getEmail()
        );


        return putUsersGateway.putUsers(persistence, id);
    }
}
