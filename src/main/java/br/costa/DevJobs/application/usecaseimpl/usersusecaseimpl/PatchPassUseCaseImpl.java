package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.PatchPassUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.IdAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.PassAvailableUseCase;
import br.costa.DevJobs.usecase.userusecase.PatchPassUseCase;

public class PatchPassUseCaseImpl implements PatchPassUseCase {

    private final IdAvailableUseCase  idAvailableUseCase;
    private final PatchPassUsersGateway patchPassUsersGateway;
    private final PassAvailableUseCase passAvailableUseCase;

    public PatchPassUseCaseImpl(IdAvailableUseCase idAvailableUseCase, PatchPassUsersGateway patchPassUsersGateway, PassAvailableUseCase passAvailableUseCase) {
        this.idAvailableUseCase = idAvailableUseCase;
        this.patchPassUsersGateway = patchPassUsersGateway;
        this.passAvailableUseCase = passAvailableUseCase;
    }

    @Override
    public void patchPass(String oldPassword, String newPassword, String confirmNewPassword, Long id) {

        if(Boolean.FALSE.equals(idAvailableUseCase.idAvailable(id))){
            throw new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(), ErrorCodeEnum.IAI0001.getCode());
        }

        passAvailableUseCase.availablePass(oldPassword, id);


        patchPassUsersGateway.patchPass(newPassword,confirmNewPassword,  id);
    }
}
