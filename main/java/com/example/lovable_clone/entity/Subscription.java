package com.example.lovable_clone.entity;

import com.example.lovable_clone.enums.SubscriptionStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Subscription {

    Long id;
    User user;
    Plan plan;
    String stripeCustomerId;
    String stripeSubscriptionId;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    boolean cancelAtPeriodEnd;

    Instant createdAt;
    Instant updatedAt;

    SubscriptionStatus status;
}
