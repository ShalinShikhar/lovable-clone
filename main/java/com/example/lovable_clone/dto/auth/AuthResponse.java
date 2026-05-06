package com.example.lovable_clone.dto.auth;

public record AuthResponse(String token,UserProfileResponse user)
{
}
//dummy new AuthResponse("",)//all args constructor