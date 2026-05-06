package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {
    @Nullable UserProfileResponse getProfile();

}
