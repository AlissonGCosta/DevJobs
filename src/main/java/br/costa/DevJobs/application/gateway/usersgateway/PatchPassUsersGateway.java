package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

public interface PatchPassUsersGateway {
    void patchPass (String newPassword, Long id);
}
