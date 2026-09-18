package br.costa.DevJobs.application.usecaseimpl.usersusecase;

import br.costa.DevJobs.application.gateway.usersgateway.EmailAvaliableGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.application.gateway.usersgateway.RegisterUseGateway;
import br.costa.DevJobs.core.exception.ConflictException;
import br.costa.DevJobs.core.exception.InternalServerErrorException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.RegisterAcountUseCase;

public class RegisterAcountUseCaseImpl implements RegisterAcountUseCase {

    private final RegisterUseGateway registerUseGateway;
    private final EmailAvaliableGateway emailAvaliableGateway;

    public RegisterAcountUseCaseImpl(RegisterUseGateway registerUseGateway, EmailAvaliableGateway emailAvaliableGateway) {
        this.registerUseGateway = registerUseGateway;
        this.emailAvaliableGateway = emailAvaliableGateway;
    }


    @Override
    public void create(Users user) {

        if(!emailAvaliableGateway.emailAvaliable(user.getEmail())) {
            throw new ConflictException(ErrorCodeEnum.CML0001.getMessage(),  ErrorCodeEnum.CML0001.getCode());
        }

        if(!registerUseGateway.registerUser(user)) {
            throw new InternalServerErrorException(ErrorCodeEnum.ISE0001.getMessage(), ErrorCodeEnum.ISE0001.getCode());
        }
    }
}
