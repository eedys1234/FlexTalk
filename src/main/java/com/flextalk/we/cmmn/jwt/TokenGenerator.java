package com.flextalk.we.cmmn.jwt;

public interface TokenGenerator<T> {
    String generate(T t);
}
