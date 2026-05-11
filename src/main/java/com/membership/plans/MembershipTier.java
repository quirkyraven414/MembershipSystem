package com.membership.plans;

public interface MembershipTier {
    String getTierName();
    double getBasePrice();
    void showBenefits();
}
