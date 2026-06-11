package com.example.demo.dto;

import com.example.demo.entity.Comment;
import com.example.demo.entity.Tweet;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class LikeRequest {
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
