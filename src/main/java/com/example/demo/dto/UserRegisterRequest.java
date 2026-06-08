package com.example.demo.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @NotBlank(message = "Name field cannot be left blank.")
    private String name;

    @NotBlank(message = "Surname field cannot be left blank.")
    private String surname;

    @NotBlank(message = "Email field cannot be left blank.")
    private String email;

    private String picture;

    @NotBlank(message = "Password field cannot be left blank.")
    @Size(min = 6, max = 20, message = "Password must be at least 6 and at most 20 characters.")
    private String password;
}
