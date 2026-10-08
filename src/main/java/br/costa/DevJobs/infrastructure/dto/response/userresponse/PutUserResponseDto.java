package br.costa.DevJobs.infrastructure.dto.response.userresponse;

import java.time.Instant;

public record PutUserResponseDto(
        String fullName,
        String email,
        Instant updatedAt
) {
}
