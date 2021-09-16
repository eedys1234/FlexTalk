package com.flextalk.we.cmmn.auth;

import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.cmmn.util.AuthConstants;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.repository.TokenRepository;
import com.flextalk.we.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 사용자 로그인 성공 시 JWT Token 발행
 */
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final TokenGenerator<CustomUser> jwtTokenGenerator;
    private final TokenRepository tokenRepository;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {

        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        final String token = jwtTokenGenerator.generate(customUser);
        tokenRepository.saveToken(String.valueOf(customUser.getUser().getId()), token);
        response.addHeader(AuthConstants.AUTH_HEADER, String.format("%s %s", AuthConstants.TOKEN_TYPE, token));
    }
}
