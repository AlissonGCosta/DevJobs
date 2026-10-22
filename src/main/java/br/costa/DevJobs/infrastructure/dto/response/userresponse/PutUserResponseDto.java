package br.costa.DevJobs.infrastructure.dto.response.userresponse;

import br.costa.DevJobs.core.domain.enumerated.enumuser.UserRoleEnum;

import java.time.Instant;

public record PutUserResponseDto(
        Long id,
        String fullName,
        String email,
        Instant updatedAt,
        UserRoleEnum roles
) {
}
