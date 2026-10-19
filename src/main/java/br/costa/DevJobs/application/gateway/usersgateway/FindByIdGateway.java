package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.application.result.users.UserDetailsResult;
import br.costa.DevJobs.core.domain.Users;

public interface FindByIdGateway {

    UserDetailsResult findById(Long id);
}
