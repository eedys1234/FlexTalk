package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.token.jwt.JWTSecurityKey;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.PostConstruct;

@TestConfiguration
public class IntegrationTestConfig {

    @MockBean
    private JWTSecurityKey jwtSecurityKey;

    @PostConstruct
    public void init() {
        ReflectionTestUtils.setField(jwtSecurityKey, "SECURITY_KEY", "123##2da");
    }
}
