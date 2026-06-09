package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.subscription.*;
import com.example.lovable_clone.service.PaymentProcessor;
import com.example.lovable_clone.service.PlanService;
import com.example.lovable_clone.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PlanService planService;
    private final PaymentProcessor paymentProcessor;

    @GetMapping("/api/plans")
    public ResponseEntity<PlanResponse> getAllPlans()
    {
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription()
    {
        Long userId=1L;
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(@RequestBody CheckoutRequest request)
    {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(request));
    }
    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal()
    {
        Long userId=1L;
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }
}
