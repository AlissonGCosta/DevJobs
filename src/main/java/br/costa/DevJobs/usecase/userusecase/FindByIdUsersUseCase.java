package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

import java.util.Optional;

public interface FindByIdUsersUseCase {

    Users findById(Long id);

}
