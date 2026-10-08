package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.DeleteUserGateway;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.DeleteUserUseCase;
import br.costa.DevJobs.usecase.userusecase.IdAvailableUseCase;

public class DeleteUserUseCaseImpl  implements DeleteUserUseCase {

    private final DeleteUserGateway deleteUserGateway;
    private final IdAvailableUseCase idAvailableUseCase;

    public DeleteUserUseCaseImpl(DeleteUserGateway deleteUserGateway, IdAvailableUseCase idAvailableUseCase) {
        this.deleteUserGateway = deleteUserGateway;
        this.idAvailableUseCase = idAvailableUseCase;
    }

    @Override
    public void delete(Long id) {

        if(Boolean.FALSE.equals(idAvailableUseCase.idAvailable(id))){
            throw new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(), ErrorCodeEnum.IAI0001.getCode());
        }


        deleteUserGateway.deleteUser(id);
    }
}
