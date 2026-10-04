package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.PatchPassUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.PassAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.PatchPassUseCase;

public class PatchPassUseCaseImpl implements PatchPassUseCase {

    private final FindByIdUsersUseCase findByIdUsersUseCase;
    private final PatchPassUsersGateway patchPassUsersGateway;
    private final PassAvailableUseCase passAvailableUseCase;

    public PatchPassUseCaseImpl(FindByIdUsersUseCase findByIdUsersUseCase, PatchPassUsersGateway patchPassUsersGateway, PassAvailableUseCase passAvailableUseCase) {
        this.findByIdUsersUseCase = findByIdUsersUseCase;
        this.patchPassUsersGateway = patchPassUsersGateway;
        this.passAvailableUseCase = passAvailableUseCase;
    }

    @Override
    public void patchPass(Users users, Long id) {

       var userPass = findByIdUsersUseCase.findById(id);

       passAvailableUseCase.availablePass(users.getOldPassword(), id);

       userPass.setPassword(users.getPassword());
       userPass.setConfirmPassword(users.getConfirmPassword());

        patchPassUsersGateway.patchPass(userPass, id);
    }
}
