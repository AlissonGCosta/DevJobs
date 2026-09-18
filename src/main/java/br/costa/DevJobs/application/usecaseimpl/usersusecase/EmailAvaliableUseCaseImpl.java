package br.costa.DevJobs.application.usecaseimpl.usersusecase;

import br.costa.DevJobs.application.gateway.usersgateway.EmailAvaliableGateway;
import br.costa.DevJobs.usecase.userusecase.EmailAvaliableUseCase;

public class EmailAvaliableUseCaseImpl implements EmailAvaliableUseCase {

    private final EmailAvaliableGateway emailAvaliableGateway;

    public EmailAvaliableUseCaseImpl(EmailAvaliableGateway emailAvaliableGateway) {
        this.emailAvaliableGateway = emailAvaliableGateway;
    }

    @Override
    public Boolean emailAvaliable(String email) {
       return  emailAvaliableGateway.emailAvaliable(email);
    }
}
