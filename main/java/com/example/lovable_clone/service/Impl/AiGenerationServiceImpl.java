package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.llm.PromptUtil;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.AiGenerationService;
import com.example.lovable_clone.service.ProjectFileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
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

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile(
            "<file path=\"([^\"]+)\">(.*?)</file>",
            Pattern.DOTALL
    );

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String message, Long projectId) {
        Long userId=authUtil.getCurrentUserId();
        createChatSessionIfNotExist(projectId,userId);
        Map<String,Object> advisorParam= Map.of(
                "userId",userId,
                "projectId",projectId
        );
        StringBuilder fullResponseBuffer=new StringBuilder();
        return chatClient.prompt().system(PromptUtil.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
                .advisors(
                        advisorSpec -> {
                            advisorSpec.params(advisorParam);
                        }
                )
                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    String content=chatResponse.getResult().getOutput().getText();
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(()->{
                    Schedulers.boundedElastic().schedule(
                            ()->{
                                parseAndSaveFiles(fullResponseBuffer.toString(),projectId);
                            }
                    );

                })
                .doOnError(error->log.error("error durring streaming for project"))
                .map(chatResponse -> Objects.requireNonNull(chatResponse.getResult()).getOutput().getText());

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
            String filePath=matcher.group(0);
            String fileContent=matcher.group(1);
            projectFileService.saveFile(projectId,filePath,fileContent);
        }

    }

    private void createChatSessionIfNotExist(Long projectId, Long userId) {


    }
}
