package com.example.lovable_clone.dto.chat;

import com.example.lovable_clone.entity.ChatMessage;
import com.example.lovable_clone.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata
)
{
}
