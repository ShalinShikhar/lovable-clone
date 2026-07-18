package com.example.lovable_clone.llm.advisors;

import com.example.lovable_clone.dto.project.FileNode;
import com.example.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Map<String,Object> context=chatClientRequest.context();
        Long projectId=Long.parseLong(context.getOrDefault("projectId",0).toString());

        return streamAdvisorChain.nextStream(augmentRequestByFileTree(chatClientRequest,projectId));
    }

    private  ChatClientRequest augmentRequestByFileTree(ChatClientRequest request,Long projectId)
    {
        List<Message> incomingMessages=request.prompt().getInstructions();
        Message systemMessage=incomingMessages.stream().filter(m->m.getMessageType()== MessageType.SYSTEM).findFirst().orElse(null);
        List<Message> userMessages=incomingMessages.stream().filter(m->m.getMessageType() != MessageType.SYSTEM).toList();
        List<Message> allMessage=new ArrayList<>();

        //Add orignal system message
        if(systemMessage!=null)
        {
            allMessage.add(systemMessage);
        }

        List<FileNode> fileTree=projectFileService.getFileTree(projectId);
        String fileTreeContext="\n\n --- FILE_TREE ----\n"+fileTree.toString();

        allMessage.add(new SystemMessage(fileTreeContext));
        allMessage.addAll(userMessages);

        return  request.mutate().prompt(new Prompt(allMessage,request.prompt().getOptions())).build();

    }
    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
