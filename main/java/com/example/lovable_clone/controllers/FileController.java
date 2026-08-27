package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.project.FileContentResponse;
import com.example.lovable_clone.dto.project.FileNode;
import com.example.lovable_clone.dto.project.FileTreeResponse;
import com.example.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
@RequiredArgsConstructor
public class FileController {
    private final ProjectFileService projectFileService;



    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId)
    {

        return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> getFile(@PathVariable Long projectId, @RequestParam String path)
    {
            Long userId=1L;
            return ResponseEntity.ok(projectFileService.getFileContent(projectId,path));
    }
}
