package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.application.persistence.user.UpdatePassUserPersistence;

public interface PatchPassUsersGateway {
    void patchPass (UpdatePassUserPersistence password, Long id);
}
