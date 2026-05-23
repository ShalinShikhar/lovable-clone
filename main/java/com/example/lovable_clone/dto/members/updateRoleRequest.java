package com.example.lovable_clone.dto.members;

import com.example.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record updateRoleRequest(
        @NotNull ProjectRole role
) {
}
