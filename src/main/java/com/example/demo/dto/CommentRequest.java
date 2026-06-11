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

    @NotNull(message = "The tweet ID being commented on cannot be empty.")
    private Long tweetId;

}
