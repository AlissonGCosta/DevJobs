package br.costa.DevJobs.infrastructure.exception;

public record Error(
        String Field,
        String message
) {
}
