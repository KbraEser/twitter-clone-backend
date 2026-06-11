package com.example.demo.controller;

import com.example.demo.dto.TweetRequest;
import com.example.demo.entity.Tweet;
import com.example.demo.entity.User;
import com.example.demo.service.TweetService;
import com.example.demo.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tweet")
public class TweetController {

    private final TweetService tweetService;

    @Autowired
    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @GetMapping("/findAll")
    public List<Tweet> findAll() {
        return tweetService.findAll();
    }

    @PostMapping
    public Tweet save(@Valid @RequestBody TweetRequest tweetRequest) {
        Tweet tweet = new Tweet();

        String content = tweetRequest.getContent();
        tweet.setContent(content != null && !content.trim().isEmpty() ? content : null);

        User user = SecurityUtils.getCurrentUser();
        tweet.setUser(user);

        if (tweetRequest.getTweetId() != null) {
            Tweet parentTweet = tweetService.findById(tweetRequest.getTweetId());
            tweet.setParentTweet(parentTweet);
        }

        return tweetService.save(tweet);
    }

    @GetMapping("/findByUserId")
    public List<Tweet> findAllByUserId(@RequestParam Long id) {
        return tweetService.findAllByUserId(id);
    }

    @GetMapping("/findById")
    public Tweet findById(@RequestParam Long id) {
        return tweetService.findById(id);
    }

    @PutMapping("/{id}")
    public Tweet update(@PathVariable Long id, @Valid @RequestBody TweetRequest tweetRequest) {
        Tweet updatedData = new Tweet();
        updatedData.setContent(tweetRequest.getContent());
        return tweetService.update(id, SecurityUtils.getCurrentUser().getId(), updatedData);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        tweetService.delete(id, SecurityUtils.getCurrentUser().getId());
    }
}
