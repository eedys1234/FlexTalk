package com.flextalk.we.cmmn.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuccessResponse<T> {

    private String message;

    private int status;

    private T result;

    private SuccessResponse(int status) {
        this(status, "");
    }

    private SuccessResponse(int status, String message) {
        this(status, message, null);
    }

    private SuccessResponse(int status, String message, T result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public static SuccessResponse of(int status) {
        SuccessResponse response = new SuccessResponse(status);
        return response;
    }

    public static <T> SuccessResponse of(int status, T result) {
        SuccessResponse response = new SuccessResponse(status, "", result);
        return response;
    }

    public static <T> SuccessResponse of(int status, String message, T result) {
        SuccessResponse response = new SuccessResponse(status, message, result);
        return response;
    }
}

