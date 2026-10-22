package br.costa.DevJobs.application.result.users;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;

import java.time.Instant;

public record UpdateUserResult(
        Long id,
        String fullName,
        String email,
        Instant updatedAt,
        UserRoleEnum role
) {
}
