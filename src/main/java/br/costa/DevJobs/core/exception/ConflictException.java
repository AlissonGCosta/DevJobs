package br.costa.DevJobs.core.exception;

public class ConflictException extends RuntimeException {

    private String code;

    public ConflictException(String message, String code) {

        super(message);
        this.code = code;
    }
}
