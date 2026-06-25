package com.example.lovable_clone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name="chat_session")
@Entity
public class ChatSession {

    @EmbeddedId
    ChatSessionId id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("projectId")
    @JoinColumn(name = "project_id",nullable = false,updatable = false)
    Project project;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id",nullable = false,updatable = false)
    User user;


    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    Instant deletedAt;
}
