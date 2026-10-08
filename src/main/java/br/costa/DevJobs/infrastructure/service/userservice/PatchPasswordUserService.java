package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.PatchPassUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
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
    private final PasswordConfig passwordConfig;




    @Override
    public Boolean patchPass(String newPassword, String confirmNewPassword, Long id) {
       try{

           UsersEntity userEntity = usersRepository.findById(id)
                   .orElseThrow(() -> new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(),  ErrorCodeEnum.IAI0001.getCode()));

           userEntity.setPassword(passwordConfig.passwordEncoder().encode(newPassword));
           userEntity.setConfirmPassword(passwordConfig.passwordEncoder().encode(confirmNewPassword));


           usersRepository.save(userEntity);
           return true;
       }catch(Exception e){
           return false;
       }
    }
}
