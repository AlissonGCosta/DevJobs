package br.costa.DevJobs.application.usecaseimpl.usersusecase;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.application.gateway.usersgateway.RegisterUserGateway;
import br.costa.DevJobs.core.exception.EmailAvaliableException;
import br.costa.DevJobs.core.exception.InternalServerErrorException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.EmailAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.RegisterAccountUseCase;

public class RegisterAccountUseCaseImpl implements RegisterAccountUseCase {

    private final RegisterUserGateway registerUserGateway;
    private final EmailAvailableUseCase emailAvailableUseCase;

    public RegisterAccountUseCaseImpl(RegisterUserGateway registerUserGateway, EmailAvailableUseCase emailAvailableUseCase) {
        this.registerUserGateway = registerUserGateway;
        this.emailAvailableUseCase = emailAvailableUseCase;

    }


    @Override
    public void create(Users user) {

        // validate for create account
        if(emailAvailableUseCase.emailAvailable(user.getEmail())) {
            throw new EmailAvaliableException(ErrorCodeEnum.EAA0001.getMessage(),  ErrorCodeEnum.EAA0001.getCode());
        }

        // response if created account
        if(!registerUserGateway.registerUser(user)) {
            throw new InternalServerErrorException(ErrorCodeEnum.ISE0001.getMessage(), ErrorCodeEnum.ISE0001.getCode());
        }
    }
}
