package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.PassAvailableGatway;
import br.costa.DevJobs.usecase.userusecase.PassAvailableUseCase;

public class PassAvilableUseCaseImpl implements PassAvailableUseCase {

    private final PassAvailableGatway passAvailableGatway;

    public PassAvilableUseCaseImpl(PassAvailableGatway passAvailableGatway) {
        this.passAvailableGatway = passAvailableGatway;
    }

    @Override
    public void availablePass(String oldPassword, Long id) {

        passAvailableGatway.availablePass(oldPassword, id);
    }
}
