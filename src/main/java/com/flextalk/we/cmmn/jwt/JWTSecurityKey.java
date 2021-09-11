package com.flextalk.we.cmmn.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Getter
@Component
public final class JWTSecurityKey {

    @Value("{jwt.security_key}")
    private String SECURITY_KEY = "";

    public JWTSecurityKey() {
        SECURITY_KEY = Base64.getEncoder().encodeToString(SECURITY_KEY.getBytes());
    }

}
