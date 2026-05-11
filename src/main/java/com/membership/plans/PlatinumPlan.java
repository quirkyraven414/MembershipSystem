package com.membership.plans;

public class PlatinumPlan implements MembershipTier {
    public String getTierName() { return "PLATINUM"; }
    public double getBasePrice() { return 300.0; }
    public void showBenefits() { 
        System.out.println("- Free Delivery");
        System.out.println("- 15% off on all orders");
        System.out.println("- Priority Support");
    }
}
