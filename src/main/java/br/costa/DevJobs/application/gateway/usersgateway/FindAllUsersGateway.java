package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.core.domain.Users;

import java.util.List;

public interface FindAllUsersGateway {

    List<Users> findAll();
}
