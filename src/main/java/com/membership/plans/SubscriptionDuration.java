package com.membership.plans;

public enum SubscriptionDuration {
    MONTHLY(1, 1.0),
    QUARTERLY(3, 0.9),
    HALF_YEARLY(6, 0.85),
    ANNUAL(12, 0.75);

    private final int months;
    private final double discountMultiplier;

    SubscriptionDuration(int months, double discountMultiplier) {
        this.months = months;
        this.discountMultiplier = discountMultiplier;
    }

    public double calculateCost(double baseMonthlyPrice) {
        return (baseMonthlyPrice * months) * discountMultiplier;
    }
}
