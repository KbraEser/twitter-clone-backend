package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("SELECT DISTINCT c FROM Comment c JOIN FETCH c.user WHERE c.tweet.id = :tweetId ORDER BY c.id ASC")
    List<Comment> findByTweetId(Long tweetId);
}
