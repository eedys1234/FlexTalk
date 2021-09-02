package com.flextalk.we.cmmn.configuration;

import com.flextalk.we.cmmn.prop.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
    }

    @PostConstruct
    public void postConstruct() {
        this.redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        this.redisServer.stop();
    }

}
