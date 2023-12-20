package com.interswitch.bookstore.dtos;

import com.interswitch.bookstore.models.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginDTO implements Serializable {

    private User user;

    private String token;
}
