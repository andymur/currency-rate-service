package com.andymur.sme.challenge.resource.exceptions;

public class RateServiceException extends RuntimeException {
    private int code;

    public RateServiceException() {
        this(500);
    }

    public RateServiceException(int code) {
        this(code, "Error while processing the request", null);
    }

    public RateServiceException(int code, String message) {
        this(code, message, null);
    }

    public RateServiceException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
