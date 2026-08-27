package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.deploy.DeployResponse;

public interface DeployementService {
        DeployResponse deploy(Long projectId);
}
