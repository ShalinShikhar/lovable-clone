package com.example.lovable_clone.dto.auth;

public record SignUpRequest (
        String email,
        String name,
        String password
){
}
