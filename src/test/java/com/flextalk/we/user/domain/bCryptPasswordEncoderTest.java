package com.flextalk.we.user.domain;

import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.*;

@SpringBootTest
@ActiveProfiles("test")
public class bCryptPasswordEncoderTest {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @DisplayName("암호화 테스트")
    @Test
    public void bCryptPasswordEncryptTest() {

        String password = "test123@";
        String encode = bCryptPasswordEncoder.encode(password);
        assertThat(encode, notNullValue());
    }
}
