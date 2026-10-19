package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.application.persistence.user.UpdateUserPersistence;
import br.costa.DevJobs.application.result.users.UpdateUserResult;


public interface PutUsersGateway {

    UpdateUserResult putUsers(UpdateUserPersistence users, Long id);
}
