package br.costa.DevJobs.infrastructure.service.userservice;

import br.costa.DevJobs.application.persistence.user.PostUserPersistence;
import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.infrastructure.Entity.UsersEntity;
import br.costa.DevJobs.infrastructure.config.PasswordConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserEntityService {

    private final PasswordConfig passwordConfig;

    public UsersEntity PostPersistenctoUsersEntity(PostUserPersistence user) {
        return new UsersEntity(
                user.fullName(),
                user.email(),
                passwordConfig.passwordEncoder().encode(user.password()),
                user.role(),
                user.createdAt(),
                user.updatedAt(),
                user.attempts()
        );
    }
}
