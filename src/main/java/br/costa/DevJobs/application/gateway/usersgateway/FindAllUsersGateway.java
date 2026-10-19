package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.application.result.users.FindAllUsersResult;
import br.costa.DevJobs.core.domain.Users;

import java.util.List;

public interface FindAllUsersGateway {

    List<FindAllUsersResult> findAll();
}
