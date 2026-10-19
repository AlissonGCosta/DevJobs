package br.costa.DevJobs.application.persistence.user;

import br.costa.DevJobs.core.domain.Users;
import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;

import java.time.Instant;

public record PostUserPersistence(
        String fullName,
        String email,
        String password,
        UserRoleEnum role,
        Instant createdAt,
        Instant updatedAt,
        Integer attempts
) {
}
