package br.costa.DevJobs.application.command.users;

public record PatchUserPassCommand(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {
}
