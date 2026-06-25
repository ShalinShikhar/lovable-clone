package com.example.lovable_clone.dto.chat;

import lombok.Builder;
import lombok.Getter;



public record ChatRequest(
        String message,
        Long projectId
) {
}
