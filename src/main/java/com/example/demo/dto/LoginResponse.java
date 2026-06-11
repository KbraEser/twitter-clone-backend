package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {
    private final String jwtToken;
    private final Long userId;
    private final String email;
    private final String name;
    private final String surname;
}
