package com.flextalk.we.cmmn.exception;

import com.flextalk.we.cmmn.response.ErrorResponse;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * javax.validate.Valid or @Validated으로 binding error 발생시 발생
     * HttpMessageConverter에서 등록한 HttpMessageConverter가 binding 하지 못했을 때 발생
     * @param e method binding exception
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ErrorResponse.of(ErrorCode.BAD_REQUEST, e.getBindingResult());
    }

    /**
     * url(자원)이 지원하지 않는 method 호출시 발생
     * @param e url not supported this method
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 인증되지 않은 사용자일경우 발생
     * @param e not exist token
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return ErrorResponse.of(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 인증되지 않은 사용자일경우 발생
     * @param e 비밀번호 틀림
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        return ErrorResponse.of(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 인가되지 않은 사용자일경우 발생
     * @param e not permission
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ErrorResponse handleResourceAccessDeniedException(ResourceAccessDeniedException e) {
        return ErrorResponse.of(ErrorCode.FORBIDDEN);
    }

    /**
     * url이 없을경우 발생
     * @param e not found
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return ErrorResponse.of(ErrorCode.NOT_FOUND);
    }

    /**
     * Not Exist Exception
     * @param e not exist
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotExistException.class)
    public ErrorResponse handleNotExistException(NotEntityException e) {
        return ErrorResponse.of(ErrorCode.NOT_FOUND);
    }

    /**
     * 비즈니스 로직에 모순이 발생할 경우 발생
     * @param e business logic contradiction
     * @return ErrorResponse
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResponse handleIllegalStateException(IllegalStateException e) {
        return ErrorResponse.of(ErrorCode.CONFLICT);
    }

    /**
     * 비즈니스 로직 수행 시 적절하지 않는 인수일 경우 발생
     * @param e invalid parameter
     * @return Error Response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return ErrorResponse.of(ErrorCode.BAD_REQUEST.getStatus(), e.getMessage());
    }
}
