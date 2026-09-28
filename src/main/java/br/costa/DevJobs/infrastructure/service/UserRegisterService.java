package br.costa.DevJobs.infrastructure.service;

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
    private final UserMapper userMapper;


    @Override
    public Boolean registerUser(Users user) {
        try{
            usersRepository.save(userMapper.userDomaintoUsersEntity(user));
            return true;
        }catch(Exception e){
            return  false;
        }
    }
}
