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
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level=AccessLevel.PRIVATE)
@Slf4j
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
    @Transactional
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Long planId, Boolean cancelAtPeriodEnd) {

        Subscription subscription=getSubscription(subscriptionId);
        boolean subscriptionHasBeenUpdated=false;

        if(status!=null  && subscription.getStatus()!=status)
        {
            subscription.setStatus(status);
            subscriptionHasBeenUpdated=true;
        }
        if(periodStart!=null && !periodStart.equals(subscription.getCurrentPeriodStart()))
        {
            subscription.setCurrentPeriodStart(periodStart);
            subscriptionHasBeenUpdated=true;
        }
        if(periodEnd!=null && !periodEnd.equals(subscription.getCurrentPeriodEnd()))
        {
            subscription.setCurrentPeriodStart(periodEnd);
            subscriptionHasBeenUpdated=true;
        }

        if(cancelAtPeriodEnd!=null && cancelAtPeriodEnd!=subscription.isCancelAtPeriodEnd())
        {
             subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            subscriptionHasBeenUpdated=true;
        }

        if(planId != null && subscription.getPlan().getId()!=planId)
        {
            Plan plan=getPlan(planId);
            subscription.setPlan(plan);
            subscriptionHasBeenUpdated=true;
        }

        if(subscriptionHasBeenUpdated)
        {
            log.debug("Subscription has been updated {}",subscriptionId);
            subscriptionRepository.save(subscription);
        }

    }

    @Override
    public void cancelSubscription(String id) {
        Subscription subscription=getSubscription(id);
        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);

    }

    @Override
    public void markSubscriptionPastDue(String subId) {

        Subscription subscription=getSubscription(subId);
        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE)
        {
            log.debug("Subscription is already past due id : {}",subId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);




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
