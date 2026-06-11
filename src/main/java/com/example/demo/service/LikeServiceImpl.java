package com.example.demo.service;

import com.example.demo.entity.Like;
import com.example.demo.exceptions.ApiException;
import com.example.demo.repository.LikeRepository;
import com.example.demo.util.TwitterValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeServiceImpl implements LikeService {
    private LikeRepository likeRepository;


    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }




    @Override
    public Like save(Like like) {

       Optional<Like> savedLike;

       if(like.getTweet() != null){
             savedLike = likeRepository.findByUserIdAndTweetId(like.getUser().getId(),like.getTweet().getId());
       }else {
             savedLike=likeRepository.findByUserIdAndCommentId(like.getUser().getId(),like.getComment().getId());
       }

       if(savedLike.isPresent()){
           throw new ApiException("You have already liked this!", HttpStatus.BAD_REQUEST);
       }

       return likeRepository.save(like);

    }

    @Override
    public void deleteTweetLike(Long userId, Long tweetId) {
        Like like = likeRepository.findByUserIdAndTweetId(userId, tweetId)
                .orElseThrow(() -> new ApiException("Like not found for this tweet!", HttpStatus.NOT_FOUND));
        likeRepository.delete(like);
    }

    @Override
    public void deleteCommentLike(Long userId, Long commentId) {
        Like like = likeRepository.findByUserIdAndCommentId(userId, commentId).orElseThrow(() -> new ApiException("Like not found for this comment!", HttpStatus.NOT_FOUND));
        likeRepository.delete(like);
    }

    @Override
    public Like findById(Long id) {
        TwitterValidation.validateId(id);
      return   likeRepository.findById(id).orElseThrow(() -> new ApiException("Like not found for this id!", HttpStatus.NOT_FOUND));

    }
}
