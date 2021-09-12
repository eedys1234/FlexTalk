package com.flextalk.we.cmmn.exception;

public class NotFileException extends NotExistException {

    public NotFileException(String message) {
        super(message);
    }

    public NotFileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
