package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.subscription.SubscriptionResponse;
import com.example.lovable_clone.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Long planId, Boolean cancelAtPeriodEnd);

    void cancelSubscription(String id);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
