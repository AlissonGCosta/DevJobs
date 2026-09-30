package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.FindByIdGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import br.costa.DevJobs.usecase.userusecase.IdAvailableUseCase;


public class FindByIdUsersUseCaseImpl implements FindByIdUsersUseCase {

    private final FindByIdGateway findByIdGateway;
    private final IdAvailableUseCase idAvailableUseCase;

    public FindByIdUsersUseCaseImpl(FindByIdGateway findByIdGateway, IdAvailableUseCase idAvailableUseCase) {
        this.findByIdGateway = findByIdGateway;
        this.idAvailableUseCase = idAvailableUseCase;
    }

    @Override
    public Users findById(Long id) {

        if(!idAvailableUseCase.idAvailable(id)) {
            throw new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(),  ErrorCodeEnum.IAI0001.getCode());
        }

        return findByIdGateway.findById(id);

    }
}
