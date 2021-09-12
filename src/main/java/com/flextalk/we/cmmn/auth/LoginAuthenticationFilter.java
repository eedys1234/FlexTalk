package com.flextalk.we.cmmn.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flextalk.we.user.dto.UserLoginRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private ObjectMapper objectMapper;

    public LoginAuthenticationFilter(final AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        final UsernamePasswordAuthenticationToken authRequest;
        final UserLoginRequestDto loginRequestDto;

        try {
            loginRequestDto = objectMapper.readValue(request.getInputStream(), UserLoginRequestDto.class);
            authRequest = new UsernamePasswordAuthenticationToken(loginRequestDto.getUserEmail(), loginRequestDto.getUserPassword());
        }
        catch (IOException e) {
            throw new AccessDeniedException("인증을 실패했습니다. 다시 시도해주세요.");
        }

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
