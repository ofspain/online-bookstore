package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.LoginDTO;
import com.interswitch.bookstore.exceptions.AuthenticationException;
import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.repositories.UserRepository;
import com.interswitch.bookstore.security.JwtTokenProvider;
import com.interswitch.bookstore.utils.BasicUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.interswitch.bookstore.exceptions.Messages.*;


@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User saveUser(User user){
        log.info("Saving user {}",user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public LoginDTO login(String username, String password){
        log.info("User {} attempting login ",username);
        User user = userRepository.findUserByUsername(username);

        if (user == null) throw new AuthenticationException(NO_USER);
        if(!BasicUtil.checkPassword(password,  user.getPassword())) throw new AuthenticationException(WRONG_PASSWORD);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        LoginDTO loginDto = new LoginDTO();
        loginDto.setToken(token);
        loginDto.setUser(user);
        log.info("User {} login ",username);
        return  loginDto;
    }

    public User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return (User) principal;
            }
        }
        return null;
    }

}
