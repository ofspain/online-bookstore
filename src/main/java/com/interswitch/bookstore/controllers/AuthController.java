package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.dtos.LoginDto;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.services.UserService;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/auth/")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    IdempotentService idempotentService;

    @Autowired
    private RedisTemplate<String, ApiResponse<?>> redisTemplate;



    @PostMapping("/sign-up")
    public ApiResponse<User> signup(@Valid @RequestBody User userRequest, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<User> cachedResponse = BasicUtil.validString(idemKey) ? (ApiResponse<User>) idempotentService.getResponse(idemKey) : null;
        if(null != cachedResponse){return cachedResponse;}

        ApiResponse<User> response = new ApiResponse<>(userService.saveUser(userRequest), HttpStatus.OK);
        if(null != idemKey){idempotentService.saveResponse(idemKey, response);}
        return response;
    }//dxzxytlcemlr

    @PostMapping("/login")
    public ApiResponse<LoginDto> login(@RequestBody Map<String, String> request, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<LoginDto> cachedResponse = BasicUtil.validString(idemKey) ?  (ApiResponse<LoginDto>) idempotentService.getResponse(idemKey) : null;
        if(null != cachedResponse){return cachedResponse;}

        ApiResponse<LoginDto> response = new ApiResponse<>(userService.login(request.get("username"), request.get("password")), HttpStatus.OK);
        if(null != idemKey){idempotentService.saveResponse(idemKey, response);}
        return response;
    }

}
