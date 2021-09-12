package com.flextalk.we.cmmn.jwt;

import com.flextalk.we.user.domain.entity.CustomUser;
import com.flextalk.we.user.domain.entity.Role;
import com.flextalk.we.user.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

/**
 * JWT Token Generator
 */
@Component("jwtTokenGenerator")
@RequiredArgsConstructor
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
        long nowTime = now.getTime();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(nowTime + expireMilisecond))
                .signWith(SignatureAlgorithm.HS256, jwtSecurityKey.getBaseSecurityKey())
                .compact();
    }
}
