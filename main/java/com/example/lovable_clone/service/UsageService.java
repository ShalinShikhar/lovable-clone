package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.example.lovable_clone.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    void recordTokenUssage(Long userId,int actualTokens);
    void checkDailyTokenUsage();
}
