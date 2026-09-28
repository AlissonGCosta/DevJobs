package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

public interface FindByIdGateway {

    Users findById(Long id);
}
