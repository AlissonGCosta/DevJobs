package br.costa.DevJobs.infrastructure.service;

import br.costa.DevJobs.application.gateway.usersgateway.FindAllUsersGateway;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.persistence.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindAllUsersService  implements FindAllUsersGateway {

    private final UsersRepository usersRepository;

    @Override
    public List<Users> findAll() {
        return usersRepository.findAll().stream()
                .map(user -> new Users(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getCreatedAt(),
                        user.getRole()
                     )
                )
                .toList();

    }
}
