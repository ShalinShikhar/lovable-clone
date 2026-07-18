package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.chat.ChatRequest;
import com.example.lovable_clone.dto.chat.ChatResponse;
import com.example.lovable_clone.entity.ChatMessage;
import com.example.lovable_clone.service.AiGenerationService;
import com.example.lovable_clone.service.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ChatController {
        AiGenerationService aiGenerationService;
        ChatService chatService;

        @PostMapping(value = "/api/chat/stream" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request){

            return aiGenerationService.streamResponse(request.message(),request.projectId()).map(data -> ServerSentEvent.<String>builder().data(data).build());

        }

        @GetMapping("/projects/{projectId}")
        public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId){
            return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
        }
    }
