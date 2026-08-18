package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.subscription.PlanLimitsResponse;
import com.example.lovable_clone.dto.subscription.PlanResponse;
import com.example.lovable_clone.dto.subscription.SubscriptionResponse;
import com.example.lovable_clone.dto.subscription.UsageTodayResponse;
import com.example.lovable_clone.entity.UsageLog;
import com.example.lovable_clone.repository.UsageLogRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.SubscriptionService;
import com.example.lovable_clone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class UsageServiceimpl implements UsageService {


    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUssage(Long userId, int actualTokens) {
        LocalDate today=LocalDate.now();
        UsageLog todayLog=usageLogRepository.findByUserIdAndDate(userId,today).orElseGet(()->createNewDailyLog(userId,today));

        todayLog.setTokenUsed(todayLog.getTokenUsed()+actualTokens);
        usageLogRepository.save(todayLog);
    }

    @Override
    public void checkDailyTokenUsage() {
        Long userId=authUtil.getCurrentUserId();
        SubscriptionResponse subscriptionResponse=subscriptionService.getCurrentSubscription();
        PlanResponse plan=subscriptionResponse.plan();

        LocalDate today=LocalDate.now();
        UsageLog todayLog=usageLogRepository.findByUserIdAndDate(userId,today).orElseGet(()->createNewDailyLog(userId,today));
        if(plan.unlimitedAi())
        {
           return;
        }
        int currentUsage=todayLog.getTokenUsed();
        int limit=plan.maxTokenPerDay();
        if(currentUsage>=limit)
        {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,"Daily limit reached , upgrade now");
        }
    }

    private UsageLog createNewDailyLog(Long userId,LocalDate date)
    {
        UsageLog nearLog=UsageLog.builder()
                .userId(userId)
                .date(date)
                .tokenUsed(0)
                .build();
        return usageLogRepository.save(nearLog);
    }
}
