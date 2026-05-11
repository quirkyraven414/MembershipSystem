package com.membership.transition;

public class TransitionRule {
    private final String fromTier;
    private final String toTier;
    private final TransitionType type;
    private final boolean allowed;
    private final String condition;
    private final String reason;

    public TransitionRule(String fromTier, String toTier, TransitionType type, 
                         boolean allowed, String condition, String reason) {
        this.fromTier = fromTier;
        this.toTier = toTier;
        this.type = type;
        this.allowed = allowed;
        this.condition = condition;
        this.reason = reason;
    }

    public String getFromTier() { return fromTier; }
    public String getToTier() { return toTier; }
    public TransitionType getType() { return type; }
    public boolean isAllowed() { return allowed; }
    public String getCondition() { return condition; }
    public String getReason() { return reason; }
}
