package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.auth.AuthResponse;
import com.example.lovable_clone.dto.auth.LoginRequest;
import com.example.lovable_clone.dto.auth.SignUpRequest;
import com.example.lovable_clone.dto.auth.UserProfileResponse;
import com.example.lovable_clone.service.AuthService;
import com.example.lovable_clone.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request)
    {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile()
    {
        long userId=1L;
        return ResponseEntity.ok(userService.getProfile());
    }
}
