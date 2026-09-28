package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

public interface EmailAvailableUseCase {

    Boolean emailAvailable(String email);
}
