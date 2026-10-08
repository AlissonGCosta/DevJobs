package br.costa.DevJobs.core.exception.usersexception;

public class EmailAvaliableException extends RuntimeException {

    private String code;

    public EmailAvaliableException(String message, String code) {

        super(message);
        this.code = code;
    }
}
