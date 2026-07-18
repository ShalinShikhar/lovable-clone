package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.chat.ChatResponse;
import com.example.lovable_clone.entity.ChatMessage;
import com.example.lovable_clone.entity.ChatSession;
import com.example.lovable_clone.entity.ChatSessionId;
import com.example.lovable_clone.mapper.ChatMapper;
import com.example.lovable_clone.repository.ChatMessageRepository;
import com.example.lovable_clone.repository.ChatSessionRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceimpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId=authUtil.getCurrentUserId();
        ChatSession chatSession=chatSessionRepository.getReferenceById(new ChatSessionId(projectId,userId));
        List<ChatMessage> chatMessageList=chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
