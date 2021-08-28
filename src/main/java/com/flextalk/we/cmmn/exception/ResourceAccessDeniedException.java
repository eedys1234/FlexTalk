package com.flextalk.we.cmmn.exception;

import com.flextalk.we.cmmn.response.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *  자원(리소스)에 (권한)인가 되지 않음
 */
public class ResourceAccessDeniedException extends RuntimeException {

    private List<ErrorResponse.ErrorParam> errorParams = new ArrayList<>();

    public ResourceAccessDeniedException(String message) {
        super(message);
    }

    public ResourceAccessDeniedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
