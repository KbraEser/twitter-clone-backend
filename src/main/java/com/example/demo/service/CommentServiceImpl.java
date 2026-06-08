package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment findById(Long id) {
        TwitterValidation.validateId(id);
        return commentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Comment not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public Comment update(Long id, Comment comment) {
        TwitterValidation.validateId(id);
        Optional<Comment> optionalComment = commentRepository.findById(id);

        Comment existingComment = optionalComment.orElseThrow(()->new ApiException("Comment to update not found" +id, HttpStatus.NOT_FOUND));

        existingComment.setContent(comment.getContent());
        return commentRepository.save(existingComment);
    }

    @Override
    public void delete(Long id) {
        TwitterValidation.validateId(id);
        Comment commentToDelete = findById(id);
        commentRepository.delete(commentToDelete);
    }
}
