package com.flextalk.we.cmmn.auth;

import com.flextalk.we.cmmn.jwt.TokenGenerator;
import com.flextalk.we.cmmn.util.AuthConstants;
import com.flextalk.we.user.domain.entity.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 사용자 로그인 성공 시 JWT Token 발행
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private TokenGenerator jwtTokenGenerator;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {

        final String token = jwtTokenGenerator.generate(((CustomUser)authentication.getPrincipal()).getUser());
        response.addHeader(AuthConstants.AUTH_HEADER, String.format("%s %s", AuthConstants.TOKEN_TYPE, token));
    }
}
