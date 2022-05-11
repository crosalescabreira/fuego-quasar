package com.satellite.position.exception;


public abstract class GenericException extends RuntimeException {

    private final String errorMessage;

    public GenericException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
