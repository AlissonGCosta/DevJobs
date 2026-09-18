package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

import java.util.Optional;

public interface RegisterUseGateway {

    Boolean registerUser(Users user);
}
