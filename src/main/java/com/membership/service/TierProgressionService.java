package com.membership.service;

import com.membership.model.Membership;
import com.membership.model.TierProgressionRule;
import com.membership.model.User;
import com.membership.repository.MembershipRepository;
import com.membership.repository.OrderRepository;
import com.membership.repository.TierProgressionRuleRepository;
import com.membership.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TierProgressionService {
    private final TierProgressionRuleRepository ruleRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    public TierProgressionService(TierProgressionRuleRepository ruleRepository,
                                 OrderRepository orderRepository,
                                 UserRepository userRepository,
                                 MembershipRepository membershipRepository) {
        this.ruleRepository = ruleRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
    }

    public TierEvaluationResult evaluateUserTier(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Membership> currentMembership = membershipRepository.findActiveByUserId(userId);
        String currentTier = currentMembership.map(Membership::getTier).orElse("NONE");

        List<TierProgressionRule> rules = ruleRepository.findByActiveTrueOrderByPriorityAsc();
        
        String qualifiedTier = currentTier;
        List<String> matchedRules = new ArrayList<>();
        
        for (TierProgressionRule rule : rules) {
            if (evaluateRule(rule, user, userId)) {
                if (isTierHigher(rule.getTargetTier(), qualifiedTier)) {
                    qualifiedTier = rule.getTargetTier();
                    matchedRules.add(rule.getRuleName());
                }
            }
        }

        boolean upgradeNeeded = !qualifiedTier.equals(currentTier) && 
                               isTierHigher(qualifiedTier, currentTier);

        return new TierEvaluationResult(
            userId,
            currentTier,
            qualifiedTier,
            upgradeNeeded,
            matchedRules
        );
    }

    private boolean evaluateRule(TierProgressionRule rule, User user, Long userId) {
        switch (rule.getCriteriaType()) {
            case "ORDER_COUNT":
                return evaluateOrderCount(rule, userId);
            case "ORDER_VALUE":
                return evaluateOrderValue(rule, userId);
            case "COHORT":
                return evaluateCohort(rule, user);
            default:
                return false;
        }
    }

    private boolean evaluateOrderCount(TierProgressionRule rule, Long userId) {
        Long count;
        
        if ("CURRENT_MONTH".equals(rule.getTimeWindow())) {
            LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
            count = orderRepository.countByUserIdAndDateAfter(userId, startOfMonth);
        } else if ("LAST_30_DAYS".equals(rule.getTimeWindow())) {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            count = orderRepository.countByUserIdAndDateAfter(userId, thirtyDaysAgo);
        } else {
            count = orderRepository.countByUserId(userId);
        }

        return compareValues(count.doubleValue(), rule.getOperator(), rule.getThresholdValue());
    }

    private boolean evaluateOrderValue(TierProgressionRule rule, Long userId) {
        Double totalValue;
        
        if ("CURRENT_MONTH".equals(rule.getTimeWindow())) {
            LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
            totalValue = orderRepository.sumValueByUserIdAndDateAfter(userId, startOfMonth);
        } else if ("LAST_30_DAYS".equals(rule.getTimeWindow())) {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            totalValue = orderRepository.sumValueByUserIdAndDateAfter(userId, thirtyDaysAgo);
        } else {
            totalValue = orderRepository.sumTotalValueByUserId(userId);
        }

        return compareValues(totalValue, rule.getOperator(), rule.getThresholdValue());
    }

    private boolean evaluateCohort(TierProgressionRule rule, User user) {
        if (user.getCohort() == null || rule.getCohortName() == null) {
            return false;
        }
        return user.getCohort().equals(rule.getCohortName());
    }

    private boolean compareValues(Double actualValue, String operator, Double thresholdValue) {
        if (actualValue == null || thresholdValue == null) {
            return false;
        }

        return switch (operator) {
            case ">=" -> actualValue >= thresholdValue;
            case ">" -> actualValue > thresholdValue;
            case "=" -> actualValue.equals(thresholdValue);
            case "<" -> actualValue < thresholdValue;
            case "<=" -> actualValue <= thresholdValue;
            default -> false;
        };
    }

    private boolean isTierHigher(String tier1, String tier2) {
        int rank1 = getTierRank(tier1);
        int rank2 = getTierRank(tier2);
        return rank1 > rank2;
    }

    private int getTierRank(String tier) {
        return switch (tier) {
            case "PLATINUM" -> 3;
            case "GOLD" -> 2;
            case "SILVER" -> 1;
            default -> 0;
        };
    }

    public TierProgressionRule createRule(TierProgressionRule rule) {
        return ruleRepository.save(rule);
    }

    public TierProgressionRule updateRule(Long id, TierProgressionRule rule) {
        rule.setId(id);
        rule.setUpdatedAt(LocalDateTime.now());
        return ruleRepository.save(rule);
    }

    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    public List<TierProgressionRule> getAllRules() {
        return ruleRepository.findAll();
    }

    public TierProgressionRule getRuleById(Long id) {
        return ruleRepository.findById(id).orElse(null);
    }

    public static class TierEvaluationResult {
        private final Long userId;
        private final String currentTier;
        private final String qualifiedTier;
        private final boolean upgradeNeeded;
        private final List<String> matchedRules;

        public TierEvaluationResult(Long userId, String currentTier, String qualifiedTier, 
                                   boolean upgradeNeeded, List<String> matchedRules) {
            this.userId = userId;
            this.currentTier = currentTier;
            this.qualifiedTier = qualifiedTier;
            this.upgradeNeeded = upgradeNeeded;
            this.matchedRules = matchedRules;
        }

        public Long getUserId() { return userId; }
        public String getCurrentTier() { return currentTier; }
        public String getQualifiedTier() { return qualifiedTier; }
        public boolean isUpgradeNeeded() { return upgradeNeeded; }
        public List<String> getMatchedRules() { return matchedRules; }
    }
}
