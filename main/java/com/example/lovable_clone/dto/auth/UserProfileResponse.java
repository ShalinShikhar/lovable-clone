package com.example.lovable_clone.dto.auth;

public record UserProfileResponse
        (Long id,
         String email,
         String name,
         String avatarurl)
{
}
