package com.flextalk.we.cmmn.config;


import com.flextalk.we.cmmn.auth.LoginAuthenticationFilter;
import com.flextalk.we.cmmn.auth.LoginAuthenticationProvider;
import com.flextalk.we.cmmn.auth.LoginSuccessHandler;
import com.flextalk.we.cmmn.jwt.JWTTokenGenerator;
import com.flextalk.we.cmmn.jwt.TokenGenerator;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final TokenGenerator<CustomUser> jwtTokenGenerator;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic().disable()
            .csrf().disable().authorizeRequests()
            .anyRequest().permitAll()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .formLogin().disable()
            .addFilterBefore(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
    }


    @Bean
    public LoginAuthenticationFilter customAuthenticationFilter() throws Exception {
        LoginAuthenticationFilter customAuthenticationFilter = new LoginAuthenticationFilter(authenticationManager());
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/user/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(customLoginSuccessHandler());
        customAuthenticationFilter.afterPropertiesSet();
        return customAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LoginSuccessHandler customLoginSuccessHandler() {
        return new LoginSuccessHandler(jwtTokenGenerator);
    }

    @Bean
    public LoginAuthenticationProvider customAuthenticationProvider() {
        return new LoginAuthenticationProvider(bCryptPasswordEncoder());
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
    }

}
