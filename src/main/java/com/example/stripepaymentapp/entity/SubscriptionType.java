package com.example.stripepaymentapp.entity;

import java.math.BigDecimal;

public enum SubscriptionType {
    BASIC("Basic", new BigDecimal("30.00"), "Perfect for individuals getting started"),
    PRO("Pro", new BigDecimal("50.00"), "Ideal for growing businesses and teams"),
    EXPERT("Expert", new BigDecimal("70.00"), "Advanced features for power users");

    private final String displayName;
    private final BigDecimal price;
    private final String description;

    SubscriptionType(String displayName, BigDecimal price, String description) {
        this.displayName = displayName;
        this.price = price;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public long getPriceInCents() {
        return price.multiply(new BigDecimal("100")).longValue();
    }

    public static SubscriptionType fromString(String value) {
        for (SubscriptionType type : SubscriptionType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid subscription type: " + value);
    }
}