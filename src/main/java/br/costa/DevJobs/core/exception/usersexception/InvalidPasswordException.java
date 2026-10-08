package br.costa.DevJobs.core.exception.usersexception;

public class InvalidPasswordException extends RuntimeException {

    private String code;

    public InvalidPasswordException(String message, String code) {
        super(message);
        this.code = code;
    }
}
