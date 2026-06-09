package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.subscription.CheckoutRequest;
import com.example.lovable_clone.dto.subscription.CheckoutResponse;
import com.example.lovable_clone.dto.subscription.PortalResponse;
import com.example.lovable_clone.entity.Plan;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.repository.PlanRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.PaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    @Value("${client.url}")
    private String frontendUrl;

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan= (Plan) planRepository.findById(request.planId()).orElseThrow(()->new ResourceNotFoundException("Plan",request.planId().toString()));
        Long userId=authUtil.getCurrentUserId();

        SessionCreateParams params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceID()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder().setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder().setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE).build()).build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id",plan.getId().toString())
                .build();
        try {
            Session session = Session.create(params);
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }
}
