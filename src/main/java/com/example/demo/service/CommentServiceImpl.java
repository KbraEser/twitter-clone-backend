package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<Comment> findAllByTweetId(Long tweetId) {
        TwitterValidation.validateId(tweetId);
        return commentRepository.findByTweetId(tweetId);
    }

    @Override
    public Comment update(Long id, Long userId, Comment comment) {
        TwitterValidation.validateId(id);
        TwitterValidation.validateId(userId);

        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Comment to update not found" + id, HttpStatus.NOT_FOUND));

        if (!existingComment.getUser().getId().equals(userId)) {
            throw new ApiException("You can only update your own comment.", HttpStatus.FORBIDDEN);
        }

        existingComment.setContent(comment.getContent());
        return commentRepository.save(existingComment);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        TwitterValidation.validateId(id);
        TwitterValidation.validateId(userId);

        Comment commentToDelete = commentRepository.findById(id)
                .orElseThrow(() -> new ApiException("Comment not found with ID: " + id, HttpStatus.NOT_FOUND));

        if (!commentToDelete.getUser().getId().equals(userId)) {
            throw new ApiException("You can only delete your own comment.", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(commentToDelete);
    }
}
