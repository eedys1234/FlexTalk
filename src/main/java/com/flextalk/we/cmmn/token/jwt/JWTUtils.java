package com.flextalk.we.cmmn.token.jwt;

import com.flextalk.we.user.domain.entity.Role;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Log4j2
class JWTUtils {

    public static boolean isValidateToken(String security, String token) {
        log.info("security : " + security);
        return isSubValidateToken(security, token) && !isExpireToken(security, token);
    }

    public static boolean isSubValidateToken(String security, String token) {
        String sub = String.valueOf(getBodyFromToken(security, token).get("sub"));

        if(!StringUtils.isEmpty(sub)) {
            return true;
        }
        return false;
    }

    public static boolean isExpireToken(String security, String token) {

        long exp = (long) getBodyFromToken(security, token).get("exp");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expire = LocalDateTime.ofInstant(Instant.ofEpochMilli(exp), ZoneId.systemDefault());

        return now.isAfter(expire);
    }


    private static Map<String, Object> getBodyFromToken(String security, String token) {
        return Jwts.parser().setSigningKey(security).parseClaimsJws(token).getBody();
    }

    public static String getTokenFromHeader(String header) {
        return header.split(" ")[1];
    }

    public static String getRoleFromToken(String security, String token) {
        Map<String, Object> body = getBodyFromToken(security, token);

        return Optional.ofNullable(body.get("role"))
                .map(u -> String.valueOf(u))
                .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 token입니다. "));
    }

    public static Long getIdFromToken(String security, String token) {
        Map<String, Object> body = getBodyFromToken(security, token);

        return Optional.ofNullable(body.get("id"))
                .map(id -> Long.parseLong(String.valueOf(id)))
                .orElseThrow(()-> new IllegalArgumentException("유효하지 않은 token입니다. "));
    }

}
