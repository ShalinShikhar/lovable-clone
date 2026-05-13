package com.example.lovable_clone.mapper;

import com.example.lovable_clone.dto.auth.UserProfileResponse;
import com.example.lovable_clone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserProfileResponse toUserProfileResponse(User user);
}
