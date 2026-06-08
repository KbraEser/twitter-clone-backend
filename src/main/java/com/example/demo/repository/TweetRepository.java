package com.example.demo.repository;

import com.example.demo.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TweetRepository extends JpaRepository<Tweet,Long> {
    @Query("SELECT t FROM Tweet t WHERE t.user.id =:id")
    List<Tweet> findByUserId(Long id);
}
