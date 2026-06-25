package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.project.FileContentResponse;
import com.example.lovable_clone.dto.project.FileNode;
import com.example.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectid}/files")
@RequiredArgsConstructor
public class FileController {
    private final ProjectFileService projectFileService;



    @GetMapping
    public ResponseEntity<List<FileNode>> getFileName(@PathVariable Long projectId)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectFileService.getFileTree(projectId,userId));
    }

    @GetMapping("/{*path}")
    public ResponseEntity<FileContentResponse> getFile(@PathVariable Long projectId, @PathVariable String path)
    {
            Long userId=1L;
            return ResponseEntity.ok(projectFileService.getFileContent(projectId,path,userId));
    }
}
