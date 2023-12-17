package com.interswitch.bookstore;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Validator;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestConfig {

    private RedisServer redisServer;

    public TestConfig(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @Bean
    public Validator validator() {

        return new LocalValidatorFactoryBean();
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("starting redis.........");
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("stopping redis.........");
        redisServer.stop();
    }
}