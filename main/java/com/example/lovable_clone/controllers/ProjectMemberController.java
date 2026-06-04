package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.members.InviteMemberRequest;
import com.example.lovable_clone.dto.members.MemberResponse;
import com.example.lovable_clone.dto.members.updateRoleRequest;
import com.example.lovable_clone.repository.ProjectMemberRepository;
import com.example.lovable_clone.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private final ProjectMemberRepository projectMemberRepository;
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> invitedMember(
        @PathVariable Long projectId,
        @RequestBody @Valid InviteMemberRequest request
    ){
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.inviteMember(projectId,request));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId,@PathVariable Long memberId,@RequestBody @Valid updateRoleRequest request)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId,memberId,request));
    }
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long projectId,@PathVariable Long memberId)
    {
        Long userId=1L;
        projectMemberService.removeProjectMember(projectId,memberId);
        return ResponseEntity.noContent().build();
    }
}
