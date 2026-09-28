package br.costa.DevJobs.core.exception;

public class EmailAvaliableException extends RuntimeException {

    private String code;

    public EmailAvaliableException(String message, String code) {

        super(message);
        this.code = code;
    }
}
