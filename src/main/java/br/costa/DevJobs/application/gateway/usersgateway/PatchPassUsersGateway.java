package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

public interface PatchPassUsersGateway {
    Boolean patchPass (Users users, Long id);
}
