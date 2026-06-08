package com.example.demo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeRequest {
    @NotNull(message = "The user ID information for the person who liked the post cannot be empty.")
    private Long userId;

    private Long tweetsId;

    private Long commentId;

    @AssertTrue(
            message = "A like must be for either a tweet or a comment."
    )
    public boolean isValidLikeTarget() {
        return tweetsId != null || commentId != null;
    }
}
