package com.veersa.eventApp.exception;

public class UnauthorizedActionException extends  RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
