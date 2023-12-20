package com.interswitch.bookstore.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IdempotentService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public final static String IDEMPOTENT_KEY = "idempotent-key";

    @Cacheable(value = "idempotentCache", key = "'idempotent:' + #key")
    public ApiResponse<?> getResponse(String idempotentKey) {

        if(redisTemplate.hasKey("idempotent:"+idempotentKey)){
            String v = redisTemplate.opsForValue().get("idempotent:"+idempotentKey);
            if(BasicUtil.validString(v)){
                try {
                    ApiResponse<?> result = new ObjectMapper().readValue(v, new TypeReference<ApiResponse<?>>() {});
                    return result;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;

                }
            }

        }

        return null;

    }


    public void saveResponse(String idempotentKey, ApiResponse<?> response) {
        try {
            String resp = new ObjectMapper().writeValueAsString(response);

            redisTemplate.opsForValue().set("idempotent:" + idempotentKey, resp);
        } catch (JsonProcessingException e) {

        }
    }
}
