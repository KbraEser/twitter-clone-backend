package com.example.demo.controller;

import com.example.demo.dto.LikeRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Like;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LikeRepository;
import com.example.demo.service.CommentService;
import com.example.demo.service.LikeService;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LikeController {

    private LikeService likeService;
    private CommentService commentService;
    private TweetService tweetService;
    private UserService userService;


    @Autowired
    private LikeController(LikeService likeService, CommentService commentService, TweetService tweetService, UserService userService) {
        this.likeService = likeService;
        this.commentService = commentService;
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @PostMapping("/like")
    public Like like(@Valid @RequestBody LikeRequest likeRequest) {
        Like like = new Like();

        User user = userService.findById(likeRequest.getUserId());
        like.setUser(user);

        if(likeRequest.getComment() != null){
            Comment comment = commentService.findById(likeRequest.getComment().getId());
            like.setComment(comment);
        } else if (likeRequest.getTweet() != null) {
            Tweet tweet = tweetService.findById(likeRequest.getTweet().getId());
            like.setTweet(tweet);
        }else {
            throw new ApiException("Provide tweetId or commentId!", HttpStatus.BAD_REQUEST);
        }

        return likeService.save(like);
    }

    @PostMapping("/dislike")
    public String dislike(@Valid @RequestBody LikeRequest likeRequest) {

        if (likeRequest.getComment() != null && likeRequest.getComment().getId() != null) {
            likeService.deleteCommentLike(likeRequest.getUserId(), likeRequest.getComment().getId());
        }

        else if (likeRequest.getTweet() != null && likeRequest.getTweet().getId() != null) {
            likeService.deleteTweetLike(likeRequest.getUserId(), likeRequest.getTweet().getId());
        }
        else {
            throw new ApiException("Provide tweetId or commentId!", HttpStatus.BAD_REQUEST);
        }

        return "Like successfully removed!";
    }
}
