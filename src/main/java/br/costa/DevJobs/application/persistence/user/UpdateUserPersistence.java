package br.costa.DevJobs.application.persistence.user;

public record UpdateUserPersistence(
        String email,
        String fullName
) {
}
