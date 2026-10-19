package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.application.result.users.UserDetailsResult;
import br.costa.DevJobs.core.domain.Users;

import java.util.Optional;

public interface FindByIdUsersUseCase {

    UserDetailsResult findById(Long id);

}
