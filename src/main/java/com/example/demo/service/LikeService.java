package com.example.demo.service;

import com.example.demo.entity.Like;
import com.example.demo.entity.User;

public interface LikeService {

    Like save(Like like);
    void deleteTweetLike(Long userId, Long tweetId);
    void deleteCommentLike(Long userId, Long commentId);
    Like findById(Long id);


}
