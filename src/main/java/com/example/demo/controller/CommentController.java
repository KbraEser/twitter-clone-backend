package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private CommentService commentService;
    private UserService userService;
    private TweetService tweetService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService, TweetService tweetService) {
        this.commentService = commentService;
        this.userService = userService;
        this.tweetService = tweetService;
    }

    @PostMapping
    public Comment save(@Valid @RequestBody CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());

        User user = userService.findById(commentRequest.getUserId());
        newComment.setUser(user);

        Tweet tweet= tweetService.findById(commentRequest.getTweetId());
        newComment.setTweet(tweet);

        return commentService.save(newComment);

    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable Long id, @Valid @RequestBody CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        return commentService.update(id, newComment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        Comment comment = commentService.findById(id);
        commentService.delete(comment.getId());
    }




}
