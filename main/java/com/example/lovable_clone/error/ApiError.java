package com.example.lovable_clone.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record ApiError(
    HttpStatus status,
    String mesaage,
    Instant timestamp,
    @JsonInclude(JsonInclude.Include.NON_NULL) List<ApiFieldError>errors
){

    public ApiError (HttpStatus status,String mesaage){
        this(status,mesaage,Instant.now(),null);
    }
    public ApiError (HttpStatus status,String mesaage,List<ApiFieldError> errors){
        this(status,mesaage,Instant.now(),errors);
    }
}
record ApiFieldError(String field,String message){};
