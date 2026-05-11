package com.membership.plans;

public class SilverPlan implements MembershipTier {
    public String getTierName() { return "SILVER"; }
    public double getBasePrice() { return 100.0; }
    public void showBenefits() { System.out.println("- 5% off on all orders"); }
}
