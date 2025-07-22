package com.veersa.eventApp.exception;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException() {
        super("Incorrect password provided.");
    }

    public IncorrectPasswordException(String message) {
        super(message);
    }
}
