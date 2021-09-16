package com.flextalk.we.cmmn.auth;

import com.flextalk.we.cmmn.exception.ResourceAccessDeniedException;
import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.cmmn.util.AuthConstants;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.repository.TokenRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

@Component("jwtTokenInterceptor")
public class JWTTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private TokenGenerator<CustomUser> jwtTokenGenerator;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws IOException {

        //handler를 통해 api permission 확인
        Role apiRole = getProperRole((HandlerMethod) handler);

        //apiRole이 GUEST 일경우
        if(apiRole == Role.ROLE_GUEST) {
            return true;
        }

        final String authorization = request.getHeader(AuthConstants.AUTH_HEADER);

        if(Objects.nonNull(authorization)) {
            final String token = jwtTokenGenerator.getTokenFromHeader(authorization);
            if(jwtTokenGenerator.isValidateToken(token) && tokenRepository.findToken(token)) {

                Role userRole = Role.valueOf(jwtTokenGenerator.getRoleFromToken(token));

                if(userRole.getPriority() <= apiRole.getPriority()) {
                    return true;
                }

                throw new ResourceAccessDeniedException("인가되지 않았습니다.");
            }
        }

        throw new AccessDeniedException("인증이 실패했습니다.");
    }


    private Role getProperRole(HandlerMethod handlerMethod) {

        ApiPermission annotation = handlerMethod.getMethod().getAnnotation(ApiPermission.class);

        if(Objects.isNull(annotation)) {
            return Role.ROLE_NORMAL;
        }

        return annotation.target();
    }
}
