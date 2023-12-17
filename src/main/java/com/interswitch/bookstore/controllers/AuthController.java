package com.interswitch.bookstore.controllers;

import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.services.IdempotentService;
import com.interswitch.bookstore.services.UserService;
import com.interswitch.bookstore.utils.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    @Autowired
    UserService userService;

    @Autowired
    IdempotentService idempotentService;



    @PostMapping("/sign-up")
    public ApiResponse<User> signup(@RequestBody User userRequest, @RequestHeader HttpHeaders headers){
        String idemKey = headers.getFirst(IdempotentService.IDEMPOTENT_KEY);
        ApiResponse<User> response = new ApiResponse<>(userService.saveUser(userRequest), HttpStatus.OK);
        idempotentService.saveResponse(idemKey, response);
        return response;
    }
}
