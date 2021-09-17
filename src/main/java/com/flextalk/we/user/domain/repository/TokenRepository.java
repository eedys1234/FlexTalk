package com.flextalk.we.user.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<String, Object> redisSessionTemplate;

    public boolean findToken(String token) {
        return Objects.nonNull(redisSessionTemplate.opsForValue().get(token));
    }

    public void saveToken(String id, String token) {
        redisSessionTemplate.opsForValue().set(token, id);
    }

}
