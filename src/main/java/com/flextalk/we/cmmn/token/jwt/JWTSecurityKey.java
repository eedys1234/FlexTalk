package com.flextalk.we.cmmn.token.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;

@Getter
@Component(value = "jwtSecurityKey")
public class JWTSecurityKey {

    @Value("${jwt.security_key}")
    private String SECURITY_KEY;

    private String baseSecurityKey;

    public JWTSecurityKey() {
    }

    @PostConstruct
    public void init() {
        assert SECURITY_KEY != null;
        this.baseSecurityKey = Base64.getEncoder().encodeToString(SECURITY_KEY.getBytes());
    }

}
