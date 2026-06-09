package com.example.demo.repository;

import com.example.demo.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Long> {

    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.tweet.id = :tweetId ")
    Optional<Like> findByUserIdAndTweetId(@Param("userId") Long userId,@Param("tweetId") Long tweetId);

    @Query("SELECT l FROM Like l WHERE l.user.id= :userId AND l.comment.id = :commentId")
    Optional<Like> findByUserIdAndCommentId(@Param("userId") Long userId,@Param("commentId") Long commentId);

}
