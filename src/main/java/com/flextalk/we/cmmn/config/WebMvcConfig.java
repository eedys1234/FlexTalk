package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.auth.JWTTokenInterceptor;
import com.flextalk.we.cmmn.jwt.JWTSecurityKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JWTSecurityKey jwtSecurityKey;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor());
    }

    @Bean
    public JWTTokenInterceptor jwtTokenInterceptor() {
        return new JWTTokenInterceptor(jwtSecurityKey);
    }

}
