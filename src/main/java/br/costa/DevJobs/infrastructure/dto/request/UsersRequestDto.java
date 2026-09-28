package br.costa.DevJobs.infrastructure.dto.request;

public record UsersRequestDto(
        String fullName,
        String email,
        String password,
        String confirmPassword

) {
}
