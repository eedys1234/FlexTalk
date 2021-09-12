package com.flextalk.we.cmmn.exception;

public class NotExistException extends RuntimeException {

    public NotExistException(String message) {
        super(message);
    }

    public NotExistException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
