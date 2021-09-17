package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.prop.RedisCacheProperties;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class EmbeddedRedisCacheConfig {

    private RedisServer redisServer;

    public EmbeddedRedisCacheConfig(RedisCacheProperties redisCacheProperties) {
        this.redisServer = new RedisServer(redisCacheProperties.getRedisPort());
    }

    @PostConstruct
    public void start() {
        if(this.redisServer != null) this.redisServer.start();
    }

    @PreDestroy
    public void stop() {
        if(this.redisServer != null) this.redisServer.stop();
    }
}
