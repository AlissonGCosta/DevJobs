package br.costa.DevJobs.core.exception;

public class InternalServerErrorException extends RuntimeException {

    private String code;

    public InternalServerErrorException(String message, String code) {
        super(message);
        this.code = code;
    }
}
