package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.auth.AuthResponse;
import com.example.lovable_clone.dto.auth.LoginRequest;
import com.example.lovable_clone.dto.auth.SignUpRequest;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.error.BadRequestException;
import com.example.lovable_clone.mapper.UserMapper;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AuthServiceimpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthUtil authUtil;
    AuthenticationManager authenticationManager;
    @Override
    public AuthResponse signup(SignUpRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(user->{
            throw new BadRequestException("User already exist with username"+request.username());

        });
        User user=userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token=authUtil.generateAccessToken(user);
        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(),request.password())
        );
        User user=(User) authentication.getPrincipal();
        String token=authUtil.generateAccessToken(user);
        return new AuthResponse(token,userMapper.toUserProfileResponse(user));
    }
}
