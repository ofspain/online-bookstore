package com.interswitch.bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(@Value("${spring.data.redis.port}") Integer redisPort,
                                                           @Value("${spring.data.redis.host}") String redisHost) {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
//        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
//    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
