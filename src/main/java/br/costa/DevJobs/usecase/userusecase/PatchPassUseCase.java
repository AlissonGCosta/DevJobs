package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.application.command.users.PatchUserPassCommand;
import br.costa.DevJobs.core.domain.Users;

public interface PatchPassUseCase {

    void patchPass(PatchUserPassCommand userCommand, Long id);

}
