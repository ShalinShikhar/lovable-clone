package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.members.InviteMemberRequest;
import com.example.lovable_clone.dto.members.MemberResponse;
import com.example.lovable_clone.dto.members.updateRoleRequest;
import com.example.lovable_clone.entity.Project;
import com.example.lovable_clone.entity.ProjectMember;
import com.example.lovable_clone.entity.ProjectMemberId;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.mapper.ProjectMemberMapper;
import com.example.lovable_clone.mapper.UserMapper;
import com.example.lovable_clone.repository.ProjectMemberRepository;
import com.example.lovable_clone.repository.ProjectRepository;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.service.ProjectMemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level=AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;

    @Override

    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        Project project=getAccessibleProjectById(projectId,userId);
        List<MemberResponse> memberResponseList=projectMemberRepository.findByIdProjectId(projectId).stream().map(projectMemberMapper::toProjectMemberResponseFromMember).toList();
        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId) {
        Project project=getAccessibleProjectById(projectId,userId);

        User invitee = userRepository.findByUsername(request.username()).orElseThrow();
        if(invitee.getId().equals(userId))
        {
            throw new RuntimeException("cannot invite yourself");

        }
        ProjectMemberId projectMemberId=new ProjectMemberId(projectId,invitee.getId());
        if(projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("Cannot invite once again");
        }
        ProjectMember projectMember=ProjectMember.builder().id(projectMemberId).project(project).user(invitee).projectRole(request.role()).invitedAt(Instant.now()).build();
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);

    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, updateRoleRequest request, Long userId) {
        Project project=getAccessibleProjectById(projectId,userId);

        ProjectMemberId projectMemberId=new ProjectMemberId(projectId,memberId);
        ProjectMember projectMember=projectMemberRepository.findById(projectMemberId).orElseThrow();
        projectMember.setProjectRole(request.role());
        projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    public void removeProjectMember(Long projectId, Long memberId, Long userId) {
        Project project=getAccessibleProjectById(projectId,userId);

        ProjectMemberId projectMemberId=new ProjectMemberId(projectId,memberId);
        if(!projectMemberRepository.existsById(projectMemberId))
        {
            throw new RuntimeException("no member with id exist");
        }
        projectMemberRepository.deleteById(projectMemberId);

    }
    public Project getAccessibleProjectById(Long projectId, Long userId)
    {
        return projectRepository.findAccessibleProjectById(projectId,userId).orElseThrow();

    }
}
