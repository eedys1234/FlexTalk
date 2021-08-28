package com.flextalk.we.cmmn.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HttpResponse {

    /**
     * 인스턴스화 불가
     */
    private HttpResponse() {
        new AssertionError();
    }

    private static final ResponseEntity<Void> RESPONSE_INVALID_VALUE = new ResponseEntity(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<Void> RESPONSE_NULL_POINTER = new ResponseEntity(HttpStatus.BAD_REQUEST);
    private static final ResponseEntity<Void> RESPONSE_UNAUTHORIZED = new ResponseEntity(HttpStatus.UNAUTHORIZED);
    private static final ResponseEntity<Void> RESPONSE_NOT_FOUND = new ResponseEntity(HttpStatus.NOT_FOUND);
    private static final ResponseEntity<Void> RESPONSE_CREATED = new ResponseEntity(HttpStatus.CREATED);
    private static final ResponseEntity<Void> RESPONSE_FORBIDDEN = new ResponseEntity(HttpStatus.FORBIDDEN);

}
