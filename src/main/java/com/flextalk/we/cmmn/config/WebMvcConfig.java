package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.auth.JWTTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JWTTokenInterceptor jwtTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor);
    }

//    @Bean
//    public JWTTokenInterceptor jwtTokenInterceptor() {
//        return new JWTTokenInterceptor();
//    }

}
