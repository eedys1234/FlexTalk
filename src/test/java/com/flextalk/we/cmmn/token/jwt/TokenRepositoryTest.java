package com.flextalk.we.cmmn.token.jwt;

import com.flextalk.we.cmmn.config.IntegrationTestConfig;
import com.flextalk.we.cmmn.config.TestRedisConfiguration;
import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.User;
import com.flextalk.we.user.domain.repository.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_EMAIL;
import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenGenerator<CustomUser> tokenGenerator;

    private String token;
    private User user;

    @BeforeEach
    public void init() {
        MockUserFactory mockUserFactory = new MockUserFactory();
        this.user = mockUserFactory.create(ADMIN_EMAIL, ADMIN_PASSWORD);
        long userId = 1L;
        ReflectionTestUtils.setField(this.user, "id", userId);
        CustomUser customUser = new CustomUser(this.user, Collections.singleton(new SimpleGrantedAuthority(this.user.getRole().getKey())));
//        this.token = tokenGenerator.generate(customUser);
        this.token = "sssss";
    }

    @DisplayName("Token 저장 To Redis 테스트")
    @Test
    public void saveTokenToRedisTest() {

        //when
        tokenRepository.saveToken(String.valueOf(user.getId()), token);

        //then
        assertTrue(tokenRepository.findToken(token));
    }
}
