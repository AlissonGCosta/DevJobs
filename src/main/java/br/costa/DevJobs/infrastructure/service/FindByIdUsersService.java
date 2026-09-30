package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.FindByIdGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.exception.IdFoundAvailableException;
import br.costa.DevJobs.core.exception.enums.ErrorCodeEnum;

import br.costa.DevJobs.infrastructure.mappers.UserMapper;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindByIdUsersService  implements FindByIdGateway {

    private final UsersRepository usersRepository;
    private final UserMapper userMapper;

    @Override
    public Users findById(Long id) {

        var users = usersRepository.findById(id)
                .orElseThrow(() -> new IdFoundAvailableException(ErrorCodeEnum.IAI0001.getMessage(),  ErrorCodeEnum.IAI0001.getCode()));

        return userMapper.userEntityToDomain(users);
    }
}
