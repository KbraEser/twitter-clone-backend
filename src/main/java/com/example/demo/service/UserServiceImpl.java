package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }


    @Override
    public User findById(Long id) {
        TwitterValidation.validateId(id);
        return userRepository.findById(id).orElseThrow(()-> new ApiException("User is not found with id: "+ id, HttpStatus.NOT_FOUND));
    }
}
