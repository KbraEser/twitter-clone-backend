package com.example.demo.dto;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Tweet;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequest {
    @NotNull(message = "The user ID information for the person who liked the post cannot be empty.")
    private Long userId;

    private Tweet tweet;

    private Comment comment;

    @AssertTrue(
            message = "A like must be for either a tweet or a comment."
    )
    public boolean isValidLikeTarget() {
        boolean hasTweet = (tweet != null && tweet.getId() != null);
        boolean hasComment = (comment != null && comment.getId() != null);
        return hasTweet || hasComment;
    }
}
