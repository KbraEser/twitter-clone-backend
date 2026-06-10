package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name field cannot be left blank.")
        String name,
        @NotBlank(message = "Surname field cannot be left blank.")
        String surname,
        @NotBlank(message = "Email field cannot be left blank.")
        String email,
        @NotBlank(message = "Password field cannot be left blank.")
        @Size(min = 6, max = 20, message = "Password must be at least 6 and at most 20 characters.")
        String password,
        String picture
) {
}
