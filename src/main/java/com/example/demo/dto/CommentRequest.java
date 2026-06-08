package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Comment field cannot be left blank.")
    @Size( max = 200, message = "Comment must be at most 200 characters.")
    private String content;

    @NotNull(message = "The commenter's user ID cannot be empty.")
    private Long userId;

    @NotNull(message = "The tweet ID being commented on cannot be empty.")
    private Long tweetId;

}
