package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.PutUsersGateway;
import br.costa.DevJobs.application.persistence.user.UpdateUserPersistence;
import br.costa.DevJobs.application.result.users.UpdateUserResult;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.usersexception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PutUsersService implements PutUsersGateway {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UpdateUserResult putUsers(UpdateUserPersistence users, Long id) {

        var userEntity =  usersRepository.findById(id)
                .orElseThrow(() -> new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(),
                        ErrorCodeEnum.IAI0001.getCode()));

        userEntity.setEmail(users.email());
        userEntity.setFullName(users.fullName());
        userEntity.setUpdatedAt(Instant.now());


        var save =  usersRepository.save(userEntity);
        return userMapper.userEntityToUpdateUserResult(save);



    }



}
