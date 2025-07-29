package com.example.stripepaymentapp.controller;

import com.example.stripepaymentapp.entity.Payment;
import com.example.stripepaymentapp.entity.SubscriptionType;
import com.example.stripepaymentapp.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            logger.error("Invalid signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            default:
                logger.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook received");
    }

    private void handlePaymentIntentSucceeded(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            if (paymentIntent != null) {
                // Extract email and subscription type from metadata
                String email = paymentIntent.getMetadata().get("email");
                String subscriptionTypeStr = paymentIntent.getMetadata().get("subscription_type");
                
                // Convert amount from cents to dollars
                BigDecimal amount = new BigDecimal(paymentIntent.getAmount()).divide(new BigDecimal("100"));
                
                // Get subscription type
                SubscriptionType subscriptionType = SubscriptionType.fromString(subscriptionTypeStr);
                
                // Create Payment entity
                Payment payment = new Payment();
                payment.setStripePaymentId(paymentIntent.getId());
                payment.setStatus(paymentIntent.getStatus());
                payment.setAmount(amount);
                payment.setCurrency(paymentIntent.getCurrency());
                payment.setEmail(email);
                payment.setSubscriptionType(subscriptionType);
                payment.setStartDate(LocalDate.now());
                payment.setEndDate(LocalDate.now().plusDays(30)); // +30 days
                payment.setCreatedAt(LocalDateTime.now());

                // Save to database
                paymentRepository.save(payment);
                
                logger.info("Payment saved successfully: {}", payment);
            }
        } catch (Exception e) {
            logger.error("Error processing payment_intent.succeeded event: {}", e.getMessage(), e);
        }
    }
}