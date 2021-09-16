package com.flextalk.we.user.domain.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TokenRepository {

    private final RedisTemplate<Object, Object> redisTemplate;

    public boolean findToken(String token) {
        return Objects.nonNull(redisTemplate.opsForValue().get(token));
    }

    public void saveToken(String id, String token) {
        redisTemplate.opsForValue().set(token, id);
    }

}
