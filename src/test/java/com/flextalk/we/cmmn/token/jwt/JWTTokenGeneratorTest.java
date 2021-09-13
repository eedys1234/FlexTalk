package com.flextalk.we.cmmn.token.jwt;

import com.flextalk.we.cmmn.auth.JWTTokenInterceptor;
import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.user.cmmn.MockUserFactory;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Collections;

import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_EMAIL;
import static com.flextalk.we.user.cmmn.MockUserInfo.ADMIN_PASSWORD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTTokenGeneratorTest {

    @InjectMocks
    private JWTTokenGenerator jwtTokenGenerator;

    @Mock
    private JWTSecurityKey jwtSecurityKey;

    @DisplayName("토큰 발행 테스트")
    @Test
    public void tokenGenerateTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, ADMIN_PASSWORD);
        adminUser.grantAuthority(Role.ROLE_ADMIN);
        long userId = 1L;
        ReflectionTestUtils.setField(adminUser, "id", userId);
        String base64SecurityKey = Base64.getEncoder().encodeToString("123##2da".getBytes());

        doReturn(base64SecurityKey).when(jwtSecurityKey).getBaseSecurityKey();
        CustomUser customUser = new CustomUser(adminUser, Collections.singleton(new SimpleGrantedAuthority(adminUser.getRole().getKey())));

        //when
        String token = jwtTokenGenerator.generate(customUser);

        //then
        assertThat(token, notNullValue());
        assertThat(token, containsString("."));

        //verify
        verify(jwtSecurityKey, times(1)).getBaseSecurityKey();
    }

    @DisplayName("토큰으로부터 Role 추출 테스트")
    @Test
    public void ExtractRoleByTokenTest() {

        //given
        MockUserFactory mockUserFactory = new MockUserFactory();
        User adminUser = mockUserFactory.create(ADMIN_EMAIL, ADMIN_PASSWORD);
        adminUser.grantAuthority(Role.ROLE_ADMIN);

        long userId = 1L;
        ReflectionTestUtils.setField(adminUser, "id", userId);
        String base64SecurityKey = Base64.getEncoder().encodeToString("123##2da".getBytes());

        doReturn(base64SecurityKey).when(jwtSecurityKey).getBaseSecurityKey();
        CustomUser customUser = new CustomUser(adminUser, Collections.singleton(new SimpleGrantedAuthority(adminUser.getRole().getKey())));

        String token = jwtTokenGenerator.generate(customUser);

        //when
        String role = jwtTokenGenerator.getRoleFromToken(token);

        //then
        assertThat(role, is(Role.ROLE_ADMIN.getKey()));

        //verify
        verify(jwtSecurityKey, times(2)).getBaseSecurityKey();
    }

}
