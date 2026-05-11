package com.membership.factory;

import com.membership.plans.*;

public class MembershipFactory {
    public static MembershipTier getTier(String tierType) {
        if (tierType == null) return null;
        
        return switch (tierType.toUpperCase()) {
            case "SILVER" -> new SilverPlan();
            case "GOLD" -> new GoldPlan();
            case "PLATINUM" -> new PlatinumPlan();
            default -> throw new IllegalArgumentException("Unknown tier: " + tierType);
        };
    }
}
