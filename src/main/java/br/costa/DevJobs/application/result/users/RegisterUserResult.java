package br.costa.DevJobs.application.result.users;

import java.time.Instant;

public record RegisterUserResult(
        Long id,
        String email,
        String fullName

) {
}
