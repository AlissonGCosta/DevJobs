package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.command.users.RegisterUserRequestCommand;
import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.application.result.users.RegisterUserResult;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.application.gateway.usersgateway.RegisterUserGateway;
import br.costa.DevJobs.core.exception.usersexception.EmailAvaliableException;
import br.costa.DevJobs.core.exception.usersexception.InternalServerErrorException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.EmailAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.RegisterAccountUseCase;

public class RegisterAccountUseCaseImpl implements RegisterAccountUseCase {

    private final RegisterUserGateway registerUserGateway;
    private final EmailAvailableUseCase emailAvailableUseCase;


    public RegisterAccountUseCaseImpl(RegisterUserGateway registerUserGateway,
                                      EmailAvailableUseCase emailAvailableUseCase
                                      ) {
        this.registerUserGateway = registerUserGateway;
        this.emailAvailableUseCase = emailAvailableUseCase;

    }


    @Override
    public RegisterUserResult create(RegisterUserRequestCommand userCommand) {

        Users user = new Users(
                userCommand.fullName(),
                userCommand.email(),
                userCommand.password(),
                userCommand.confirmPassword()
        );

        // validate for create account
        if(emailAvailableUseCase.emailAvailable(user.getEmail())) {
            throw new EmailAvaliableException(ErrorCodeEnum.EAA0001.getMessage(),  ErrorCodeEnum.EAA0001.getCode());
        }

        PostUserPersistence userPersistence = new PostUserPersistence(
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAttempt()
        );

        // response if created account
       return registerUserGateway.registerUser(userPersistence);

    }
}
