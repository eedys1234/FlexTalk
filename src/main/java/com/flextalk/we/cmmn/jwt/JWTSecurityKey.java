package com.flextalk.we.cmmn.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Getter
@Component(value = "jwtSecurityKey")
public class JWTSecurityKey {

    @Value("${jwt.security_key}")
    private String SECURITY_KEY;

    private String baseSecurityKey;

    public JWTSecurityKey() {
        assert SECURITY_KEY != null;
        baseSecurityKey = Base64.getEncoder().encodeToString(SECURITY_KEY.getBytes());
    }

}
