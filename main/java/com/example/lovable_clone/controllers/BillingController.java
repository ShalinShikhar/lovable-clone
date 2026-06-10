package com.example.lovable_clone.controllers;

import com.example.lovable_clone.dto.subscription.*;
import com.example.lovable_clone.service.PaymentProcessor;
import com.example.lovable_clone.service.PlanService;
import com.example.lovable_clone.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BillingController {

    private final SubscriptionService subscriptionService;
    private final PlanService planService;
    private final PaymentProcessor paymentProcessor;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
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

    @PostMapping("/webhooks/payments")
    public ResponseEntity<String> handlePaymentWebhooks(@RequestBody String payload, @RequestHeader("Stripe-Signature")String signature)
    {
        try {
            Event event= Webhook.constructEvent(payload,signature,webhookSecret);

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }



    }
}
