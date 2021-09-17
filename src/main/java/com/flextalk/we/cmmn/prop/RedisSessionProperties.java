package com.flextalk.we.cmmn.prop;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RedisSessionProperties {

    private int redisPort;
    private String redisHost;

    public RedisSessionProperties(@Value("${spring.redis.session.port}") int redisPort, @Value("${spring.redis.host}") String redisHost) {

        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }
}
