package br.costa.DevJobs.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public record UsersRequestDto(
        @NotNull
        String fullName,
        @NotNull
        String email,
        @NotNull
        String password,
        @NotNull
        String confirmPassword

) {
}
