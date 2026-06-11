package com.example.demo.service;

import com.example.demo.entity.Tweet;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.TweetRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private TweetRepository tweetRepository;

    @Autowired
    public TweetServiceImpl(TweetRepository tweetRepository) {
        this.tweetRepository = tweetRepository;
    }


    @Override
    public Tweet save(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    @Override
    public List<Tweet> findAll() {
        return tweetRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<Tweet> findAllByUserId(Long id) {
        TwitterValidation.validateId(id);
        return tweetRepository.findByUserId(id);
    }

    @Override
    public Tweet findById(Long id) {
        TwitterValidation.validateId(id);
        return  tweetRepository.findById(id)
                .orElseThrow(()-> new ApiException("Tweet not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public Tweet update(Long id, Long userId, Tweet tweet) {
        TwitterValidation.validateId(id);
        TwitterValidation.validateId(userId);

        Tweet existingTweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Tweet to update not found with ID: " + id, HttpStatus.NOT_FOUND));

        if (!existingTweet.getUser().getId().equals(userId)) {
            throw new ApiException("You can only update your own tweet.", HttpStatus.FORBIDDEN);
        }

        existingTweet.setContent(tweet.getContent());
        existingTweet.setParentTweet(tweet.getParentTweet());

        return tweetRepository.save(existingTweet);
    }

    @Override
    public void delete(Long id, Long userId) {
        TwitterValidation.validateId(id);
        TwitterValidation.validateId(userId);

        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ApiException("Tweet not found with ID: " + id, HttpStatus.NOT_FOUND));

        if (!tweet.getUser().getId().equals(userId)) {
            throw new ApiException("You can only delete your own tweet.", HttpStatus.FORBIDDEN);
        }

        tweetRepository.delete(tweet);
    }
}
