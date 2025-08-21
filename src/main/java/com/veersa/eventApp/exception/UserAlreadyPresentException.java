package com.veersa.eventApp.exception;

public class UserAlreadyPresentException extends RuntimeException{
    public UserAlreadyPresentException(String message) {
        super(message);
    }

    public UserAlreadyPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
