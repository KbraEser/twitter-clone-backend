package com.example.demo.controller;

import com.example.demo.dto.TweetRequest;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.TweetService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tweet")
public class TweetController {


    private TweetService tweetService;
    private UserService userService;

    @Autowired
    public TweetController(TweetService tweetService, UserService userService) {
        this.tweetService = tweetService;
        this.userService = userService;
    }

    @PostMapping
    public Tweet save(@Valid @RequestBody TweetRequest tweetRequest){
        Tweet tweet =new Tweet();
        tweet.setContent(tweetRequest.getContent());

        User user = userService.findById(tweetRequest.getUserId());
        tweet.setUser(user);
        if(tweetRequest.getTweetId() != null){
            Tweet parentTweet = tweetService.findById(tweetRequest.getTweetId());
            tweet.setParentTweet(parentTweet);
        }
        return tweetService.save(tweet);

    }

    @GetMapping("/findByUserId")
    public List<Tweet> findAllByUserId(@RequestParam Long id){
        return tweetService.findAllByUserId(id);
    }

    @GetMapping("/findById")
    public Tweet findById(@RequestParam Long id){
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    public Tweet update(@PathVariable Long id, @Valid @RequestBody TweetRequest tweetRequest){
        Tweet updatedData = new Tweet();
        updatedData.setContent(tweetRequest.getContent());
        return tweetService.update(id,updatedData);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        Tweet deletedData = tweetService.findById(id);
        tweetService.delete(deletedData);
    }


}
