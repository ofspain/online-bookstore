package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.LoginDTO;
import com.interswitch.bookstore.exceptions.AuthenticationException;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.repositories.UserRepository;
import com.interswitch.bookstore.security.JwtTokenProvider;
import com.interswitch.bookstore.utils.BasicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.interswitch.bookstore.exceptions.Messages.*;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public LoginDTO login(String username, String password){

        User user = userRepository.findUserByUsername(username);

        if (user == null) throw new AuthenticationException(NO_USER);
        if(!BasicUtil.checkPassword(password,  user.getPassword())) throw new AuthenticationException(WRONG_PASSWORD);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        LoginDTO loginDto = new LoginDTO();
        loginDto.setToken(token);
        loginDto.setUser(user);
        return  loginDto;
    }
}
