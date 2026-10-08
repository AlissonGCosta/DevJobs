package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.RegisterUserGateway;
import br.costa.DevJobs.core.domain.Users;
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
    public Boolean registerUser(Users user) {
        try{
            usersRepository.save(createUserEntityService.userDomaintoUsersEntity(user));
            return true;
        }catch(Exception e){
            return  false;
        }
    }
}
