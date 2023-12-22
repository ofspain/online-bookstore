package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.dtos.IdempotenceDTO;
import com.interswitch.bookstore.dtos.LoginDTO;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.services.UserService;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("api/auth")
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    IdempotentService idempotentService;
    

    @PostMapping("/sign-up")
    public ApiResponse<User> signup(@Valid @RequestBody User userRequest, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<User> cachedResponse = BasicUtil.validString(idemKey) ? (ApiResponse<User>) idempotentService.getResponse(idemKey) : null;
        if(null != cachedResponse){
            return cachedResponse;
        }

        ApiResponse<User> response = new ApiResponse<>(userService.saveUser(userRequest), HttpStatus.OK);
        if(null != idemKey){idempotentService.saveResponse(idemKey, new IdempotenceDTO<User>(response.getBody(), response.getHttpStatus()));}
        return response;
    }

    @PostMapping("/login")
    public ApiResponse<LoginDTO> login(@RequestBody Map<String, String> request, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<LoginDTO> cachedResponse = BasicUtil.validString(idemKey) ?  (ApiResponse<LoginDTO>) idempotentService.getResponse(idemKey) : null;
        if(null != cachedResponse){return cachedResponse;}

        ApiResponse<LoginDTO> response = new ApiResponse<>(userService.login(request.get("username"), request.get("password")), HttpStatus.OK);
        if(null != idemKey){idempotentService.saveResponse(idemKey, new IdempotenceDTO<LoginDTO>(response.getBody(), response.getHttpStatus()));}
        return response;
    }

}
