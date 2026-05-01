package com.example.lovable_clone.entity;

import com.example.lovable_clone.enums.ProjectRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ProjectMember {

    ProjectMemberId id;
    Project project;
    User user;
    ProjectRole projectRole;

    Instant invitedAt;
    Instant acceptedAt;

}
