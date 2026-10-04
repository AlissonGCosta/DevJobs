package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.PatchPassUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PatchPasswordUserService implements PatchPassUsersGateway {

    private final UsersRepository usersRepository;
    private final FindByIdUsersUseCase findByIdUsersUseCase;
    private final UserMapper userMapper;


    @Override
    public Boolean patchPass(Users users, Long id) {
       try{

           var patchPassUsers = findByIdUsersUseCase.findById(id);

           patchPassUsers.setConfirmPassword(users.getConfirmPassword());
           patchPassUsers.setPassword(users.getPassword());
           patchPassUsers.setUpdatedAt(Instant.now());

           usersRepository.save(userMapper.userDomaintoUsersEntity(patchPassUsers));
           return true;
       }catch(Exception e){
           return false;
       }
    }
}
