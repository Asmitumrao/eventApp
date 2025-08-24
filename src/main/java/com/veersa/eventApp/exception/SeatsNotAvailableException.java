package com.veersa.eventApp.exception;

public class SeatsNotAvailableException extends  RuntimeException {
    public SeatsNotAvailableException(String message) {
        super(message);
    }
}
