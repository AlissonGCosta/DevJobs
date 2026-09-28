package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

public interface IdAvailableUseCase {
    Boolean idAvailable(Long id);
}
