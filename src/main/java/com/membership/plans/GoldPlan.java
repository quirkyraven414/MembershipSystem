package com.membership.plans;

public class GoldPlan implements MembershipTier {
    public String getTierName() { return "GOLD"; }
    public double getBasePrice() { return 200.0; }
    public void showBenefits() { 
        System.out.println("- Free Delivery");
        System.out.println("- 10% off on all orders");
    }
}
