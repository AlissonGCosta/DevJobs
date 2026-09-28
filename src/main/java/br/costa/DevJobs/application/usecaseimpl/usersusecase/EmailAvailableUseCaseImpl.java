package br.costa.DevJobs.application.usecaseimpl.usersusecase;

import br.costa.DevJobs.application.gateway.usersgateway.EmailAvailableGateway;
import br.costa.DevJobs.usecase.userusecase.EmailAvailableUseCase;


public class EmailAvailableUseCaseImpl implements EmailAvailableUseCase {

    private final EmailAvailableGateway emailAvailableGateway;

    public EmailAvailableUseCaseImpl(EmailAvailableGateway emailAvailableGateway) {
        this.emailAvailableGateway = emailAvailableGateway;
    }

    @Override
    public Boolean emailAvailable(String email) {
        // return available email or not
       return  emailAvailableGateway.emailAvaliable(email);
    }
}
