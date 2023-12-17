package com.interswitch.bookstore.security;

import com.interswitch.bookstore.models.User;
import com.interswitch.bookstore.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService  implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findUserByUsername(username);


        return user;
    }
}
