package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.chat.StreamResponse;
import com.example.lovable_clone.entity.*;
import com.example.lovable_clone.enums.ChatEventType;
import com.example.lovable_clone.enums.MessageRole;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.llm.LlmResponseParser;
import com.example.lovable_clone.llm.PromptUtil;
import com.example.lovable_clone.llm.advisors.FileTreeContextAdvisor;
import com.example.lovable_clone.llm.tools.CodeGenerationTools;
import com.example.lovable_clone.repository.*;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.AiGenerationService;
import com.example.lovable_clone.service.UsageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import com.example.lovable_clone.service.ProjectFileService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {

    ChatClient chatClient;
    AuthUtil authUtil;
    ProjectFileService projectFileService;
    FileTreeContextAdvisor fileTreeContextAdvisor;
    LlmResponseParser llmResponseParser;
    ChatSessionRepository chatSessionRepository;
    ProjectRepository projectRepository;
    UserRepository userRepository;
    ChatMessageRepository chatMessageRepository;
    ChatEventRepository chatEventRepository;
    UsageService usageService;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String message, Long projectId) {
        usageService.checkDailyTokenUsage();
        Long userId=authUtil.getCurrentUserId();
        ChatSession chatSession=createChatSessionIfNotExist(projectId,userId);
        Map<String,Object> advisorParam= Map.of(
                "userId",userId,
                "projectId",projectId
        );
        StringBuilder fullResponseBuffer=new StringBuilder();
        CodeGenerationTools codeGenerationTools=new CodeGenerationTools(projectFileService,projectId);
        AtomicReference<Long> startTime=new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime=new AtomicReference<>();
        AtomicReference<Usage> usageRef=new AtomicReference<>();
        return chatClient.prompt().system(PromptUtil.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
                .tools(codeGenerationTools)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParam);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    String content=chatResponse.getResult().getOutput().getText();
                    if(content!=null && !content.isEmpty() && endTime.get()==0) {
                        endTime.set(System.currentTimeMillis());
                    }
                    if(chatResponse.getMetadata().getUsage()!=null)
                    {
                          usageRef.set(chatResponse.getMetadata().getUsage());
                    }
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    Schedulers.boundedElastic().schedule(
                            ()->{
                                long duration=(endTime.get()-startTime.get())/1000;
                                parseAndSaveFiles(fullResponseBuffer.toString(),projectId);
                                finalizeChat(message,chatSession,fullResponseBuffer.toString(),duration,usageRef.get());
                            }
                    );

                })
                .doOnError(error->log.error("error durring streaming for project"))
                .map(chatResponse -> {
                    String text=chatResponse.getResult().getOutput().getText();
                    return new StreamResponse(text!=null ? text : "");
                });

    }
    private void finalizeChat(String userMessage, ChatSession chatSession,String fullText,long duration,Usage usage){
        Long projectId=chatSession.getProject().getId();
        //save token usage
        if(usage!=null)
        {
            int totalToken=usage.getTotalTokens();
            usageService.recordTokenUssage(chatSession.getUser().getId(),totalToken);
        }

        //save the user message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );
        ChatMessage assistantChatMessage=ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message Here.....")
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();
        assistantChatMessage=chatMessageRepository.save(assistantChatMessage);
        List<ChatEvent> chatEventList=    llmResponseParser.parseChatEvents(fullText,assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder().chatEventType(ChatEventType.THOUGHT).chatMessage(assistantChatMessage).content("thought for "+duration+"s").sequenceOrder(0).build());
        chatEventList.stream().filter(e->e.getChatEventType()== ChatEventType.FILE_EDIT).forEach(e->projectFileService.saveFile(projectId,e.getFilePath(),e.getContent()));

        chatEventRepository.saveAll(chatEventList);


    }
    private void parseAndSaveFiles(String fullResponse, Long projectId) {
//            String dummy= """
//                    <message>I'm going to read the files and genrate the code</message>
//                    <file path="">
//                        import App from './App.jsx'
//                        ......
//                    </files>
//                    message>I'm going to read the files and genrate the code</message>
//                    <file path="">
//                        import App from './App.jsx'
//                        ......
//                    </files>
//                    """;


        Matcher matcher=FILE_TAG_PATTERN.matcher(fullResponse);

        while(matcher.find()){
            String filePath = matcher.group(1);
            String fileContent = matcher.group(2);
            projectFileService.saveFile(projectId,filePath,fileContent);
        }

    }

    private ChatSession createChatSessionIfNotExist(Long projectId, Long userId) {
        ChatSessionId chatSessionId=new ChatSessionId(projectId,userId);
        ChatSession chatSession=chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession==null)
        {
            Project project=projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("Project",projectId.toString()));
            User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User",userId.toString()));
            chatSession=ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession=chatSessionRepository.save(chatSession);
        }
        return chatSession;


    }
}
