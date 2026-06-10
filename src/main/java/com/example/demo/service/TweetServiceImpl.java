package com.example.demo.service;

import com.example.demo.entity.Tweet;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.TweetRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public Tweet update(Long id,Tweet tweet) {

        TwitterValidation.validateId(id);

        Optional<Tweet> tweetOptional = tweetRepository.findById(id);

        Tweet existingTweet =tweetOptional.orElseThrow(()->
                        new ApiException("Tweet to update not found with ID: " + id, HttpStatus.NOT_FOUND)
                );

        existingTweet.setContent(tweet.getContent());
        existingTweet.setParentTweet(tweet.getParentTweet());

        return tweetRepository.save(existingTweet);
    }

    @Override
    public void delete(Tweet tweet) {

        TwitterValidation.validateId(tweet.getId());
        tweetRepository.delete(tweet);
    }
}
