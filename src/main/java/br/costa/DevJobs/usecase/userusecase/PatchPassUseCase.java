package br.costa.DevJobs.usecase.userusecase;

import br.costa.DevJobs.core.domain.Users;

public interface PatchPassUseCase {

    void patchPass(String oldPassword, String newPassword, String confirmNewPassword, Long id);

}
