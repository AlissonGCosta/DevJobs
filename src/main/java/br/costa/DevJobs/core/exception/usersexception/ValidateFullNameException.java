package br.costa.DevJobs.core.exception.usersexception;

public class ValidateFullNameException extends RuntimeException {

    private String code;

    public ValidateFullNameException(String message, String code) {
        super(message);
        this.code = code;
    }
}
