package com.flextalk.we.cmmn.auth;

import com.flextalk.we.cmmn.exception.ResourceAccessDeniedException;
import com.flextalk.we.cmmn.jwt.JWTSecurityKey;
import com.flextalk.we.cmmn.jwt.JWTUtils;
import com.flextalk.we.cmmn.util.AnnotationUtils;
import com.flextalk.we.cmmn.util.AuthConstants;
import com.flextalk.we.user.domain.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;

@RequiredArgsConstructor
public class JWTTokenInterceptor implements HandlerInterceptor {

    private final JWTSecurityKey jwtSecurityKey;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws IOException {

        final String authorization = request.getHeader(AuthConstants.AUTH_HEADER);

        if(Objects.nonNull(authorization)) {
            final String token = JWTUtils.getTokenFromHeader(authorization);

            String security_key = jwtSecurityKey.getSECURITY_KEY();
            if(JWTUtils.isValidateToken(security_key, token) || !JWTUtils.isExpireToken(security_key, token)) {

                Role userRole = JWTUtils.getRoleFromToken(security_key, token);
                Role apiRole = getProperRole((HandlerMethod) handler);

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
