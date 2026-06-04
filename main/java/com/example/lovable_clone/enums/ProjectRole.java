package com.example.lovable_clone.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.example.lovable_clone.enums.ProjectPermission.*;

@Getter
@RequiredArgsConstructor
public enum ProjectRole {
    EDITOR(Set.of(EDIT,VIEW,DELETE,VIEW_MEMBERS)),VIEWER(Set.of(VIEW,VIEW_MEMBERS)),OWNER(Set.of(VIEW,EDIT,DELETE,MANAGE_MEMBER,VIEW_MEMBERS));

    private final Set<ProjectPermission> permissions;

}
