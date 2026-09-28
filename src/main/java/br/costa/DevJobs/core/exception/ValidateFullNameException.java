package br.costa.DevJobs.core.exception;

public class ValidateFullNameException extends RuntimeException {

    private String code;

    public ValidateFullNameException(String message, String code) {
        super(message);
        this.code = code;
    }
}
