package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.PutUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.EmailAvaliableException;

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
    public Users putUsers(Users users, Long id) {

        var actual = findByIdUsersUseCase.findById(id);


        if(emailAvailableUseCase.emailAvailable(users.getEmail())){
            throw new EmailAvaliableException(ErrorCodeEnum.EAA0001.getMessage(),
                    ErrorCodeEnum.EAA0001.getCode());
        }

        actual.setEmail(users.getEmail());
        actual.setFullName(users.getFullName());
        actual.setUpdatedAt(Instant.now());


        return putUsersGateway.putUsers(actual);
    }
}
