package br.costa.DevJobs.application.command.users;

public record RegisterUserRequestCommand(
        String fullName,
        String email,
        String password,
        String confirmPassword
) {
}
