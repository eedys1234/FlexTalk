package com.flextalk.we.cmmn.config;

import com.flextalk.we.cmmn.prop.RedisCacheProperties;
import com.flextalk.we.cmmn.prop.RedisSessionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConnection {

    @Autowired
    private RedisSessionProperties redisSessionProperties;

    @Autowired
    private RedisCacheProperties redisCacheProperties;

    @Autowired
    private RedisCacheConfiguration redisCacheConfiguration;

    @Bean
    public RedisConnectionFactory redisSessionConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisSessionProperties.getRedisHost());
        redisStandaloneConfiguration.setPort(redisSessionProperties.getRedisPort());

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean({"redisConnectionFactory", "redisCacheConnectionFactory"})
    public RedisConnectionFactory redisCacheConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisCacheProperties.getRedisHost());
        redisStandaloneConfiguration.setPort(redisCacheProperties.getRedisPort());

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisSessionTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSessionConnectionFactory());
        return template;
    }

    @Bean
    public RedisTemplate<Object, Object> redisCacheTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisCacheConnectionFactory());
        return template;
    }


    @Bean
    public RedisCacheManager redisCacheManager() {

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisCacheConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

}
