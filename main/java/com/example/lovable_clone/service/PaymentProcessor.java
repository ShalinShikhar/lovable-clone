package com.example.lovable_clone.service;

import com.example.lovable_clone.dto.subscription.CheckoutRequest;
import com.example.lovable_clone.dto.subscription.CheckoutResponse;
import com.example.lovable_clone.dto.subscription.PortalResponse;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();
}
