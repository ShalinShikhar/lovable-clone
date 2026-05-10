package com.example.lovable_clone.dto.members;

import com.example.lovable_clone.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole role
) {
}
