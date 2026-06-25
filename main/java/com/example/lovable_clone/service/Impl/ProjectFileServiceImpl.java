package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.project.FileContentResponse;
import com.example.lovable_clone.dto.project.FileNode;
import com.example.lovable_clone.service.ProjectFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProjectFileServiceImpl implements ProjectFileService {
    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String filePath, String fileContent) {
        log.info("Saving file {}",filePath);
        //save the file metadata to postgres
        // save the content in minio
    }
}
