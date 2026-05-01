package com.example.lovable_clone.entity;

import com.example.lovable_clone.enums.MessageRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ChatMessage {

    Long id;
    ChatSession chatSession;
    String content;
    String toolCalls;// json array of tool calls
    Integer tokensUsed;
    Instant createdAt;

    MessageRole role;//whose msg is this

}
