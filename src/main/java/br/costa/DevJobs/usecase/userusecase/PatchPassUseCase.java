package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

public interface PatchPassUseCase {

    void patchPass(Users users, Long id);

}
