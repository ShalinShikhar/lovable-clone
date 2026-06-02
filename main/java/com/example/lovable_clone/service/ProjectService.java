package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.project.ProjectRequest;
import com.example.lovable_clone.dto.project.ProjectResponse;
import com.example.lovable_clone.dto.project.ProjectSummaryResponse;

import java.util.List;


public interface ProjectService {


    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getUserProjectById(Long id);

    ProjectResponse updateProject(long id, ProjectRequest request);

    void softDelete(Long id);

    ProjectResponse createProject(ProjectRequest request);
}
