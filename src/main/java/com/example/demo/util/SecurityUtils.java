package com.example.demo.util;

import com.example.demo.entity.User;
import com.example.demo.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        throw new ApiException("Authenticated user not found", HttpStatus.UNAUTHORIZED);
    }
}
