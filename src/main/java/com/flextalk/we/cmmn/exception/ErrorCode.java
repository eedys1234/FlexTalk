package com.flextalk.we.cmmn.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "invalid parameter"),
    UNAUTHORIZED(401, "not exist token"),
    FORBIDDEN(403, "not permission"),
    NOT_FOUND(404, "not found"),
    METHOD_NOT_ALLOWED(405, "url not supported this method"),
    CONFLICT(409, "business logic contradiction occur");

    private final int status;
    private final String message;

    ErrorCode(final int status, String message) {
        this.status = status;
        this.message = message;
    }


}
