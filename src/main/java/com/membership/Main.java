package com.membership;

import com.membership.factory.MembershipFactory;
import com.membership.plans.*;

public class Main {
    public static void main(String[] args) {
        String selectedTier = "GOLD";
        SubscriptionDuration duration = SubscriptionDuration.ANNUAL;

        MembershipTier myPlan = MembershipFactory.getTier(selectedTier);

        double finalPrice = duration.calculateCost(myPlan.getBasePrice());

        System.out.println("--- Subscription Details ---");
        System.out.println("Tier: " + myPlan.getTierName());
        System.out.println("Duration: " + duration);
        System.out.println("Total Price: ₹" + finalPrice);
        myPlan.showBenefits();
    }
}
