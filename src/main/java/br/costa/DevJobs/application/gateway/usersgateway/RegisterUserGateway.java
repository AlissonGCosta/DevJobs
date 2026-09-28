package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

public interface RegisterUserGateway {

    Boolean registerUser(Users user);
}
