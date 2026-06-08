package com.example.demo.util;

import com.example.demo.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class TwitterValidation {
    public static void validateId(Long id){
        if(id == null ||  id <= 0){
            throw new ApiException("Invalid ID provided: "+id, HttpStatus.BAD_REQUEST);
        }
    }


}
