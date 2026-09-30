package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

public interface PutUsersUseCase {

    Users putUsers(Users users, Long id);

}
