package com.example.demo.service;

import com.example.demo.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment save(Comment comment);
    Comment findById(Long id);
    List<Comment> findAllByTweetId(Long tweetId);
    Comment update(Long id, Long userId, Comment comment);
    void delete(Long id, Long userId);
}
