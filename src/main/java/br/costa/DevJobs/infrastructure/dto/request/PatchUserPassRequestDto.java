package br.costa.DevJobs.infrastructure.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Valid
public record PatchUserPassRequestDto(

        @NotNull
        String oldPassword,
        @NotNull
        String newPassword,
        @NotNull
        String confirmNewPassword
) {
}
