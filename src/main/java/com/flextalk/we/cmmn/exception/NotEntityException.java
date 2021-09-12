package com.flextalk.we.cmmn.exception;

import com.flextalk.we.cmmn.response.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity 존재하지 않을 때 발생하는 예외
 */
public class NotEntityException extends NotExistException {

    private List<ErrorResponse.ErrorParam> errorParams = new ArrayList<>();

    public NotEntityException(String message) {
        super(message);
    }

//    public NotEntityException(String message, List<ErrorResponse.ErrorParam> errorParams) {
//        this(message);
//        this.errorParams.addAll(errorParams);
//    }

    public NotEntityException(String message, Throwable throwable) {
        super(message, throwable);
    }

//    public NotEntityException(String message, Throwable throwable, List<ErrorResponse.ErrorParam> errorParams) {
//        this(message, throwable);
//        this.errorParams.addAll(errorParams);
//    }
}
