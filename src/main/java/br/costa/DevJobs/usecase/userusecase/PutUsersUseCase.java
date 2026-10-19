package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.application.command.users.UpdateUserCommand;
import br.costa.DevJobs.application.result.users.UpdateUserResult;


public interface PutUsersUseCase {

    UpdateUserResult putUsers(UpdateUserCommand userCommand, Long id);

}
