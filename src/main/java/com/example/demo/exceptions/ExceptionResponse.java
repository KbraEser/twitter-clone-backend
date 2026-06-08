package com.example.demo.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ExceptionResponse {
    public ExceptionResponse(String message) {
        this.message=message;
    }
    public ExceptionResponse(String message, int status, LocalDateTime dateTime) {
        this.message=message;
        this.status=status;
        this.dateTime=dateTime;
    }

    private int status;
    private String message;
    private LocalDateTime dateTime;
}
