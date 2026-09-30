package br.costa.DevJobs.core.exception;

public class IdFoundAvailableException extends RuntimeException {

    private String code;

    public IdFoundAvailableException(String message, String code) {

        super(message);
        this.code = code;
    }
}
