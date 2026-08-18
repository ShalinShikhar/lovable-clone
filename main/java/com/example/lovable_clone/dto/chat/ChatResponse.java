package com.example.lovable_clone.dto.chat;

import com.example.lovable_clone.entity.ChatEvent;
import com.example.lovable_clone.entity.ChatSession;
import com.example.lovable_clone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        MessageRole role,
        List<ChatEventResponse> events,
        String content,
        Integer tokenUsed,
        Instant createdAt


) {
}
