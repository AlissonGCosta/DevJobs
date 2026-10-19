package br.costa.DevJobs.application.command.users;

public record UpdateUserCommand(
        String fullName,
        String email

) {
}
