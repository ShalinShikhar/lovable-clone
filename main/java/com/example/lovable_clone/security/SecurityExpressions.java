package com.example.lovable_clone.security;

import com.example.lovable_clone.enums.ProjectRole;
import com.example.lovable_clone.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("security")
@RequiredArgsConstructor
public class SecurityExpressions {

    private final ProjectMemberRepository projectMemberRepository;
    private final AuthUtil authUtil;
    public boolean canViewProject(Long projectId)
    {
        Long userId=authUtil.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId,userId).map(role->role.equals(ProjectRole.VIEWER)|| role
                .equals(ProjectRole.EDITOR) || role.equals(ProjectRole.OWNER)).orElse(false);
    }

    public boolean canEditProject(Long projectId)
    {
        Long userId=authUtil.getCurrentUserId();
        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId,userId).map(role-> role
                .equals(ProjectRole.EDITOR) || role.equals(ProjectRole.OWNER)).orElse(false);

    }
}
