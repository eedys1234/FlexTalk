package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.prop.RedisSessionProperties;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//@TestConfiguration
public class EmbeddedRedisSessionConfig {

    private RedisServer redisServer;

    public EmbeddedRedisSessionConfig(RedisSessionProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
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
