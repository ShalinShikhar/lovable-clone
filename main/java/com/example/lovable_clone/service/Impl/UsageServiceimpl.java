package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.example.lovable_clone.dto.subscription.UsageTodayResponse;
import com.example.lovable_clone.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceimpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
