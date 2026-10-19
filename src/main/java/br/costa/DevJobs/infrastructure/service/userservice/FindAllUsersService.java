package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.gateway.usersgateway.FindAllUsersGateway;
import br.costa.DevJobs.application.result.users.FindAllUsersResult;
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
    public List<FindAllUsersResult> findAll() {
        return usersRepository.findAll().stream()
                .map(user -> new FindAllUsersResult(
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
