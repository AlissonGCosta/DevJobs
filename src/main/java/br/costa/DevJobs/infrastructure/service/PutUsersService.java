package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.PutUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;
import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PutUsersService implements PutUsersGateway {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Users putUsers(Users users) {

        var userEntity =  usersRepository.findById(users.getId())
                .orElseThrow(() -> new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(),
                        ErrorCodeEnum.IAI0001.getCode()));

        userEntity.setFullName(users.getFullName());
        userEntity.setEmail(users.getEmail());
        userEntity.setUpdatedAt(users.getUpdatedAt());

        var save =  usersRepository.save(userEntity);
        var result = userMapper.userEntityToDomain(save);
        result.setUpdatedAt(save.getUpdatedAt());

        return result;
    }



}
