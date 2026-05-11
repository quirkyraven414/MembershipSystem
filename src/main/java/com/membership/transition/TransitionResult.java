package com.membership.transition;

public class TransitionResult {
    private final boolean allowed;
    private final String message;
    private final TransitionRule rule;

    public TransitionResult(boolean allowed, String message, TransitionRule rule) {
        this.allowed = allowed;
        this.message = message;
        this.rule = rule;
    }

    public boolean isAllowed() { return allowed; }
    public String getMessage() { return message; }
    public TransitionRule getRule() { return rule; }
}
