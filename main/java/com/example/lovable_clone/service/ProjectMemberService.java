package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.members.InviteMemberRequest;
import com.example.lovable_clone.dto.members.MemberResponse;
import com.example.lovable_clone.dto.members.updateRoleRequest;
import org.jspecify.annotations.Nullable;

public interface ProjectMemberService {
    MemberResponse getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request, Long userId);

    MemberResponse updateMemberRole(Long projectId, Long memberId, updateRoleRequest request, Long userId);

    MemberResponse deleteProjectMember(Long projectId, Long memberId, updateRoleRequest request, Long userId);
}
