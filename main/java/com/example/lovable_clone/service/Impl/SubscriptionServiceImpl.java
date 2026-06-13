package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.subscription.CheckoutRequest;
import com.example.lovable_clone.dto.subscription.CheckoutResponse;
import com.example.lovable_clone.dto.subscription.PortalResponse;
import com.example.lovable_clone.dto.subscription.SubscriptionResponse;
import com.example.lovable_clone.entity.Plan;
import com.example.lovable_clone.entity.Subscription;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.enums.SubscriptionStatus;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.mapper.SubscriptionMapper;
import com.example.lovable_clone.repository.PlanRepository;
import com.example.lovable_clone.repository.SubscriptionRepository;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level=AccessLevel.PRIVATE)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription= getSubscription(gatewaySubscriptionId);
        Instant newStart=periodStart!=null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodStart(periodEnd);

        if(subscription.getStatus()==SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE)
        {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }
        subscriptionRepository.save(subscription);

    }



    @Override
    public SubscriptionResponse getCurrentSubscription() {

        Long userId=authUtil.getCurrentUserId();
        var currentSubscription= subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(SubscriptionStatus.ACTIVE,SubscriptionStatus.PAST_DUE,SubscriptionStatus.TRAILING)).orElse(
                Subscription.builder().build());

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
            boolean exists=subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
            if(exists)
            {
                return;
            }
            User user=getUser(userId);
            Plan plan=getPlan(planId);
            Subscription subscription=Subscription.builder()
                    .user(user)
                    .plan(plan)
                    .stripeSubscriptionId(subscriptionId)
                    .status(SubscriptionStatus.INCOMPLETE)
                    .build();
            subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Long planId, Boolean cancelAtPeriodEnd) {

    }

    @Override
    public void cancelSubscription(String id) {

    }

    @Override
    public void markSubscriptionPastDue(String subId) {

    }

    //utility Methods

    private User getUser(Long userId)
    {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user",userId.toString()));
    }
    private Plan getPlan(Long planId)
    {
        return planRepository.findById(planId).orElseThrow(()->new ResourceNotFoundException("plan",planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
