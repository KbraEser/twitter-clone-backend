package com.example.demo.service;

import com.example.demo.entity.Tweet;

import java.util.List;

public interface TweetService {

    Tweet save(Tweet tweet);

    List<Tweet> findAll();

    List<Tweet> findAllByUserId(Long id);

    Tweet findById(Long id);

    Tweet update(Long id,Tweet tweet);

    void delete(Tweet tweet);
}
