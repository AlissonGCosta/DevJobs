package br.costa.DevJobs.infrastructure.dto.response.userresponse;

public record UsersResponseDto(
        Long id,
        String fullName,
        String email
) {
}
