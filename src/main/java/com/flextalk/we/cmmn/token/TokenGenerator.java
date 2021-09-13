package com.flextalk.we.cmmn.token;

public interface TokenGenerator<T> {
    String generate(T t);
    String getTokenFromHeader(String header);
    String getRoleFromToken(String token);
    boolean isValidateToken(String token);

}
