package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.project.ProjectRequest;
import com.example.lovable_clone.dto.project.ProjectResponse;
import com.example.lovable_clone.dto.project.ProjectSummaryResponse;
import com.example.lovable_clone.entity.Project;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.mapper.ProjectMapper;
import com.example.lovable_clone.repository.ProjectRepository;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.service.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository  userRepository;
    ProjectMapper projectMapper;

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
//            return projectRepository.findAllAccessibleByUser(userId).stream().map(projectMapper::toProjectSummaryResponse).collect(Collectors.toList());
        return projectMapper.toListOfProjectSummaryResponse(projectRepository.findAllAccessibleByUser(userId));

    }

    @Override
    public ProjectResponse getUserProjectById(Long id, Long userId) {
        Project project=projectRepository.findAccessibleProjectById(id,userId).orElseThrow();
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(long id, ProjectRequest request, Long userId) {
        Project project=projectRepository.findAccessibleProjectById(id,userId).orElseThrow();
        if(!project.getOwner().getId().equals(userId))
        {
            throw new RuntimeException("U are not allowed to delete");
        }
        project.setName(request.name());
        project=projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    public void softDelete(Long id, Long userId) {
            Project project=getAccessibleProjectById(id,userId);
            if(!project.getOwner().getId().equals(userId))
            {
                throw new RuntimeException("U are not allowed to delete");
            }
            project.setDeletedAt(Instant.now());
            projectRepository.save(project);
    }

    @Override

    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User owner=userRepository.findById(userId).orElseThrow();
        Project project=Project.builder().name(request.name()).owner(owner).isPublic(false).build();
        project=projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    //Internal function
    public Project getAccessibleProjectById(Long projectId,Long userId)
    {
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow();
    }
}
