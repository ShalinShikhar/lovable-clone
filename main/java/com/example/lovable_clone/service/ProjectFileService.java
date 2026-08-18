package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.project.FileContentResponse;
import com.example.lovable_clone.dto.project.FileNode;
import com.example.lovable_clone.dto.project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
