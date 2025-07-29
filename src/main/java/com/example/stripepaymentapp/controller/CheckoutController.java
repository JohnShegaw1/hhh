package com.example.stripepaymentapp.controller;

import com.example.stripepaymentapp.entity.SubscriptionType;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {

    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable-key}")
    private String stripePublishableKey;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        model.addAttribute("subscriptionTypes", SubscriptionType.values());
        return "checkout";
    }

    @PostMapping("/create-payment-intent")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @RequestParam String email,
            @RequestParam String subscriptionType) {
        
        try {
            Stripe.apiKey = stripeSecretKey;
            
            // Get subscription type and amount
            SubscriptionType subType = SubscriptionType.fromString(subscriptionType);
            long amountInCents = subType.getPriceInCents();
            
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .putMetadata("email", email)
                    .putMetadata("subscription_type", subscriptionType)
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            
            return ResponseEntity.ok(response);
            
        } catch (StripeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create payment intent: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid subscription type: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}