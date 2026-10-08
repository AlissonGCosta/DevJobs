package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.PassAvailableGatway;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.usersexception.PassAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import br.costa.DevJobs.usecase.userusecase.FindByIdUsersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassAvailableService implements PassAvailableGatway {

    private final PasswordConfig passwordConfig;
    private final UsersRepository usersRepository;



    @Override
    public void availablePass(String oldPassword, Long id) {

            UsersEntity user = usersRepository.findById(id)
                    .orElseThrow(() -> new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(), ErrorCodeEnum.IAI0001.getCode()));

            if(!passwordConfig.passwordEncoder().matches(oldPassword, user.getPassword())) {
              throw new PassAvailableException(ErrorCodeEnum.PSE0001.getMessage(),  ErrorCodeEnum.PSE0001.getCode());
            }


    }
}
