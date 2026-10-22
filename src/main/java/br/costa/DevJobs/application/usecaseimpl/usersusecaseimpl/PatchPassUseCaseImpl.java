package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.command.users.PatchUserPassCommand;
import br.costa.DevJobs.application.gateway.usersgateway.PatchPassUsersGateway;
import br.costa.DevJobs.application.persistence.user.UpdatePassUserPersistence;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.usersexception.PassAvailableException;
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
    public void patchPass(PatchUserPassCommand userPassCommand, Long id) {

        if(Boolean.FALSE.equals(idAvailableUseCase.idAvailable(id))){
            throw new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(), ErrorCodeEnum.IAI0001.getCode());
        }

        passAvailableUseCase.availablePass(userPassCommand.oldPassword(), id);

        if(!userPassCommand.newPassword().equals(userPassCommand.confirmNewPassword())){
            throw new PassAvailableException(ErrorCodeEnum.PSE0001.getMessage(), ErrorCodeEnum.PSE0001.getCode());
        }

        Users user = new Users(userPassCommand.newPassword());

        UpdatePassUserPersistence persistUserPass = new UpdatePassUserPersistence(
                user.getPassword()
        );

        patchPassUsersGateway.patchPass(persistUserPass,  id);
    }
}
