package com.weshaka.framework.beans;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
@Lazy
@Profile("prod")
public class RedisConfig extends CachingConfigurerSupport {
    @Value("#{systemEnvironment['shaka.redis.host']}")
    private String redisHost;

    @Bean
    @DependsOn("redisTemplate")
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        final RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);

        // Number of seconds before expiration. Defaults to unlimited (0)
        cacheManager.setDefaultExpiration(10);// In seconds
        return cacheManager;
    }

    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                // This will generate a unique key of the class name, the method
                // name,
                // and all method parameters appended.
                final StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (final Object obj : objects) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean(name = "redisConnectionFactory")
    public JedisConnectionFactory redisConnectionFactory() {
        final JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
        final String redisHostName = Optional.ofNullable(redisHost).orElse("redis");
        // Defaults
        redisConnectionFactory.setHostName(redisHostName);
        redisConnectionFactory.setPort(6379);// TODO: Externalize
        return redisConnectionFactory;
    }

    @Bean(name = "redisTemplate")
    @DependsOn("redisConnectionFactory")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory cf) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }
}
