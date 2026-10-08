package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserEntityService {

    private final PasswordConfig passwordConfig;

    public UsersEntity userDomaintoUsersEntity(Users user) {
        return new UsersEntity(
                user.getFullName(),
                user.getEmail(),
                passwordConfig.passwordEncoder().encode(user.getPassword()),
                passwordConfig.passwordEncoder().encode(user.getConfirmPassword()),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getAttempt()
        );
    }
}
