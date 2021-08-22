package com.flextalk.we.cmmn.response;

import com.flextalk.we.cmmn.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private int status;
    private List<ErrorParam> errors = new ArrayList<>();

    private ErrorResponse(final int status, final String message) {
        this.status = status;
        this.message = message;
    }

    private ErrorResponse(final ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode.getMessage());
    }

    private ErrorResponse(final ErrorCode errorCode, final List<ErrorParam> errors) {
        this(errorCode);
        this.errors = errors;
    }

    public static ErrorResponse of(final int status, final String message) {
        return new ErrorResponse(status, message);
    }

    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bindingResult) {
        return new ErrorResponse(errorCode, ErrorParam.of(bindingResult));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    private static class ErrorParam {

        private String param;
        private String value;
        private String msg;

        private ErrorParam(FieldError error) {
            this.param = error.getField();
            this.value = Objects.isNull(error.getRejectedValue()) ? "" : error.getRejectedValue().toString();
            this.msg = error.getDefaultMessage();
        }

        public static List<ErrorParam> of(final BindingResult bindingResult) {
            final List<FieldError> errors = bindingResult.getFieldErrors();
            return errors.stream()
                    .map(ErrorParam::new)
                    .collect(toList());
        }
    }
}
