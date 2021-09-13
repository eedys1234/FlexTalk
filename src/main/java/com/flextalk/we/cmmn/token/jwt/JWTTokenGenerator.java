package com.flextalk.we.cmmn.token.jwt;

import com.flextalk.we.cmmn.token.TokenGenerator;
import com.flextalk.we.cmmn.token.jwt.JWTSecurityKey;
import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT Token Generator
 */
@Component("jwtTokenGenerator")
@RequiredArgsConstructor
@Log4j2
public class JWTTokenGenerator implements TokenGenerator<CustomUser> {

    //유효기간 4시간
    private final long expireMilisecond = 1000L * 60 * 60 * 4;

    private final JWTSecurityKey jwtSecurityKey;

    @Override
    public  String generate(CustomUser userDetails) {

        User user = userDetails.getUser();
        String email = user.getEmail();
        Role role = user.getRole();
        long id = user.getId();

        Claims claims = Jwts.claims().setSubject(email);

        //비공개 클레임 설정
        claims.put("role", role.getKey());
        claims.put("id", id);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expireMilisecond))
                .signWith(SignatureAlgorithm.HS256, jwtSecurityKey.getBaseSecurityKey())
                .compact();
    }

    @Override
    public String getTokenFromHeader(String header) {
        return JWTUtils.getTokenFromHeader(header);
    }

    @Override
    public String getRoleFromToken(String token) {
        return JWTUtils.getRoleFromToken(jwtSecurityKey.getBaseSecurityKey(), token);
    }

    @Override
    public boolean isValidateToken(String token) {
        log.info("jwtSecurityKey.getBaseSecurityKey() : " + jwtSecurityKey.getBaseSecurityKey());
        return JWTUtils.isValidateToken(jwtSecurityKey.getBaseSecurityKey(), token);
    }
}
