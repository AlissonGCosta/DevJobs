package br.costa.DevJobs.core.exception;

public class IdAvailableException extends RuntimeException {

    private String code;

    public IdAvailableException(String message, String code) {

        super(message);
        this.code = code;
    }
}
