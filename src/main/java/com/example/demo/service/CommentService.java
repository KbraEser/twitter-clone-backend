package com.example.demo.service;

import com.example.demo.entity.Comment;

public interface CommentService {
    Comment save(Comment comment);
    Comment findById(Long id);
    Comment update(Long id, Comment comment);
    void delete(Long id);
}
