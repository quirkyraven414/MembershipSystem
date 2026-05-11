package com.membership.transition;

import java.util.ArrayList;
import java.util.List;

public class TransitionRulesEngine {
    private final List<TransitionRule> rules;

    public TransitionRulesEngine() {
        this.rules = new ArrayList<>();
        initializeRules();
    }

    private void initializeRules() {
        rules.add(new TransitionRule("SILVER", "GOLD", TransitionType.UPGRADE, 
            true, "NONE", "Upgrade allowed anytime"));
        rules.add(new TransitionRule("SILVER", "PLATINUM", TransitionType.UPGRADE, 
            true, "NONE", "Upgrade allowed anytime"));
        
        rules.add(new TransitionRule("GOLD", "PLATINUM", TransitionType.UPGRADE, 
            true, "NONE", "Upgrade allowed anytime"));
        rules.add(new TransitionRule("GOLD", "SILVER", TransitionType.DOWNGRADE, 
            true, "SUBSCRIPTION_END_ONLY", "Downgrade only at subscription end"));
        
        rules.add(new TransitionRule("PLATINUM", "GOLD", TransitionType.DOWNGRADE, 
            true, "SUBSCRIPTION_END_ONLY", "Downgrade only at subscription end"));
        rules.add(new TransitionRule("PLATINUM", "SILVER", TransitionType.DOWNGRADE, 
            false, "NOT_ALLOWED", "Direct downgrade not allowed, must go through GOLD"));
        
        rules.add(new TransitionRule("SILVER", null, TransitionType.CANCEL, 
            true, "ANYTIME", "Can cancel anytime"));
        rules.add(new TransitionRule("GOLD", null, TransitionType.CANCEL, 
            true, "SUBSCRIPTION_END_ONLY", "Can cancel only at subscription end"));
        rules.add(new TransitionRule("PLATINUM", null, TransitionType.CANCEL, 
            true, "SUBSCRIPTION_END_ONLY", "Can cancel only at subscription end"));
    }

    public TransitionResult validateTransition(String fromTier, String toTier, boolean isSubscriptionEnd) {
        return validateTransition(fromTier, toTier, isSubscriptionEnd, false);
    }

    public TransitionResult validateTransition(String fromTier, String toTier, 
                                              boolean isSubscriptionEnd, boolean hasAlreadyDowngraded) {
        if (toTier == null) {
            return validateCancellation(fromTier, isSubscriptionEnd);
        }

        if (fromTier.equals(toTier)) {
            return new TransitionResult(false, "Cannot transition to same tier", null);
        }

        TransitionRule rule = findRule(fromTier, toTier);
        
        if (rule == null) {
            return new TransitionResult(false, "No transition rule found", null);
        }

        if (!rule.isAllowed()) {
            return new TransitionResult(false, rule.getReason(), rule);
        }

        if (rule.getType() == TransitionType.DOWNGRADE && hasAlreadyDowngraded) {
            return new TransitionResult(false, 
                "Downgrade denied: Already downgraded once in this cycle", rule);
        }

        if (rule.getCondition().equals("SUBSCRIPTION_END_ONLY") && !isSubscriptionEnd) {
            return new TransitionResult(false, 
                "This transition is only allowed at subscription end", rule);
        }

        return new TransitionResult(true, rule.getReason(), rule);
    }

    private TransitionResult validateCancellation(String fromTier, boolean isSubscriptionEnd) {
        TransitionRule rule = findCancellationRule(fromTier);
        
        if (rule == null) {
            return new TransitionResult(false, "No cancellation rule found", null);
        }

        if (!rule.isAllowed()) {
            return new TransitionResult(false, rule.getReason(), rule);
        }

        if (rule.getCondition().equals("SUBSCRIPTION_END_ONLY") && !isSubscriptionEnd) {
            return new TransitionResult(false, 
                "Cancellation only allowed at subscription end", rule);
        }

        return new TransitionResult(true, rule.getReason(), rule);
    }

    private TransitionRule findRule(String fromTier, String toTier) {
        return rules.stream()
            .filter(r -> r.getFromTier().equals(fromTier) && 
                        toTier.equals(r.getToTier()))
            .findFirst()
            .orElse(null);
    }

    private TransitionRule findCancellationRule(String fromTier) {
        return rules.stream()
            .filter(r -> r.getFromTier().equals(fromTier) && 
                        r.getToTier() == null)
            .findFirst()
            .orElse(null);
    }

    public List<TransitionRule> getAllRules() {
        return new ArrayList<>(rules);
    }

    public void printRulesTable() {
        System.out.println("\n=== MEMBERSHIP TRANSITION RULES ===");
        System.out.println(String.format("%-12s %-12s %-12s %-10s %-30s %-40s", 
            "FROM", "TO", "TYPE", "ALLOWED", "CONDITION", "REASON"));
        System.out.println("-".repeat(120));
        
        for (TransitionRule rule : rules) {
            System.out.println(String.format("%-12s %-12s %-12s %-10s %-30s %-40s",
                rule.getFromTier(),
                rule.getToTier() == null ? "CANCEL" : rule.getToTier(),
                rule.getType(),
                rule.isAllowed() ? "YES" : "NO",
                rule.getCondition(),
                rule.getReason()));
        }
        System.out.println();
    }
}
