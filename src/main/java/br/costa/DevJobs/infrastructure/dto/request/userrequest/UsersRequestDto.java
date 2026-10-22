package br.costa.DevJobs.infrastructure.dto.request.userrequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Valid
public record UsersRequestDto(
        @NotNull
        String fullName,

        @NotNull
        @Email
        String email,
        @NotNull
        String password,
        @NotNull
        String confirmPassword

) {
}
