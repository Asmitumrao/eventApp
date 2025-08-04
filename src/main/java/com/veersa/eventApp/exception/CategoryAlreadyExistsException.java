package com.veersa.eventApp.exception;

public class CategoryAlreadyExistsException extends RuntimeException {


    public CategoryAlreadyExistsException(String message) {
        super(message);
    }

    public CategoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
