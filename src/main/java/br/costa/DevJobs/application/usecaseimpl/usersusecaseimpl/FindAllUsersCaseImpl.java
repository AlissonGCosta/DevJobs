package br.costa.DevJobs.application.usecaseimpl.usersusecaseimpl;

import br.costa.DevJobs.application.gateway.usersgateway.FindAllUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.usecase.userusecase.FindAllUsersUseCase;

import java.util.List;

public class FindAllUsersCaseImpl implements FindAllUsersUseCase {

    private final FindAllUsersGateway findAllUsersGateway;

    public FindAllUsersCaseImpl(FindAllUsersGateway findAllUsersGateway) {
        this.findAllUsersGateway = findAllUsersGateway;
    }

    @Override
    public List<Users> findAll() {
        return findAllUsersGateway.findAll();
    }
}
