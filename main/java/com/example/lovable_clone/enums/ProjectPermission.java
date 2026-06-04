package com.example.lovable_clone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectPermission {

    VIEW("project:view"),
    EDIT("project:edit"),
    DELETE("project:delete"),
    MANAGE_MEMBER("project:manage_members"),
    VIEW_MEMBERS("project:view_members");

    private final String value;


}
