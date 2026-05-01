package com.example.lovable_clone.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;

//made all the fields private automatically
@FieldDefaults(level = AccessLevel.PRIVATE) // how ur fields should be
@Getter
@Setter
public class User {

    Long id;
    String email;
    String passwordHash;
    String name;
    String avatarUrl;
    Instant createdAt;
    Instant updatedAt;
    Instant deletedAt;//soft delete(not deleted from DB but get rid of the user)

}
