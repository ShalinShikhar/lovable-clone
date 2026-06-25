package com.example.lovable_clone.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@ToString
@Embeddable
public class ChatSessionId {
    Long projectId;
    Long userId;
}
