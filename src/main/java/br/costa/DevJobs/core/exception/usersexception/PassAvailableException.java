package br.costa.DevJobs.core.exception.usersexception;

public class PassAvailableException extends RuntimeException {

    private String code;

    public PassAvailableException(String message, String code) {
        super(message);
        this.code = code;
    }
}
