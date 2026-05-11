package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.auth.AuthResponse;
import com.example.lovable_clone.dto.auth.LoginRequest;
import com.example.lovable_clone.dto.auth.SignUpRequest;
import com.example.lovable_clone.service.AuthService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceimpl implements AuthService {
    @Override
    public AuthResponse signup(SignUpRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}
