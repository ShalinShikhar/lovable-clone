package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.members.InviteMemberRequest;
import com.example.lovable_clone.dto.members.MemberResponse;
import com.example.lovable_clone.dto.members.updateRoleRequest;
import com.example.lovable_clone.service.ProjectMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects/{project}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<MemberResponse> getProjectMemebers(@PathVariable Long projectId)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId,userId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> invitedMember(
        @PathVariable Long projectId,
        @RequestBody InviteMemberRequest request
    ){
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.inviteMember(projectId,request,userId));
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId,@PathVariable Long memberId,@RequestBody updateRoleRequest request)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId,memberId,request,userId));
    }
    @DeleteMapping("/{memberId}")
    public ResponseEntity<MemberResponse> deleteProjectMember(@PathVariable Long projectId,@PathVariable Long memberId,@RequestBody updateRoleRequest request)
    {
        Long userId=1L;
        return ResponseEntity.ok(projectMemberService.deleteProjectMember(projectId,memberId,request,userId));
    }
}
