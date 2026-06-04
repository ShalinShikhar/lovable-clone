package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.members.InviteMemberRequest;
import com.example.lovable_clone.dto.members.MemberResponse;
import com.example.lovable_clone.dto.members.updateRoleRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, updateRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
