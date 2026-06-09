package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
public class AuthenticationService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password) {
        userRepository.findByEmail(email).ifPresent(m -> {
            throw new RuntimeException("User with given email already exist");
        });

        String encodedPassword = passwordEncoder.encode(password);


        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);


        return userRepository.save(user);

    }
}

