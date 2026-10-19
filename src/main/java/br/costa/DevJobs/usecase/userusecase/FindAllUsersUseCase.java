package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.application.result.users.FindAllUsersResult;
import br.costa.DevJobs.core.domain.Users;

import java.util.List;

public interface FindAllUsersUseCase {
    List<FindAllUsersResult> findAll();
}
