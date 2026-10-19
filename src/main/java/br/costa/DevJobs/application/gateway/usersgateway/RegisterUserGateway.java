package br.costa.DevJobs.application.gateway.usersgateway;

import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.application.result.users.RegisterUserResult;
import br.costa.DevJobs.core.domain.Users;

public interface RegisterUserGateway {

   RegisterUserResult registerUser(PostUserPersistence postUserPersistence);
}
