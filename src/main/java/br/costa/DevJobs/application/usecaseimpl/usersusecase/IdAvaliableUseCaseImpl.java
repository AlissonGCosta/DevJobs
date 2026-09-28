package br.costa.DevJobs.application.usecaseimpl.usersusecase;

import br.costa.DevJobs.application.gateway.usersgateway.IdAvailableGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.usecase.userusecase.IdAvailableUseCase;

public class IdAvaliableUseCaseImpl implements IdAvailableUseCase {

    private final IdAvailableGateway idAvailableGateway;

    public IdAvaliableUseCaseImpl(IdAvailableGateway idAvailableGateway) {
        this.idAvailableGateway = idAvailableGateway;
    }

    @Override
    public Boolean idAvailable(Long id) {
        return idAvailableGateway.idAvailable(id);
    }
}
