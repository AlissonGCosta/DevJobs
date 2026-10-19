package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.RegisterUserGateway;
import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.application.result.users.RegisterUserResult;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRegisterService  implements RegisterUserGateway {

    private final UsersRepository usersRepository;
    private final CreateUserEntityService createUserEntityService;


    @Override
    public RegisterUserResult registerUser(PostUserPersistence postUserPersistence) {


            UsersEntity user = createUserEntityService.PostPersistenctoUsersEntity(postUserPersistence);

            usersRepository.save(user);


            return new  RegisterUserResult(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName()
            );

    }
}
