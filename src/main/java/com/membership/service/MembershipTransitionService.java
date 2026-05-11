package com.membership.service;

import com.membership.transition.TransitionResult;
import com.membership.transition.TransitionRule;
import com.membership.transition.TransitionRulesEngine;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipTransitionService {
    private final TransitionRulesEngine rulesEngine;

    public MembershipTransitionService() {
        this.rulesEngine = new TransitionRulesEngine();
    }

    public TransitionResult requestUpgrade(String currentTier, String targetTier) {
        return rulesEngine.validateTransition(currentTier, targetTier, false);
    }

    public TransitionResult requestDowngrade(String currentTier, String targetTier, boolean isSubscriptionEnd) {
        return rulesEngine.validateTransition(currentTier, targetTier, isSubscriptionEnd);
    }

    public TransitionResult requestCancellation(String currentTier, boolean isSubscriptionEnd) {
        return rulesEngine.validateTransition(currentTier, null, isSubscriptionEnd);
    }

    public void displayRulesTable() {
        rulesEngine.printRulesTable();
    }

    public List<TransitionRule> getAllRules() {
        return rulesEngine.getAllRules();
    }
}
