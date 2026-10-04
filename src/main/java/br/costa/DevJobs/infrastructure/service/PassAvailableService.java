package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.PassAvailableGatway;
import br.costa.DevJobs.core.exception.PassAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassAvailableService implements PassAvailableGatway {

    private final PasswordConfig passwordConfig;
    private final FindByIdUsersUseCase findByIdUsersUseCase;


    @Override
    public Boolean availablePass(String oldPassword, Long id) {

        try{

            var user = findByIdUsersUseCase.findById(id);

            if(!passwordConfig.passwordEncoder().matches(oldPassword, user.getPassword())) {
              throw new PassAvailableException(ErrorCodeEnum.PSE0001.getMessage(),  ErrorCodeEnum.PSE0001.getCode());
            }

            return true;
        }catch(Exception e){
            return false;
        }

    }
}
