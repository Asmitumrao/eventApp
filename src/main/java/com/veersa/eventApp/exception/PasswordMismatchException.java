package com.veersa.eventApp.exception;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException() {
        super("Password mismatch. Please try again.");
    }

    public PasswordMismatchException(String message) {
        super(message);
    }
}
