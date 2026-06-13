package com.example.lovable_clone.service.Impl;

import com.example.lovable_clone.dto.subscription.CheckoutRequest;
import com.example.lovable_clone.dto.subscription.CheckoutResponse;
import com.example.lovable_clone.dto.subscription.PortalResponse;
import com.example.lovable_clone.entity.Plan;
import com.example.lovable_clone.entity.User;
import com.example.lovable_clone.enums.SubscriptionStatus;
import com.example.lovable_clone.error.ResourceNotFoundException;
import com.example.lovable_clone.repository.PlanRepository;
import com.example.lovable_clone.repository.SubscriptionRepository;
import com.example.lovable_clone.repository.UserRepository;
import com.example.lovable_clone.security.AuthUtil;
import com.example.lovable_clone.service.PaymentProcessor;
import com.example.lovable_clone.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentProcessor implements PaymentProcessor {

    private final AuthUtil authUtil;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    @Value("${client.url}")
    private String frontendUrl;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;


    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request) {

        Plan plan= (Plan) planRepository.findById(request.planId()).orElseThrow(()->new ResourceNotFoundException("Plan",request.planId().toString()));
        Long userId=authUtil.getCurrentUserId();
        User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user",userId.toString()));
        var params = SessionCreateParams.builder()
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setPrice(plan.getStripePriceID()).setQuantity(1L).build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSubscriptionData(
                        new SessionCreateParams.SubscriptionData.Builder().setBillingMode(SessionCreateParams.SubscriptionData.BillingMode.builder().setType(SessionCreateParams.SubscriptionData.BillingMode.Type.FLEXIBLE).build()).putMetadata("user_id", userId.toString())
                                .putMetadata("plan_id", plan.getId().toString()).build()
                )
                .setSuccessUrl(frontendUrl + "/success.html?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel.html")
                .putMetadata("user_id",userId.toString())
                .putMetadata("plan_id",plan.getId().toString());

        try {
            String stripeCustomerId= user.getStripeCustomerId();
            if(stripeCustomerId==null || stripeCustomerId.isEmpty())
            {
                params.setCustomerEmail(user.getUsername());
            }
            else {
                params.setCustomer(stripeCustomerId);
            }
            Session session = Session.create(params.build());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata) {
             log.debug("Handling stripe event {}",type);
             switch (type){
                 case "customer.subscription.created" ->handleSubscriptionCreated((Subscription) stripeObject,metadata);//when subscription created
                 case "checkout.session.completed" ->handleCheckoutSessionCompleted((Session) stripeObject,metadata);
                 case "customer.subscription.updated" -> handleCustomerSubscriptionUpdated((Subscription) stripeObject);// when user cancels, upgrades or any updates
                 case "customer.subscription.deleted" -> handleCustomerSubscriptionDeleted((Subscription) stripeObject);// when subscription ends,revoke the access
                 case "invoice.payment_succeeded"->handleInvoicePaid((Invoice) stripeObject); // when invoice is paid
                 case "invoice.payment_failed"->handleInvoicePaymentFailed((Invoice) stripeObject); //when invoice is not paid , mark as PAST_DUE
                 default -> log.debug("Ignoring events {}",type);
             }
    }

    private void handleInvoicePaid(Invoice invoice) {

        String subId=extractSubscriptionId(invoice);
        if(subId==null)
        {
            return;
        }
        try {
            Subscription subscription=Subscription.retrieve(subId);
            var item  = subscription.getItems().getData().get(0);
            Instant periodStart=toInstant(item.getCurrentPeriodStart());
            Instant periodEnd=toInstant(item.getCurrentPeriodEnd());
            subscriptionService.renewSubscriptionPeriod(subId,periodStart,periodEnd);

        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    private void handleInvoicePaymentFailed(Invoice invoice) {

        String subId=extractSubscriptionId(invoice);
        if(subId==null)
        {
            return;
        }
        subscriptionService.markSubscriptionPastDue(subId);
        
    }

    private void handleCustomerSubscriptionDeleted(Subscription subscription) {
        if(subscription==null)
        {
            log.error("Subscription Object was null inside handleCustomerSubscriptionDeleted");
            return;
        }

        subscriptionService.cancelSubscription(subscription.getId());

    }

    private void handleCustomerSubscriptionUpdated(Subscription subscription) {
        if(subscription==null)
        {
            log.error("subscription object is null inside handleCustomerSubscriptionUpdated");
            return;
        }
        SubscriptionStatus status=mapStripeStatusToEnum(subscription.getStatus());
        if(status==null)
        {
            log.warn("Unknown error '{}' for Subscription {}",subscription.getStatus(),subscription.getId());

        }
        SubscriptionItem subscriptionItem=subscription.getItems().getData().get(0);
        Instant periodStart=toInstant(subscriptionItem.getCurrentPeriodStart());
        Instant periodEnd=toInstant(subscriptionItem.getCurrentPeriodEnd());

        Long planId=resolvePlanId(subscriptionItem.getPrice());
        subscriptionService.updateSubscription(subscription.getId(),status,periodStart,periodEnd,planId,subscription.getCancelAtPeriodEnd());

    }

    private Long resolvePlanId(Price price) {
        if(price==null || price.getId()==null) return null;
        return planRepository.findByStripePriceID(price.getId()).map(Plan::getId).orElse(null);
    }

    private Instant toInstant(Long epoch) {
        return epoch!=null ? Instant.ofEpochSecond(epoch) : null;
    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
        return switch (status){
            case "active" -> SubscriptionStatus.ACTIVE;
            case "trialing" -> SubscriptionStatus.TRAILING;
            case "past_due" ,"unpaid" , "paused" , "incomplete_expired" -> SubscriptionStatus.PAST_DUE;
            case "canceled" -> SubscriptionStatus.CANCELLED;
            case "incomplete" -> SubscriptionStatus.INCOMPLETE;
            default -> {
                log.warn("Unmapped Stripe status {}",status);
                yield null;
            }
        };
    }

    private void handleSubscriptionCreated(Subscription subscription,Map<String,String> metaData) {
        if(subscription==null)
        {
            log.error("subscription object is null inside handleCheckoutSessionCompleted");
            return;
        }
            Long userId= Long.parseLong(metaData.get("user_id"));
            Long planId= Long.parseLong(metaData.get("plan_id"));
            String subscriptionId=subscription.getId();
            String customerId=subscription.getCustomer();
            User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user",userId.toString()));
            if(user.getStripeCustomerId()==null)
            {
                user.setStripeCustomerId(customerId);
                userRepository.save(user);
            }
            subscriptionService.activateSubscription(userId,planId,subscriptionId,customerId);

    }
    private void handleCheckoutSessionCompleted(Session session,Map<String,String> metaData) {
        if(session==null)
        {
            log.error("subscription object is null inside handleCheckoutSessionCompleted");
            return;
        }
        Long userId= Long.parseLong(metaData.get("user_id"));
        String customerId=session.getCustomer();
        User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user",userId.toString()));
        if(user.getStripeCustomerId()==null)
        {
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }

    }
    private String extractSubscriptionId(Invoice invoice)
    {
        var parent = invoice.getParent();
        if(parent==null)
        {
            return null;
        }

        var subDetails=parent.getSubscriptionDetails();
        if(subDetails==null)return null;

        return subDetails.getSubscription();
    }
}
