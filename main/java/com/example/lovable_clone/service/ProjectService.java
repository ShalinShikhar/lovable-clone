package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.project.ProjectRequest;
import com.example.lovable_clone.dto.project.ProjectResponse;
import com.example.lovable_clone.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

public interface ProjectService {


    ProjectSummaryResponse getUserProjects(Long userId);

    ProjectResponse getUserProjectById(Long id,Long userId);

    ProjectResponse updateProject(long id, ProjectRequest request, Long userId);

    void softDelete(Long id, Long userId);

    ProjectResponse createProject(ProjectRequest request, Long userId);
}
