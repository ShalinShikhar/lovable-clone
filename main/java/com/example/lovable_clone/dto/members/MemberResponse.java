package com.example.lovable_clone.dto.members;

import com.example.lovable_clone.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(
        Long userId,
        String username,
        String name,
        Instant invitedAt,
        ProjectRole projectRole
) {
}
