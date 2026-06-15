package com.example.lovable_clone.entity;

import com.example.lovable_clone.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(nullable = false,name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(nullable = false,name = "plan_id")
    Plan plan;
    String stripeSubscriptionId;// can be gateway subscriptionId==>platform agnostic

    Instant currentPeriodStart;
    Instant currentPeriodEnd;

    boolean cancelAtPeriodEnd=false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;

    @Enumerated(value = EnumType.STRING)
    SubscriptionStatus status;
}
