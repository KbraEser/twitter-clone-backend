package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
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

    public User register(RegisterRequest registerRequest) {
        userRepository.findByEmail(registerRequest.email()).ifPresent(m -> {
            throw new RuntimeException("User with given email already exist");
        });

        String encodedPassword = passwordEncoder.encode(registerRequest.password());

        User user = new User();
        user.setName(registerRequest.name());
        user.setSurname(registerRequest.surname());
        user.setEmail(registerRequest.email());
        user.setPassword(encodedPassword);
        user.setPicture(registerRequest.picture());

        return userRepository.save(user);
    }
}

