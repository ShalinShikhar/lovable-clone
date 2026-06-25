package com.example.lovable_clone.entity;

import com.example.lovable_clone.enums.MessageRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Table(name = "chat_message")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumns({
            @JoinColumn(name = "project_id",referencedColumnName = "project_id",nullable = false),
            @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
    })
    ChatSession chatSession;

    String content;

    String toolCalls;// json array of tool calls

    Integer tokensUsed;

    Instant createdAt;

    MessageRole role;//whose msg is this

}
