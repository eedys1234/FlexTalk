//package com.flextalk.we.cmmn.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//
//@EnableCaching
//@Configuration
//@RequiredArgsConstructor
//public class CacheConfig {
//
//    private final RedisCacheConfiguration redisCacheConfiguration;
//
//    @Autowired
//    private RedisConnectionFactory redisCacheConnectionFactory;
//
//    @Bean
//    public RedisCacheManager redisCacheManager() {
//
//        return RedisCacheManager.RedisCacheManagerBuilder
//                .fromConnectionFactory(redisCacheConnectionFactory)
//                .cacheDefaults(redisCacheConfiguration)
//                .build();
//    }
//
//}
