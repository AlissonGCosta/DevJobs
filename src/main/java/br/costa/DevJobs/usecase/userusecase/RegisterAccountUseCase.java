package br.costa.DevJobs.usecase.userusecase;


import br.costa.DevJobs.application.command.users.RegisterUserRequestCommand;
import br.costa.DevJobs.application.result.users.RegisterUserResult;

public interface RegisterAccountUseCase {
   RegisterUserResult create(RegisterUserRequestCommand userCommand);
}
