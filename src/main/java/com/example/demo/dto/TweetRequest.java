package com.example.demo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TweetRequest {
    @NotNull(message = "userId cannot be null")
    private Long userId;

    private String content;

    private Long tweetId;

    @AssertTrue(
            message = "Tweet content cannot be empty. If you want to leave it blank, you must retweet a tweet."
    )
    public boolean isValidTweet(){
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasTweetId = tweetId != null;
        return hasContent || hasTweetId ;
    }
}
