package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.project.ProjectRequest;
import com.example.lovable_clone.dto.project.ProjectResponse;
import com.example.lovable_clone.dto.project.ProjectSummaryResponse;
import com.example.lovable_clone.entity.Project;
import com.example.lovable_clone.entity.ProjectMember;
import com.example.lovable_clone.entity.ProjectMemberId;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.enums.ProjectRole;
import com.example.lovable_clone.error.BadRequestException;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.mapper.ProjectMapper;
import com.example.lovable_clone.repository.ProjectMemberRepository;
import com.example.lovable_clone.repository.ProjectRepository;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.ProjectService;
import com.example.lovable_clone.service.ProjectTemplateService;
import com.example.lovable_clone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;
    ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
//            return projectRepository.findAllAccessibleByUser(userId).stream().map(projectMapper::toProjectSummaryResponse).collect(Collectors.toList());
       Long userId= authUtil.getCurrentUserId();
        return projectMapper.toListOfProjectSummaryResponse(projectRepository.findAllAccessibleByUser(userId));

    }

    @Override
    @PreAuthorize("@security.canViewProject(#id)")
    public ProjectResponse getUserProjectById(Long id) {
        Long userId= authUtil.getCurrentUserId();
        Project project=getAccessibleProjectById(id,userId);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#id)")
    public ProjectResponse updateProject(long id, ProjectRequest request) {
        Long userId= authUtil.getCurrentUserId();
        Project project=getAccessibleProjectById(id,userId);;

        project.setName(request.name());
        project=projectRepository.save(project);
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#id)")
    public void softDelete(Long id ){
        Long userId= authUtil.getCurrentUserId();
        Project project=getAccessibleProjectById(id,userId);

            project.setDeletedAt(Instant.now());
            projectRepository.save(project);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        if(!subscriptionService.canCreateNewProject())
        {
            throw new BadRequestException("User can't create a new project with current plan upgrade plan now");
        }
        Long userId= authUtil.getCurrentUserId();
//        User owner=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User",userId.toString()));
        User owner=userRepository.getReferenceById(userId);

        Project project=Project.builder().name(request.name()).isPublic(false).build();
        project = projectRepository.save(project);
        ProjectMemberId projectMemberId=new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember=ProjectMember.builder().projectRole(ProjectRole.OWNER).user(owner).acceptedAt(Instant.now()).invitedAt(Instant.now()).project(project).id(projectMemberId).build();
        projectMemberRepository.save(projectMember);
        project=projectRepository.save(project);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    //Internal function
    public Project getAccessibleProjectById(Long projectId,Long userId)
    {
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow(()->new ResourceNotFoundException("Project",projectId.toString()));
    }
}
