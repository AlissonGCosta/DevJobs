package br.costa.DevJobs.infrastructure.dto.response;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;

import java.time.Instant;

public record UserResponseFindByIdDto(
        Long id,
        String fullName,
        String email,
        Instant createdAt,
        UserRoleEnum role
) {
}
