package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.CommentService;
import com.example.demo.service.TweetService;
import com.example.demo.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final TweetService tweetService;

    @Autowired
    public CommentController(CommentService commentService, TweetService tweetService) {
        this.commentService = commentService;
        this.tweetService = tweetService;
    }

    @GetMapping("/findByTweetId")
    public List<Comment> findAllByTweetId(@RequestParam Long id) {
        return commentService.findAllByTweetId(id);
    }

    @PostMapping
    public Comment save(@Valid @RequestBody CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());

        User user = SecurityUtils.getCurrentUser();
        newComment.setUser(user);

        Tweet tweet = tweetService.findById(commentRequest.getTweetId());
        newComment.setTweet(tweet);

        return commentService.save(newComment);
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable Long id, @Valid @RequestBody CommentRequest commentRequest) {
        Comment newComment = new Comment();
        newComment.setContent(commentRequest.getContent());
        return commentService.update(id, SecurityUtils.getCurrentUser().getId(), newComment);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        commentService.delete(id, SecurityUtils.getCurrentUser().getId());
    }
}
