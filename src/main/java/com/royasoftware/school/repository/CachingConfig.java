package com.royasoftware.school.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class CachingConfig extends CachingConfigurerSupport {

    private String redisHost = "192.168.99.100"; //@Value("${spring.redis.host}") 
    private int redisPort=30379;
    private String redisPassword="1qay2wsx"; //@Value("${spring.redis.password}") 
    
	@Bean
	@Override
	public CacheManager cacheManager() {
		return new RedisCacheManager(redisTemplate());
	}

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setPassword(redisPassword);
        factory.setUsePool(true);
        return factory;
    }
    
	@Bean
	@Override
	public CacheResolver cacheResolver() {
		return new CustomCacheResolver(cacheManager());
	}
}
